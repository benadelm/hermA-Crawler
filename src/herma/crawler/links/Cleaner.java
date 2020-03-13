/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

import org.apache.http.client.utils.URIBuilder;

/**
 * Can clean a URI (URL).
 */
public interface Cleaner {
	
	/**
	 * Cleans a URI (URL) represented by a {@link URIBuilder}.
	 * <p>
	 * This method changes the content of the {@link URIBuilder}
	 * so that the URI (URL) represented by the builder
	 * is clean afterwards (with respect to the
	 * implemented notion of URI cleanness).
	 * </p>
	 * 
	 * @param uri
	 * the {@link URIBuilder} to be modified
	 * to clean the URI (URL) represented by it;
	 * not {@code null}
	 */
	void clean(URIBuilder uri);
	
}
