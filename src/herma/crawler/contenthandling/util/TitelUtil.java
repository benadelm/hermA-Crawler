/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandling.util;

import java.util.Locale;

import herma.crawler.links.PercentDecoder;

public class TitelUtil {

	private static final String FALLBACK_TITEL = "download";
	
	public static String titleFromUrl(final String url, final String expectedExtension) {
		final String filename = UrlUtil.getLastPathPart(url);
		if (filename == null)
			return FALLBACK_TITEL;
		final int dotIndex = filename.lastIndexOf('.');
		if ((dotIndex >= 0) && expectedExtension.equals(filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT)))
			return PercentDecoder.decodePercentEncodedUtf8(filename.substring(0, dotIndex));
		return PercentDecoder.decodePercentEncodedUtf8(filename);
	}

	public static String titleFromUrl(final String url) {
		final String lastPathPart = UrlUtil.getLastPathPart(url);
		if (lastPathPart == null)
			return FALLBACK_TITEL;
		final String filename = PercentDecoder.decodePercentEncodedUtf8(lastPathPart).trim();
		if ("".equals(filename))
			return FALLBACK_TITEL;
		final int dotIndex = filename.lastIndexOf('.');
		if (dotIndex < 0)
			return filename;
		return filename.substring(0, dotIndex);
	}
	
}
