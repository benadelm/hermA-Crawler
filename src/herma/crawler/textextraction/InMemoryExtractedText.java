/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import herma.crawler.util.CleanupUtil;


public class InMemoryExtractedText implements ExtractedText {
	
	private final TextExtractionMethodInfo pMethod;
	private final String[] pTexts;
	private Path[] pTempFiles;
	
	public InMemoryExtractedText(final TextExtractionMethodInfo method, final String[] texts) {
		pMethod = method;
		pTexts = texts;
		pTempFiles = null;
	}
	
	@Override
	public TextExtractionMethodInfo getTextExtractionMethod() {
		return pMethod;
	}
	
	@Override
	public String[] getTexts() {
		return pTexts;
	}
	
	@Override
	public Path[] getTempFilesPaths() {
		if (pTempFiles == null) {
			final Path[] tempFiles = new Path[pTexts.length];
			try {
				for (int i = 0; i < pTexts.length; i++) {
					try {
						tempFiles[i] = createTempFile(pTexts[i]);
					} catch (final IOException e) {
						for (int j = 0; j < i; j++)
							Files.deleteIfExists(tempFiles[j]);
						throw e;
					}
				}
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
			pTempFiles = tempFiles;
		}
		return pTempFiles;
	}
	
	private static Path createTempFile(final String text) throws IOException {
		final Path tempFile = Files.createTempFile("hermA-crawler-", ".txt");
		try (final BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
			writer.write(text);
			writer.write("\n");
			writer.flush();
			return tempFile;
		} catch (final Exception e) {
			Files.deleteIfExists(tempFile);
			throw e;
		}
	}
	
	@Override
	public void removeTempFiles() {
		if (pTempFiles != null)
			for (final Path file : pTempFiles)
				CleanupUtil.nothrowCleanup(() -> Files.deleteIfExists(file), "deleting a temporary file");
	}
	
	@Override
	public void cleanup() {
		// none necessary
	}
	
}
