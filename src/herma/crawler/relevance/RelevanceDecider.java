/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance;

import herma.crawler.WebDocumentProvider;

/**
 * Can decide whether a web document is relevant (according to
 * some criterion).
 * <p>
 * Access to the web document is provided
 * via a {@link WebDocumentProvider}.
 * </p>
 * 
 * @see WebDocumentProvider
 */
public interface RelevanceDecider {
	
	/**
	 * Decides whether a web document is relevant
	 * (according to the implemented criterion),
	 * initiating further processing if necessary.
	 * 
	 * @param webDocument
	 * a {@link WebDocumentProvider} providing access to
	 * the web document to be tested for relevance;
	 * not {@code null}
	 * 
	 * @return
	 * {@code true} if the web document is relevant;
	 * {@code false} otherwise
	 */
	boolean isRelevant(WebDocumentProvider webDocument);
	
}
