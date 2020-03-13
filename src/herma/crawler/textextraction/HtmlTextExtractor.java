/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import org.jsoup.nodes.Document;

/**
 * Can extract document text from
 * {@link Document} instances representing HTML files.
 */
public interface HtmlTextExtractor {
	
	/**
	 * Extracts document text from an HTML file,
	 * represented by a {@link Document} instance.
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
	 * this HTML text extractor is (technically) unable to extract
	 * text from the given HTML.
	 * For example, an extractor that relies on HTML5
	 * and would extract document text from within the
	 * {@code <main>} element may return {@code null}
	 * if the HTML does not contain such a
	 * {@code <main>} element.
	 * </p>
	 * <p>
	 * If this text extractor is technically able to extract text
	 * from the given HTML, but does not discover any,
	 * an empty array or an array of empty text extraction results
	 * may be returned.
	 * </p>
	 * <p>
	 * In a fallback scenario, where several HTML text extractors
	 * are tried in a row, a return value of {@code null} could be
	 * interpreted as meaning that this extractor is not applicable
	 * and another one should be tried; on the other hand, a non-null
	 * return value could be interpreted as meaning that further
	 * extractors need not be tried anymore.
	 * </p>
	 * 
	 * @param htmlDocument
	 * the HTML document; not {@code null}
	 * 
	 * @return
	 * An array of non-null {@link ExtractedText} instances
	 * corresponding to alternatives of text extracted from
	 * the given HTML; or {@code null} if this HTML text extractor
	 * is technically unable to extract text from the given HTML.
	 * 
	 * @see ExtractedText
	 */
	ExtractedText[] extractText(Document htmlDocument);
	
}
