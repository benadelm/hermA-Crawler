/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.html.links;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.client.utils.URIBuilder;

import herma.crawler.contenthandling.util.UrlUtil;

public class ForeignHostFilterLinkFactory implements LinkFactory {
	
	private final boolean pSkipForeignLinks;
	
	public ForeignHostFilterLinkFactory(final boolean skipForeignLinks) {
		pSkipForeignLinks = skipForeignLinks;
	}
	
	@Override
	public Iterable<? extends Link> createLink(final String referringUrl, final String referringHost, final URIBuilder referredUrlBuilder, final String referredUrl) {
		final String host = UrlUtil.extractHost(referredUrlBuilder);
		if ((referringHost == null) || referringHost.equals(host))
			return Collections.singleton(new Link(referredUrl, host, true, true));
		if (pSkipForeignLinks)
			return Collections.emptyList();
		final ArrayList<Link> result = new ArrayList<>();
		result.add(new Link(referredUrl, host, false, true));
		referredUrlBuilder.setPath("/");
		result.add(new Link(referredUrlBuilder.toString(), host, false, false));
		return result;
	}
	
}
