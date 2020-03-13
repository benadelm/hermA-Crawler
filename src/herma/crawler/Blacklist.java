/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;

import herma.crawler.contenthandling.util.UrlUtil;

public class Blacklist {
	
	private final Pattern pBlacklistPattern;
	
	public Blacklist(final Iterable<String> items) {
		final StringBuilder regexBuilder = new StringBuilder();
		regexBuilder.append(".*((");
		boolean first = true;
		for (final String item : items) {
			if (first)
				first = false;
			else
				regexBuilder.append(")|(");
			regexBuilder.append(Pattern.quote(item));
		}
		regexBuilder.append(")).*");
		if (first)
			pBlacklistPattern = Pattern.compile("", Pattern.CASE_INSENSITIVE);
		else
			pBlacklistPattern = Pattern.compile(regexBuilder.toString(), Pattern.CASE_INSENSITIVE);
	}
	
	public boolean isOnBlacklist(final String url) {
		final URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(url);
		} catch (final URISyntaxException e) {
			return false;
		}
		return isOnBlacklist(uriBuilder);
	}
	
	public boolean isOnBlacklist(final URIBuilder url) {
		final String host = UrlUtil.extractHost(url);
		if (host == null)
			return false;
		return pBlacklistPattern.matcher(host).matches();
	}
}
