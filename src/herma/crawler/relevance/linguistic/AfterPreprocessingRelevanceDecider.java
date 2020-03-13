/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance.linguistic;

import herma.crawler.linguisticprocessing.PreParsingResult;
import herma.crawler.relevance.RelevanceDecision;

/**
 * Can decide whether a web document is relevant (according to
 * some criterion), based on linguistic preprocessing of the text
 * extracted from the document.
 * <p>
 * The preprocessing such a relevance decider expects
 * consists of typical linguistic preprocessing steps that happen
 * <i>before parsing</i> and whose results are represented by a
 * {@link PreParsingResult} instance.
 * </p>
 */
public interface AfterPreprocessingRelevanceDecider {
	
	/**
	 * Decides whether a web document is relevant, based on the
	 * result of linguistically preprocessing the text extracted
	 * from the document.
	 * 
	 * @param preParsingResult
	 * the result of linguistically preprocessing the text
	 * extracted from the document; not {@code null}
	 * 
	 * @return
	 * a {@link RelevanceDecision} stating whether the document
	 * is relevant; not {@code null}
	 */
	RelevanceDecision isRelevant(PreParsingResult preParsingResult);
	
}
