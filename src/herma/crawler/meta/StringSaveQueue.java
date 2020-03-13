/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.meta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;

import herma.crawler.util.Roller;

public class StringSaveQueue implements MetaInformationStringCollection {

	private static final int MAX_LINES = 10000;
	
	private final Path pFile;
	private final ArrayDeque<String> pChangesQueue;
	private boolean pRunning;
	
	public StringSaveQueue(final Path file) {
		pFile = file;
		pChangesQueue = new ArrayDeque<>();
		pRunning = true;
		
		(new SaveThread()).start();
	}
	
	@Override
	public boolean add(final String str) {
		synchronized (pChangesQueue) {
			pChangesQueue.add(str);
			pChangesQueue.notifyAll();
		}
		return true;
	}
	
	public void stop() {
		synchronized (pChangesQueue) {
			pRunning = false;
			pChangesQueue.notifyAll();
		}
	}
	
	private class SaveThread extends Thread {
		
		@Override
		public void run() {
			final ArrayList<String> queueCopy = new ArrayList<>();
			long pWrittenLines = 0L;
			while (true) {
				synchronized (pChangesQueue) {
					while (pChangesQueue.size() > 0)
						queueCopy.add(pChangesQueue.poll());
					if (queueCopy.isEmpty()) {
						if (pRunning) {
							try {
								pChangesQueue.wait();
							} catch (final InterruptedException e) {
							}
							continue;
						}
						break;
					}
				}
				if (pWrittenLines > MAX_LINES) {
					try {
						Roller.roll(pFile);
					} catch (final IOException e) {
						throw new UncheckedIOException(e);
					}
					pWrittenLines = 0L;
				}
				try (final BufferedWriter writer = Files.newBufferedWriter(pFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
					for (final String item : queueCopy) {
						writer.write(item);
						writer.write('\n');
					}
					writer.flush();
				} catch (final IOException e) {
					throw new UncheckedIOException(e);
				}
				pWrittenLines += queueCopy.size();
				queueCopy.clear();
			}
		}
		
	}
	
}
