/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

/**
 * A view on the crawling agenda to be used for re-inserting URLs
 * (for example, as a reaction to a redirect).
 * 
 */
public interface Restager {
	
	/**
	 * Re-inserts a URL into the agenda.
	 * <p>
	 * The re-inserted URL is prioritized to be taken
	 * out of the agenda again as soon as possible
	 * (which is desirable, for example, in case of a redirect).
	 * </p>
	 * 
	 * @param url
	 * the URL to be re-inserted; not {@code null}
	 * 
	 * @param host
	 * the host component of the URL to be re-inserted;
	 * possibly {@code null}
	 * 
	 * @param depth
	 * the depth of the URL to re-insert in the search tree;
	 * seed URLs have depth zero
	 */
	void restage(String url, String host, long depth);
	
	/**
	 * Notifies the agenda that a URL has been fully processed.
	 * <p>
	 * In terms of this method,
	 * a URL has been fully processed when no processing will
	 * happen anymore. This also applies if
	 * {@link #restage(String, String, long)}
	 * has been called for that URL, but it does not apply
	 * if usual processing of the URL is initiated. 
	 * </p>
	 * 
	 * @param url
	 * the URL that has been fully processed; not {@code null}
	 */
	void processingFinished(String url);
	
}
