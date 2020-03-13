/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.html.links;

import org.jsoup.nodes.Document;

/**
 * Can extract links from {@link Document} instances
 * representing HTML files.
 */
public interface HtmlLinkExtractor {
	
	/**
	 * Extracts links from an HTML file,
	 * represented by a {@link Document} instance.
	 * <p>
	 * Besides the information contained in the HTML,
	 * this method may also make use of the URL
	 * the HTML has been retrieved from.
	 * For convenience, the method also receives
	 * the host component of that URL as a separate parameter,
	 * as a typical caller will have to extract it anyway.
	 * However, in case the host component cannot be extracted,
	 * the corresponding actual parameter might be {@code null}.
	 * The implementation may expect that this is the case
	 * <i>only</i> if extracting the host component was impossible.
	 * </p>
	 * <p>
	 * Besides extracting links actually present in the HTML,
	 * this method may also return links that can otherwise be
	 * derived from the HTML document by some logic.
	 * {@link Link#isActualLink()} of the returned {@link Link}
	 * instances indicates whether the link is actually present
	 * in the HTML.
	 * </p>
	 * <p>
	 * This method does not return {@code null}
	 * and the items of the returned {@link Iterable}
	 * are not {@code null}, either.
	 * </p>
	 * 
	 * @param url
	 * the URL the HTML has been retrieved from; not {@code null}
	 * 
	 * @param host
	 * the host component of the URL; possibly {@code null}
	 * 
	 * @param document
	 * the HTML document; not {@code null}
	 * 
	 * @return
	 * An {@link Iterable} of {@link Link} instances
	 * representing outgoing links from the HTML document.
	 * Neither the {@link Iterable} instance
	 * nor the {@link Link} instances are {@code null}
	 * (but the {@link Iterable} may be empty).
	 * 
	 * @see Link
	 */
	Iterable<Link> extractLinks(String url, String host, Document document);
	
}
