/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import herma.crawler.linguisticprocessing.PreParsingResult;
import herma.crawler.relevance.RelevanceDecision;

/**
 * A data item from a web document to be further processed.
 * Such an item consists of the results of linguistic
 * preprocessing and the result of a relevance decision.
 * 
 * @see PreParsingResult
 * @see RelevanceDecision
 *
 */
public class FurtherProcessingItem {
	
	private final PreParsingResult pPreParsingResult;
	private final RelevanceDecision pRelevanceDecision;
	
	public FurtherProcessingItem(final PreParsingResult preParsingResult, final RelevanceDecision relevanceDecision) {
		pPreParsingResult = preParsingResult;
		pRelevanceDecision = relevanceDecision;
	}
	
	/**
	 * Returns the results of the linguistic preprocessing
	 * performed before the relevance decision.
	 * 
	 * @return
	 * results of linguistic preprocessing
	 * performed before the relevance decision;
	 * not {@code null}
	 * 
	 * @see PreParsingResult
	 */
	public PreParsingResult getPreParsingResult() {
		return pPreParsingResult;
	}
	
	/**
	 * Returns the result of the relevance decision.
	 * 
	 * @return
	 * the result of the relevance decision;
	 * not {@code null}
	 * 
	 * @see RelevanceDecision
	 */
	public RelevanceDecision getRelevanceDecision() {
		return pRelevanceDecision;
	}
	
}
