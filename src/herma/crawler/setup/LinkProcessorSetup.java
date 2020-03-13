/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import herma.crawler.links.Cleaner;
import herma.crawler.links.JsessionidRemover;
import herma.crawler.links.LinkProcessor;
import herma.crawler.links.Rewriter;
import herma.crawler.links.RewritingCleaner;

public class LinkProcessorSetup {
	
	public static void setupLinkProcessor(final LinkProcessor linkProcessor) {
		final RewritingCleaner urlCleaner = new RewritingCleaner();
		
		final List<Rewriter> urlRewriters = urlCleaner.getRewriters();
		urlRewriters.add(URIBuilder::removeQuery);
		urlRewriters.add(b -> b.setFragment(null));
		urlRewriters.add(new JsessionidRemover());
		
		final Map<String, Cleaner> cleaners = linkProcessor.getCleaners();
		cleaners.put("http", urlCleaner);
		cleaners.put("https", urlCleaner);
	}
	
}
