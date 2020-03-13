/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.pdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import herma.crawler.util.RunUtil;

public class PdfTitleExtractor {
	
	private final Path pXpdfPdfInfoPath;
	
	public PdfTitleExtractor(final Path xpdfPdfInfoPath) {
		pXpdfPdfInfoPath = xpdfPdfInfoPath;
	}
	
	public String extractTitle(final Path pdfFile) throws IOException {
		final ProcessBuilder pb = new ProcessBuilder(pXpdfPdfInfoPath.toString(), "-enc", "UTF-8", pdfFile.toString());
		
		pb.redirectInput(Redirect.PIPE);
		pb.redirectOutput(Redirect.PIPE);
		pb.redirectError(Redirect.PIPE);
		
		final Process process = pb.start();
		
		RunUtil.consumeErrorStreamToNowhere(process);
		
		try (final InputStream stdout = process.getInputStream()) {
			final OutputReaderThread outputReaderThread = new OutputReaderThread(stdout);
			outputReaderThread.start();
			process.waitFor();
			outputReaderThread.join();
			final IOException exception = outputReaderThread.getException();
			if (exception == null)
				return outputReaderThread.getTitle();
			throw exception;
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static class OutputReaderThread extends Thread {
		
		private static final String TITLE_KEY = "Title:";
		private static final int TITLE_KEY_LENGTH = TITLE_KEY.length();
		
		private final InputStream pStream;
		private IOException pException;
		private String pTitle;
		
		public OutputReaderThread(final InputStream stream) {
			pStream = stream;
			pException = null;
			pTitle = null;
		}
		
		@Override
		public void run() {
			try (final InputStreamReader r = new InputStreamReader(pStream, StandardCharsets.UTF_8)) {
				try (final BufferedReader reader = new BufferedReader(r)) {
					findTitle(reader);
				}
			} catch (final IOException e) {
				pException = e;
			}
		}

		private void findTitle(final BufferedReader reader) throws IOException {
			while (true) {
				final String line = reader.readLine();
				if (line == null)
					break;
				if (line.startsWith(TITLE_KEY)) {
					pTitle = line.substring(TITLE_KEY_LENGTH).trim();
					break;
				}
			}
		}
		
		public IOException getException() {
			return pException;
		}
		
		public String getTitle() {
			return pTitle;
		}
		
	}
	
}
