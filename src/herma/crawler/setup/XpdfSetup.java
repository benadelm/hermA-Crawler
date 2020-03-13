/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.nio.file.Path;

import herma.crawler.config.Configuration;
import herma.crawler.contenthandlers.pdf.PdfTitleExtractor;
import herma.crawler.textextraction.XpdfTextExtractor;

public class XpdfSetup {
	
	private static final String PDF_TO_TEXT_KEY = "pdfToText";
	private static final String PDF_INFO_KEY = "pdfInfo";
	
	public static XpdfTextExtractor setupXpdfTextExtractor(final Configuration config, final Path pathBase) {
		final Path pdfToTextPath = config.getPath(pathBase, PDF_TO_TEXT_KEY);
		if (pdfToTextPath == null)
			return null;
		return new XpdfTextExtractor(pdfToTextPath);
	}
	
	public static PdfTitleExtractor setupPdfTitleExtractor(final Configuration config, final Path pathBase) {
		final Path pdfInfoPath = config.getPath(pathBase, PDF_INFO_KEY);
		if (pdfInfoPath == null)
			return null;
		return new PdfTitleExtractor(pdfInfoPath);
	}
	
}
