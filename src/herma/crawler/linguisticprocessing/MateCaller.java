/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import herma.crawler.util.RunUtil;


public class MateCaller implements ParserCaller {
	
	private final String pJavaCommand;
	
	private final String pJavaMemory;
	
	private final Path pMateJar;
	private final String pMateClass;
	private final Path pMateModel;

	private final Semaphore pParserRunSemaphore;
	private final HashSet<WaitForParserThread> pWaitingThreads;
	private final AtomicBoolean pWaiting;
	
	private final Predicate<ArrayList<?>> pSentenceLengthPredicate;
	
	public MateCaller(final String javaCommand, final String javaMemory, final Path mateJar, final String mateClass, final Path mateModel, final int maxConcurrentParserInvocations, final int maxSentenceLength) {
		pJavaCommand = javaCommand;
		
		pJavaMemory = "-Xmx" + javaMemory;
		
		pMateJar = mateJar;
		pMateClass = mateClass;
		pMateModel = mateModel;
		
		pParserRunSemaphore = new Semaphore(maxConcurrentParserInvocations, true);
		pWaitingThreads = new HashSet<>();
		pWaiting = new AtomicBoolean(true);
		
		pSentenceLengthPredicate = l -> l.size() > maxSentenceLength;
	}
	
	@Override
	public boolean callParser(final Path morphFile, final Path alternativeInputFile, final Path outputFile) {
		final boolean longSentencesRemoved = removeLongSentences(morphFile, alternativeInputFile);
		final ProcessBuilder pb = new ProcessBuilder(pJavaCommand, pJavaMemory, "-Dfile.encoding=UTF-8", "-cp", pMateJar.toString(), pMateClass, "-model", pMateModel.toString(), "-test", (longSentencesRemoved ? alternativeInputFile : morphFile).toString(), "-out", outputFile.toString());

		pb.redirectInput(Redirect.PIPE);
		pb.redirectOutput(Redirect.PIPE);
		pb.redirectError(Redirect.PIPE);
		
		pParserRunSemaphore.acquireUninterruptibly();
		try {
			final Process process;
			try {
				process = pb.start();
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
			RunUtil.consumeStreamsToNowhere(process);
			
			final WaitForParserThread waitThread = new WaitForParserThread(process);
			
			synchronized (pWaitingThreads) {
				pWaitingThreads.add(waitThread);
			}
			
			waitThread.start();
		} catch (final Exception e) {
			pParserRunSemaphore.release();
			throw e;
		}
		
		return longSentencesRemoved;
	}
	
	public void stopWaiting() {
		pWaiting.set(false);
		synchronized (pWaitingThreads) {
			for (final WaitForParserThread thread : pWaitingThreads)
				thread.interrupt();
		}
	}
	
	private boolean removeLongSentences(final Path moprhFile, final Path alternativeInputFile) {
		try {
			final ArrayList<ArrayList<String>> sentences = readSentences(moprhFile);
			if (sentences.removeIf(pSentenceLengthPredicate)) {
				writeSentences(alternativeInputFile, sentences);
				return true;
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		return false;
	}
	
	private ArrayList<ArrayList<String>> readSentences(final Path inputFile) throws IOException {
		final ArrayList<ArrayList<String>> result = new ArrayList<>();
		ArrayList<String> currentSentence = null;
		try (final BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
			while (true) {
				final String line = reader.readLine();
				if (line == null)
					break;
				if ("".equals(line)) {
					currentSentence = null;
					continue;
				}
				if (currentSentence == null) {
					currentSentence = new ArrayList<>();
					result.add(currentSentence);
				}
				currentSentence.add(line);
			}
		}
		return result;
	}
	
	private void writeSentences(final Path outputFile, final ArrayList<ArrayList<String>> sentences) throws IOException {
		boolean first = true;
		try (final BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
			for (final ArrayList<String> sentence : sentences) {
				if (first)
					first = false;
				else
					writer.write('\n');
				for (final String line : sentence) {
					writer.write(line);
					writer.write('\n');
				}
			}
			writer.flush();
		}
	}
	
	private class WaitForParserThread extends Thread {
		
		private final Process pProcess;
		
		public WaitForParserThread(final Process process) {
			pProcess = process;
		}
		
		@Override
		public void run() {
			try {
				while (pWaiting.get()) {
					try {
						pProcess.waitFor();
					} catch (final InterruptedException e) {
						// test whether waiting should be continued;
						// leave loop if not
						continue;
					}
					// process has terminated, stop waiting
					break;
				}
			} finally {
				pParserRunSemaphore.release();
			}
			synchronized (pWaitingThreads) {
				pWaitingThreads.remove(this);
			}
		}
		
	}
	
}
