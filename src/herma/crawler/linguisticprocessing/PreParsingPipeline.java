/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;

import herma.crawler.textextraction.ExtractedText;

/**
 * Can apply linguistic preprocessing
 * (that happens before parsing).
 */
public interface PreParsingPipeline {
	
	/**
	 * Applies linguistic preprocessing to
	 * (text extracted from) a web document.
	 * 
	 * @param extractedText
	 * an instance of {@link ExtractedText} representing
	 * text extracted from a web document,
	 * to be linguistically preprocessed;
	 * not {@code null}
	 * 
	 * @return
	 * a {@link PreParsingResult} providing access
	 * to the result of the linguistic preprocessing;
	 * not {@code null}
	 * 
	 * @see ExtractedText
	 * @see PreParsingResult
	 */
	PreParsingResult apply(ExtractedText extractedText);
	
}
