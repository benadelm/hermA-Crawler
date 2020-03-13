/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import herma.crawler.util.CleanupUtil;
import herma.crawler.util.IOUtil;


public class FileExtractedText implements ExtractedText {

	private final TextExtractionMethodInfo pMethod;
	private final Path[] pFiles;
	private String[] pTexts;
	
	public FileExtractedText(final TextExtractionMethodInfo method, final Path file) {
		pMethod = method;
		pFiles = new Path[] { file };
		pTexts = null;
	}
	
	@Override
	public TextExtractionMethodInfo getTextExtractionMethod() {
		return pMethod;
	}
	
	@Override
	public String[] getTexts() {
		if (pTexts == null) {
			pTexts = new String[pFiles.length];
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < pFiles.length; i++) {
				final byte[] bytes;
				try {
					bytes = Files.readAllBytes(pFiles[i]);
				} catch (final IOException e) {
					throw new UncheckedIOException(e);
				}
				pTexts[i] = IOUtil.decodeBytes(bytes, StandardCharsets.UTF_8);
				sb.setLength(0);
			}
		}
		return pTexts;
	}
	
	@Override
	public Path[] getTempFilesPaths() {
		return pFiles;
	}
	
	@Override
	public void removeTempFiles() {
		for (final Path file : pFiles)
			CleanupUtil.nothrowCleanup(() -> Files.deleteIfExists(file), "deleting a temporary file");
	}
	
	@Override
	public void cleanup() {
		// none necessary
	}
	
}
