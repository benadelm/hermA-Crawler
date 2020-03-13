/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

import org.apache.http.client.utils.URIBuilder;

/**
 * Can rewrite a URI (URL).
 */
@FunctionalInterface
public interface Rewriter {
	
	/**
	 * Rewrites a URI (URL) represented by a {@link URIBuilder}.
	 * <p>
	 * This method changes the content of the {@link URIBuilder}.
	 * </p>
	 * 
	 * @param uri
	 * a {@link URIBuilder} representing
	 * the URI (URL) to be rewritten; not {@code null}
	 */
	void rewrite(URIBuilder uri);
	
}
