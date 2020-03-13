/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.html;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import herma.crawler.LinkType;
import herma.crawler.Metadata;
import herma.crawler.OriginalInfo;
import herma.crawler.WebDocumentProvider;
import herma.crawler.contenthandlers.BytesOriginalInfo;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.contenthandlers.ContentHandlerThread;
import herma.crawler.contenthandlers.html.links.HtmlLinkExtractor;
import herma.crawler.contenthandlers.html.links.Link;
import herma.crawler.contenthandling.util.ContentTypeUtil;
import herma.crawler.contenthandling.util.TitelUtil;
import herma.crawler.contenthandling.util.UrlUtil;
import herma.crawler.meta.LinkCollector;
import herma.crawler.textextraction.ExtractedText;
import herma.crawler.textextraction.HtmlTextExtractor;
import herma.crawler.util.FallbackUtil;
import herma.crawler.util.IOUtil;

class HtmlContentHandlerThread extends ContentHandlerThread implements WebDocumentProvider {
	
	private final long pDepth;
	private final Instant pDownloadTime;
	private final String pMime;
	private final byte[] pBytes;
	
	private final HtmlLinkExtractor pLinkExtractor;
	private final LinkCollector pLinkCollector;
	
	private final Iterable<? extends HtmlTextExtractor> pTextExtractors;
	
	private Document pDocument;
	private String pOwnHost;
	
	public HtmlContentHandlerThread(final Semaphore threadSemaphore, final String url, final long depth, final Instant downloadTime, final String mime, final byte[] bytes, final Iterable<? extends HtmlTextExtractor> textExtractors, final HtmlLinkExtractor linkExtractor, final ContentHandlerContext context) {
		super(threadSemaphore, url, context);
		
		pDepth = depth;
		pDownloadTime = downloadTime;
		pMime = mime;
		pBytes = bytes;
		
		pLinkExtractor = linkExtractor;
		pLinkCollector = context.getLinkCollector();
		
		pTextExtractors = textExtractors;
	}
	
	@Override
	public void handleContent() {
		pDocument = getHtmlDocument();
		if (pDocument == null)
			return;
		
		pOwnHost = UrlUtil.extractHost(pUrl);
		final ArrayList<Link> deferredLinks = new ArrayList<>();
		for (final Link link : pLinkExtractor.extractLinks(pUrl, pOwnHost, pDocument)) {
			if (link.followImmediately())
				addLinkToAgenda(link);
			else
				deferredLinks.add(link);
		}
		
		if (pRelevanceDecider.isRelevant(this)) {
			pAgenda.relevantResultFoundFor(pOwnHost);
			for (final Link deferredLink : deferredLinks)
				addLinkToAgenda(deferredLink);
		}
	}
	
	private Document getHtmlDocument() {
		final Charset headerCharset = getCharsetByMime(pMime);
		if (headerCharset != null)
			return getHtmlDocument(headerCharset);
		
		final Document asciiDoc = getHtmlDocument(StandardCharsets.US_ASCII);
		final Elements metaElements = asciiDoc.select("meta");
		for (final Element element : metaElements) {
			if (element.hasAttr("charset")) {
				final Charset metaCharset = getCharsetByName(element.attr("charset"));
				if (metaCharset != null)
					return getHtmlDocument(metaCharset);
			}
			final String httpEquiv = element.attr("http-equiv");
			if ("content-type".equals(httpEquiv.toLowerCase(Locale.ROOT))) {
				final Charset httpEquivCharset = getCharsetByMime(element.attr("content"));
				if (httpEquivCharset != null)
					return getHtmlDocument(httpEquivCharset);
			}
		}
		
		return asciiDoc;
	}

	private Document getHtmlDocument(final Charset charset) {
		return Jsoup.parse(IOUtil.decodeBytes(pBytes, charset), pUrl);
	}
	
	// TODO: the following two methods are not specific to HTML
	// Some other content processor might need them;
	// move them to some kind of helper/utility class?
	
	private Charset getCharsetByMime(final String mime) {
		try {
			return ContentTypeUtil.getEncoding(mime);
		} catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
			pErrorHandler.handleError(pUrl, e);
			return null;
		}
	}
	
	private Charset getCharsetByName(final String charsetName) {
		try {
			return ContentTypeUtil.getCharset(charsetName);
		} catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
			pErrorHandler.handleError(pUrl, e);
			return null;
		}
	}
	
	@Override
	public String getUrl() {
		return pUrl;
	}
	
	@Override
	public ExtractedText[] extractTexts() {
		return FallbackUtil.fallback(pTextExtractors, HtmlTextExtractor::extractText, pDocument);
	}
	
	@Override
	public OriginalInfo createOriginalInfo() {
		return new BytesOriginalInfo(new Metadata(pUrl, pOwnHost, pDownloadTime, pMime, getHtmlTitle(pDocument)), ".htm", pBytes);
	}
	
	private String getHtmlTitle(final Document document) {
		final Elements titleElements = document.select("head title");
		final StringBuilder titleBuilder = new StringBuilder();
		boolean first = true;
		for (final Element titleElement : titleElements) {
			if (first)
				first = false;
			else
				titleBuilder.append(" / ");
			titleBuilder.append(titleElement.text());
		}
		if (titleBuilder.length() == 0)
			return TitelUtil.titleFromUrl(pUrl);
		return titleBuilder.toString();
	}

	private void addLinkToAgenda(final Link link) {
		final String target = link.getUri();
		pAgenda.add(target, link.getHost(), pDepth);
		if (link.isActualLink())
			pLinkCollector.addLink(pUrl, target, LinkType.LINK, pDepth);
	}
	
}
