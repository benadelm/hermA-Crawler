/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.agenda;

/**
 * A view on the crawling agenda to be used for adding URLs.
 */
public interface AgendaIn {
	
	/**
	 * Notifies the agenda that the number of components
	 * that can possibly add URLs to the agenda
	 * has increased by one.
	 * <p>
	 * If this method has been called,
	 * {@link #decrementPendingCount()}
	 * <b>must</b> be called at some point in time
	 * when the component that can possibly add URLs to the
	 * will not add new URLs to the agenda anymore.
	 * </p>
	 * <p>
	 * As long as the agenda thinks there is any component
	 * that can possibly add URLs, trying to obtain from the agenda
	 * the next URL to be processed will result in
	 * indefinite waiting until such a next URL is available.
	 * If, on the other hand, the agenda knows that no-one will
	 * add any new URL, waiting is pointless and crawling can
	 * terminate.
	 * </p>
	 * <p>
	 * Thus, failing to call
	 * {@link #decrementPendingCount()}
	 * will result in a deadlock-like situation
	 * and will prevent the crawl from terminating properly.
	 * </p>
	 */
	void incrementPendingCount();
	
	/**
	 * Notifies the agenda that the number of components
	 * that can possibly add URLs to the agenda
	 * has decreased by one.
	 * <p>
	 * This method <b>must not</b> be called without a
	 * corresponding call to
	 * {@link #incrementPendingCount()}
	 * having happened before.
	 * Otherwise the agenda may prematurely signal emptiness
	 * and probably give rise to unexpected events.
	 * </p>
	 */
	void decrementPendingCount();
	
	/**
	 * Adds a URL to the agenda.
	 * <p>
	 * For convenience, this method is provided with the
	 * host component of the URL as a separate parameter,
	 * as a typical caller will have to extract it anyway.
	 * However, in case the host component cannot be extracted,
	 * the corresponding actual parameter might be {@code null}.
	 * The implementation may expect that this is the case
	 * <i>only</i> if extracting the host component was impossible.
	 * </p>
	 * 
	 * @param url
	 * the URL to be added; not {@code null}
	 * 
	 * @param host
	 * the host component of the URL to be added;
	 * possibly {@code null}
	 * 
	 * @param depth
	 * the depth of the URL to add in the search tree;
	 * seed URLs have depth zero
	 */
	void add(String url, String host, long depth);
	
	/**
	 * Notifies the agenda that some web document
	 * from a URL with this host component has been classified
	 * as relevant.
	 * <p>
	 * This method should be called exactly once for every
	 * web document that has been classified as relevant.
	 * </p>
	 * 
	 * @param host
	 * the host component of the URL of the document
	 * that has been classified as relevant;
	 * possibly {@code null}
	 */
	void relevantResultFoundFor(String host);
	
	/**
	 * Notifies the agenda that a URL has been fully processed.
	 * <p>
	 * In terms of this method,
	 * a URL has been fully processed when a relevance decision
	 * has been reached. Further processing of relevant documents,
	 * which could also be performed off-line
	 * after crawling has terminated,
	 * is irrelevant from the agenda’s point of view.
	 * </p>
	 * 
	 * @param url
	 * the URL that has been fully processed; not {@code null}
	 */
	void processingFinished(String url);
	
}
