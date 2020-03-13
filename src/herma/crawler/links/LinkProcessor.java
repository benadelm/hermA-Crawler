/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import herma.crawler.Blacklist;
import herma.crawler.ErrorHandler;
import herma.crawler.LinkType;
import herma.crawler.meta.MetaInformationStringCollection;

public class LinkProcessor {

	private final Blacklist pBlacklist;
	private final MetaInformationStringCollection pUnfollowedSchemata;
	private final ErrorHandler pErrorHandler;
	
	private final HashMap<String, Cleaner> pCleaners;
	
	public LinkProcessor(final Blacklist blacklist, final MetaInformationStringCollection unfollowedSchemata, final ErrorHandler errorHandler) {
		pBlacklist = blacklist;
		pUnfollowedSchemata = unfollowedSchemata;
		pErrorHandler = errorHandler;
		
		pCleaners = new HashMap<>();
	}
	
	public Map<String, Cleaner> getCleaners() {
		return pCleaners;
	}
	
	public URIBuilder processUrl(final String absUrl, final Charset charset) throws URISyntaxException {
		if ((absUrl == null) || "".equals(absUrl))
			return null;
		return clean(new URIBuilder(UrlPreprocessor.preprocessUrl(absUrl, charset)));
	}
	
	public URIBuilder processLink(final String referringUrl, final String referredAbsUrl, final LinkType linkType, final Charset charset) {
		final URIBuilder targetBuilder = makeUriBuilder(referringUrl, UrlPreprocessor.preprocessUrl(referredAbsUrl, charset), linkType);
		if (targetBuilder == null)
			return null;
		
		return clean(targetBuilder);
	}
	
	private URIBuilder clean(final URIBuilder targetBuilder) {
		final Cleaner cleaner = getCleaner(targetBuilder.getScheme());
		if (cleaner == null)
			return null;
		cleaner.clean(targetBuilder);
		
		if ((targetBuilder == null) || pBlacklist.isOnBlacklist(targetBuilder))
			return null;
		
		return targetBuilder;
	}
	
	private Cleaner getCleaner(final String scheme) {
		if (scheme == null) {
			return null;
		}
		
		final Cleaner cleaner = pCleaners.getOrDefault(scheme, null);
		if (cleaner == null)
			pUnfollowedSchemata.add(scheme);
		return cleaner;
	}
	
	private URIBuilder makeUriBuilder(final String referringUrl, final String referredUrl, final LinkType linkType) {
		if ((referredUrl == null) || "".equals(referredUrl))
			return null;
		try {
			return new URIBuilder(referredUrl);
		} catch (final URISyntaxException e) {
			pErrorHandler.handleError(referringUrl, referredUrl, linkType, e);
			return null;
		}
	}
	
}
