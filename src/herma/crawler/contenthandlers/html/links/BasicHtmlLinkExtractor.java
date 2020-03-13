/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.html.links;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import herma.crawler.LinkType;
import herma.crawler.links.LinkProcessor;

public class BasicHtmlLinkExtractor implements HtmlLinkExtractor {
	
	private final LinkProcessor pLinkProcessor;
	private final LinkFactory pLinkFilter;
	
	public BasicHtmlLinkExtractor(final LinkProcessor linkProcessor, final LinkFactory linkFilter) {
		pLinkProcessor = linkProcessor;
		pLinkFilter = linkFilter;
	}
	
	@Override
	public Iterable<Link> extractLinks(final String url, final String host, final Document document) {
		return addLinksToAgenda(document, StandardCharsets.UTF_8, url, host);
	}
	
	private ArrayList<Link> addLinksToAgenda(final Document document, final Charset charset, final String ownUrl, final String ownHost) {
		final ArrayList<Link> result = new ArrayList<>();
		final HashSet<String> targets = new HashSet<>();
		addLinksFromAttribute(document, charset, "a", "href", ownUrl, ownHost, result, targets);
		addLinksFromAttribute(document, charset, "area", "href", ownUrl, ownHost, result, targets);
		addLinksFromAttribute(document, charset, "frame", "src", ownUrl, ownHost, result, targets);
		addLinksFromAttribute(document, charset, "iframe", "src", ownUrl, ownHost, result, targets);
		addLinksFromAttribute(document, charset, "embed", "src", ownUrl, ownHost, result, targets);
		addLinksFromAttribute(document, charset, "object", "data", ownUrl, ownHost, result, targets);
		return result;
	}
	
	private void addLinksFromAttribute(final Document document, final Charset charset, final String elementName, final String attr, final String ownUrl, final String ownHost, final ArrayList<Link> result, final HashSet<String> targets) {
		final Elements links = document.select(elementName);
		for (final Element link : links)
			if (link.hasAttr(attr))
				processLink(link.absUrl(attr), charset, ownUrl, ownHost, result, targets);
	}
	
	private void processLink(final String absUrl, final Charset charset, final String ownUrl, final String ownHost, final ArrayList<Link> links, final HashSet<String> targets) {
		final URIBuilder targetBuilder = pLinkProcessor.processLink(ownUrl, absUrl, LinkType.LINK, charset);
		if (targetBuilder == null)
			return;
		final String target = targetBuilder.toString();
		if (targets.add(target))
			for (final Link link : pLinkFilter.createLink(ownUrl, ownHost, targetBuilder, target))
				links.add(link);
	}
	
}
