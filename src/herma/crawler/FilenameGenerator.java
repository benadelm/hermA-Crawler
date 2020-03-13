/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;


public class FilenameGenerator {
	
	public static String generateFilename(final String prefix, final String host, final String idString) {
		return prefix + '_' + getHostEnd(host) + '_' + idString;
	}
	
	private static String getHostEnd(final String host) {
		final int ultimateDot = host.lastIndexOf('.');
		if (ultimateDot < 0)
			return host;
		return host.substring(host.lastIndexOf('.', ultimateDot - 1) + 1, ultimateDot);
	}
	
}
