/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandling.util;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

public class UrlUtil {
	
	public static String extractHost(final String url) {
		final URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(url);
		} catch (final URISyntaxException e) {
			return tryExtractHostFromString(url);
		}
		return extractHost(uriBuilder);
	}
	
	public static String extractHost(final URIBuilder uriBuilder) {
		final String host = uriBuilder.getHost();
		if (host == null)
			return tryExtractHostFromString(uriBuilder.toString());
		return host;
	}
	
	private static String tryExtractHostFromString(final String possiblyErroneousUrl) {
		int startIndex = possiblyErroneousUrl.indexOf("//");
		if (startIndex < 0)
			return null;
		startIndex += 2;
		final int endIndex1 = possiblyErroneousUrl.indexOf('/', startIndex);
		final int endIndex2 = possiblyErroneousUrl.indexOf('?', startIndex);
		final int endIndex3 = possiblyErroneousUrl.indexOf('#', startIndex);
		final int endIndex = minPos(endIndex1, minPos(endIndex2, endIndex3));
		if (endIndex < 0)
			return possiblyErroneousUrl.substring(startIndex);
		return possiblyErroneousUrl.substring(startIndex, endIndex);
	}
	
	private static int minPos(final int int1, final int int2) {
		if (int1 < 0)
			return int2;
		if (int2 < 0)
			return int1;
		if (int1 < int2)
			return int1;
		return int2;
	}
	
	public static String getLastPathPart(final String url) {
		final int lastSlashIndex = url.lastIndexOf('/');
		if (lastSlashIndex < 0)
			return null;
		return url.substring(lastSlashIndex + 1);
	}
	
}
