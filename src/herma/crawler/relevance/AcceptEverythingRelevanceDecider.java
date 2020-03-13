/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance;

import java.util.Collections;

import herma.crawler.linguisticprocessing.PreParsingResult;
import herma.crawler.relevance.linguistic.AfterPreprocessingRelevanceDecider;


public class AcceptEverythingRelevanceDecider implements AfterPreprocessingRelevanceDecider {
	
	public static final AcceptEverythingRelevanceDecider INSTANCE = new AcceptEverythingRelevanceDecider();
	
	private static final RelevanceDecision RELEVANCE_DECISION = new RelevanceDecision() {
		
		@Override
		public boolean isRelevant() {
			return true;
		}

		@Override
		public Iterable<String[]> getRelevanceOutputs() {
			return Collections.emptyList();
		}
		
	};
	
	private AcceptEverythingRelevanceDecider() {
		// singleton
	}
	
	@Override
	public RelevanceDecision isRelevant(final PreParsingResult preParsingResult) {
		return RELEVANCE_DECISION;
	}
	
}
