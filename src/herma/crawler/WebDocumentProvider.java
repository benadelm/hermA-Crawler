/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import herma.crawler.textextraction.ExtractedText;

/**
 * Provides access to a currently processed web document,
 * more or less independently of the document type
 * (HTML, PDF, etc.).
 * <p>
 * Implementations may be stateful and thus the data they return
 * may change over time. Thread safety is not guaranteed, either.
 * </p>
 *
 */
public interface WebDocumentProvider {
	
	/**
	 * Returns the URL from which the currently processed
	 * web document has been retrieved.
	 * 
	 * @return
	 * the URL from which the currently processed web document
	 * has been retrieved;
	 * not {@code null}
	 */
	String getUrl();
	
	/**
	 * Extracts text from the currently processed web document.
	 * 
	 * @return
	 * An array of non-null {@link ExtractedText} instances
	 * corresponding to alternatives of text extracted from
	 * the currently processed web document;
	 * or {@code null} if it was technically impossible
	 * to extract document text.
	 */
	ExtractedText[] extractTexts();
	
	/**
	 * Creates an {@link OriginalInfo} view on the metadata
	 * of the currently processed web document.
	 * 
	 * @return
	 * an {@link OriginalInfo} view on the metadata
	 * of the currently processed web document;
	 * not {@code null}
	 */
	OriginalInfo createOriginalInfo();
	
}
