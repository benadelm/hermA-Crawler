/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;


public class RewritingCleaner implements Cleaner {
	
	private final ArrayList<Rewriter> pRewriters = new ArrayList<>();
	
	public List<Rewriter> getRewriters() {
		return pRewriters;
	}
	
	@Override
	public void clean(final URIBuilder uri) {
		for (final Rewriter rewriter : pRewriters)
			rewriter.rewrite(uri);
	}
	
}
