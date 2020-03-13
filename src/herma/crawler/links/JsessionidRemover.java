/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

import org.apache.http.client.utils.URIBuilder;


public class JsessionidRemover implements Rewriter {
	
	@Override
	public void rewrite(final URIBuilder uri) {
		final String path = uri.getPath();
		if (path == null)
			return;
		final int jsessionidStart = path.indexOf(";jsessionid");
		if (jsessionidStart < 0)
			return;
		uri.setPath(path.substring(0, jsessionidStart));
	}
	
}
