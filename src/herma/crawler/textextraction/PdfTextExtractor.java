/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import java.nio.file.Path;

/**
 * Can extract document text from PDF files.
 */
public interface PdfTextExtractor {
	
	/**
	 * Extracts document text from a PDF file,
	 * located by a {@link Path}.
	 * <p>
	 * The document text is returned in the form of
	 * {@link ExtractedText} instances,
	 * which represent alternative extraction results.
	 * Some implementations may provide alternative results,
	 * but it is also possible that an implementation always
	 * returns an array of length one.
	 * </p>
	 * <p>
	 * This method may return {@code null} if, for some reason,
	 * this PDF text extractor is (technically) unable to extract
	 * text from the given PDF file, for example if the file
	 * cannot be found or does not contain valid PDF data.
	 * </p>
	 * <p>
	 * If this text extractor is technically able to extract text
	 * from the given PDF, but does not discover any,
	 * an empty array or an array of empty text extraction results
	 * may be returned.
	 * </p>
	 * <p>
	 * In a fallback scenario, where several PDF text extractors
	 * are tried in a row, a return value of {@code null} could be
	 * interpreted as meaning that this extractor is not applicable
	 * and another one should be tried; on the other hand, a non-null
	 * return value could be interpreted as meaning that further
	 * extractors need not be tried anymore.
	 * </p>
	 * 
	 * @param pdfFile
	 * (a {@link Path} locating) the PDF file; not {@code null}
	 * 
	 * @return
	 * An array of non-null {@link ExtractedText} instances
	 * corresponding to to alternatives of text extracted from
	 * the given PDF file; or {@code null} if this PDF text extractor
	 * is technically unable to extract text from the given PDF file.
	 * 
	 * @see ExtractedText
	 */
	ExtractedText[] extractText(Path pdfFile);
	
}
