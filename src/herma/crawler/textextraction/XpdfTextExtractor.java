/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;

import herma.crawler.util.RunUtil;


public class XpdfTextExtractor implements PdfTextExtractor {
	
	private final Path pXpdfPdfToTextPath;
	
	public XpdfTextExtractor(final Path xpdfPdfToTextPath) {
		pXpdfPdfToTextPath = xpdfPdfToTextPath;
	}
	
	@Override
	public ExtractedText[] extractText(final Path pdfFile) {
		try {
			final Path tempFile = Files.createTempFile("hermA-crawler-", ".txt");
			try {
				return extractText(pdfFile, tempFile);
			} catch (final Exception e) {
				Files.deleteIfExists(tempFile);
				throw e;
			}
		} catch (final IOException e) {
			return null;
		}
	}

	private ExtractedText[] extractText(final Path pdfFile, final Path targetFile) throws IOException {
		final ProcessBuilder pb = new ProcessBuilder(pXpdfPdfToTextPath.toString(), "-enc", "UTF-8", "-eol", "unix", pdfFile.toString(), targetFile.toString());
		
		pb.redirectInput(Redirect.PIPE);
		pb.redirectOutput(Redirect.PIPE);
		pb.redirectError(Redirect.PIPE);
		
		final Process p = pb.start();
		RunUtil.consumeStreamsToNowhere(p); // TODO: warnings/errors?
		
		try {
			p.waitFor();
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		return new ExtractedText[] {
				new FileExtractedText(TextExtractionMethodInfo.XPDF, targetFile)
			};
	}
}
