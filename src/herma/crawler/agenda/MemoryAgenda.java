/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.agenda;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import herma.crawler.Restager;

public class MemoryAgenda implements AgendaIn, AgendaOut, Restager {
	
	private final HashSet<String> pVisitedUrls = new HashSet<>();
	private final HashSet<String> pOverriddenUrls = new HashSet<>();
	private final HashMap<String, BigInteger> pResultCountByHost = new HashMap<>(); // TODO: hier falsch angesiedelt?
	private final ArrayDeque<AgendaItem> pQueue = new ArrayDeque<>();
	private final AtomicInteger pPendingCount = new AtomicInteger(0);
	
	private boolean pAbort = false;
	
	@Override
	public void incrementPendingCount() {
		pPendingCount.incrementAndGet();
	}
	
	@Override
	public void elementProcessed() {
		decrementPendingCountInternal();
	}

	@Override
	public void decrementPendingCount() {
		decrementPendingCountInternal();
	}

	private void decrementPendingCountInternal() {
		if (pPendingCount.decrementAndGet() == 0) {
			synchronized (pQueue) {
				pQueue.notifyAll();
			}
		}
	}
	
	public void abort() {
		synchronized (pQueue) {
			pAbort = true;
			pQueue.notifyAll();
		}
	}

	@Override
	public void add(final String url, final String host, final long depth) {
		synchronized (pQueue) {
			if (pVisitedUrls.add(url)) {
				pQueue.addLast(new AgendaItem(url, depth));
				pQueue.notifyAll();
			}
		}
		registerHost(host);
	}
	
	@Override
	public void restage(final String url, final String host, final long depth) {
		synchronized (pQueue) {
			if (pVisitedUrls.add(url) || pOverriddenUrls.add(url)) {
				pQueue.addFirst(new AgendaItem(url, depth));
				pQueue.notifyAll();
			}
		}
		registerHost(host);
	}

	private void registerHost(final String host) {
		synchronized (pResultCountByHost) {
			pResultCountByHost.putIfAbsent(host, BigInteger.ZERO);
		}
	}

	@Override
	public AgendaItem getNext() throws InterruptedException {
		synchronized (pQueue) {
			while (true) {
				if (pAbort)
					return null;
				{
					final AgendaItem next = pQueue.pollFirst();
					if ((next != null) && (!pOverriddenUrls.contains(next.getUrl()))) {
						pPendingCount.incrementAndGet();
						return next;
					}
				}
				if (pPendingCount.get() == 0)
					return null;
				pQueue.wait();
			}
		}
	}
	
	@Override
	public void relevantResultFoundFor(final String host) {
		synchronized (pResultCountByHost) {
			pResultCountByHost.put(host, BigInteger.ONE.add(pResultCountByHost.getOrDefault(host, BigInteger.ZERO)));
		}
	}
	
	@Override
	public void processingFinished(final String url) {
		// wird in dieser Implementation nicht berücksichtigt
	}
	
	public void save(final Path queueFile, final Path visitedUrlsFile, final Path resultCountByHostFile) {
		try {
			synchronized (pQueue) {
				writeQueue(queueFile);
				writeVisitedUrls(visitedUrlsFile);
			}
			
			synchronized (pResultCountByHost) {
				writeResultCounts(resultCountByHostFile);
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void writeQueue(final Path queueFile) throws IOException {
		final Path queueBackup = backupFile(queueFile);
		try (final BufferedWriter writer = Files.newBufferedWriter(queueFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			for (final AgendaItem item : pQueue) {
				final String url = item.getUrl();
				if (pOverriddenUrls.contains(url))
					continue;
				writer.write(url);
				writer.write('\t');
				writer.write(Long.toString(item.getDepth()));
				writer.write('\n');
			}
			writer.flush();
		}
		Files.deleteIfExists(queueBackup);
	}

	private void writeVisitedUrls(final Path visitedUrlsFile) throws IOException {
		final Path visitedBackup = backupFile(visitedUrlsFile);
		Files.write(visitedUrlsFile, pVisitedUrls, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		Files.deleteIfExists(visitedBackup);
	}

	private void writeResultCounts(final Path resultCountByHostFile) throws IOException {
		final Path resultCountsBackup = backupFile(resultCountByHostFile);
		try (final BufferedWriter writer = Files.newBufferedWriter(resultCountByHostFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			for (final Entry<String, BigInteger> entry : pResultCountByHost.entrySet()) {
				final String host = entry.getKey();
				if (host != null)
					writer.write(host);
				writer.write('\t');
				writer.write(entry.getValue().toString());
				writer.write('\n');
			}
			writer.flush();
		}
		Files.deleteIfExists(resultCountsBackup);
	}
	
	private static Path backupFile(final Path file) throws IOException {
		final Path temp = Files.createTempFile(file.getParent(), file.getFileName().toString(), "");
		try {
			try {
				Files.move(file, temp, StandardCopyOption.ATOMIC_MOVE);
			} catch (final NoSuchFileException e) {
				// Datei nicht vorhanden; nichts zu kopieren
			} catch (final IOException e) {
				Files.move(file, temp);
			}
		} catch (final Exception e) {
			Files.deleteIfExists(temp);
			throw e;
		}
		return temp;
	}
	
}
