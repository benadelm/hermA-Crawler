/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.html.links;

import org.apache.http.client.utils.URIBuilder;

/**
 * Can turn source and target URLs into {@link Link} instances.
 * <p>
 * Typically, one pair of source and target URL would be turned
 * into one {@link Link} instance. However, it is also possible
 * to not create any {@link Link} instance at all (effectively
 * <i>reject</i> the link) or to create more than one instance,
 * if necessary.
 * </p>
 * 
 * @see Link
 */
public interface LinkFactory {
	
	/**
	 * Creates {@link Link} instances from source and target URL.
	 * <p>
	 * For convenience, this method is provided with the
	 * host component of the source URL as a separate parameter,
	 * as a typical caller will have to extract it anyway.
	 * However, in case the host component cannot be extracted,
	 * the corresponding actual parameter might be {@code null}.
	 * The implementation may expect that this is the case
	 * <i>only</i> if extracting the host component was impossible.
	 * </p>
	 * <p>
	 * Again for convenience, this method is provided with the
	 * target URL in two forms: a plain {@link String} URL
	 * and a {@link URIBuilder}. The {@link String} is
	 * expected to be the result of the
	 * {@link URIBuilder#toString()} method.
	 * </p>
	 * <p>
	 * The {@link URIBuilder} may be changed by this method.
	 * </p>
	 * <p>
	 * This method does not return {@code null}
	 * and the items of the returned {@link Iterable}
	 * are not {@code null}, either.
	 * </p>
	 *  
	 * @param referringUrl
	 * the source URL of the link; not {@code null}
	 * 
	 * @param referringHost
	 * the host component of the source URL; possibly {@code null}
	 * 
	 * @param referredUrlBuilder
	 * a {@link URIBuilder} containing the target URL of the link;
	 * not {@code null}
	 * 
	 * @param referredUrl
	 * the target URL of the link; not {@code null}
	 * 
	 * @return
	 * an {@link Iterable} of {@link Link} instances;
	 * neither the {@link Iterable} instance
	 * nor the {@link Link} instances are {@code null}
	 * (but the {@link Iterable} may be empty)
	 * 
	 * @see Link
	 */
	Iterable<? extends Link> createLink(String referringUrl, String referringHost, URIBuilder referredUrlBuilder, String referredUrl);
	
}
