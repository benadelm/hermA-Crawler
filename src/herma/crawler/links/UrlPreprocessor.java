/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

import java.net.IDN;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

class UrlPreprocessor {
	
	public static String preprocessUrl(final String url, final Charset charset) {
		final StringBuilder result = new StringBuilder();
		final int length = url.length();
		
		final int afterSchema = preprocessSchema(url, length, result);
		
		if (hasAuthority(url, afterSchema, length)) {
			result.append("//");
			final int afterSlashes = afterSchema + 2;
			final int hostStart = findHostStart(url, length, afterSlashes, charset, result);
			if (hostStart > afterSlashes)
				appendUserInformation(url, hostStart, afterSlashes, charset, result);
			
			final int hostEnd = findHostEnd(url, length, hostStart, charset, result);
			
			if (hostEnd > hostStart)
				appendHost(result, url.substring(hostStart, hostEnd), charset);
			preprocessRemainder(url, length, hostEnd, charset, result);
		} else {
			preprocessRemainder(url, length, afterSchema, charset, result);
		}
		
		return result.toString();
	}
	
	private static int preprocessSchema(final String url, final int length, final StringBuilder result) {
		int schemaEnd = url.indexOf(':') + 1;
		result.append(url, 0, schemaEnd);
		while (schemaEnd < length) {
			final int codePoint = url.codePointAt(schemaEnd);
			if (Character.isWhitespace(codePoint))
				schemaEnd += Character.charCount(codePoint);
			else
				break;
		}
		return schemaEnd;
	}

	private static boolean hasAuthority(final String url, int afterSchema, final int length) {
		if (afterSchema < length) {
			int codePoint = url.codePointAt(afterSchema);
			if ((codePoint != '/') && (codePoint != '\\'))
				return false;
			afterSchema++;
			if (afterSchema < length) {
				codePoint = url.codePointAt(afterSchema);
				return (codePoint == '/') || (codePoint == '\\');
			}
			return false;
		}
		return false;
	}
	
	private static int findHostStart(final String url, final int length, final int start, final Charset charset, final StringBuilder result) {
		int codePoint;
		for (int i = start; i < length; i += Character.charCount(codePoint)) {
			codePoint = url.codePointAt(i);
			switch (codePoint) {
				case '?':
				case '#':
				case '\\':
				case '/':
					return start;
				case '@':
					return i + 1;
			}
		}
		return start;
	}
	
	private static void appendUserInformation(final String url, final int length, int start, final Charset charset, final StringBuilder result) {
		while (start < length) {
			final int codePoint = url.codePointAt(start);
			switch (codePoint) {
				case '[':
				case ']':
				// unreserved characters
				case '-':
				case '.':
				case '_':
				case '~':
				// delimiters
				case '!':
				case '$':
				case '&':
				case '\'':
				case '(':
				case ')':
				case '*':
				case '+':
				case ',':
				case ';':
				case '=':
				case ':':
				case '@':
					result.appendCodePoint(codePoint);
					break;
				default:
					if (isUnreserved(codePoint)) {
						result.appendCodePoint(codePoint);
						break;
					}
					appendEncodedCodePoint(codePoint, charset, result);
					break;
			}
			start += Character.charCount(codePoint);
		}
	}
	
	private static int findHostEnd(final String url, final int length, int start, final Charset charset, final StringBuilder result) {
		while (start < length) {
			final int codePoint = url.codePointAt(start);
			switch (codePoint) {
				case '?':
				case '#':
				case '\\':
				case '/':
				case ':':
					return start;
			}
			start += Character.charCount(codePoint);
		}
		return start;
	}

	private static void appendHost(final StringBuilder result, String host, final Charset charset) {
		host = PercentDecoder.decodePercentEncoded(host.trim(), charset);
		final String hostAscii;
		try {
			hostAscii = IDN.toASCII(host, IDN.ALLOW_UNASSIGNED | IDN.USE_STD3_ASCII_RULES);
		} catch (final IllegalArgumentException e) {
			result.append(host);
			return;
		}
		result.append(hostAscii);
	}
	
	private static void preprocessRemainder(final String url, final int length, int start, final Charset charset, final StringBuilder result) {
		boolean inPath = false;
		boolean inQuery = false;
		boolean inFragment = false;
		while (start < length) {
			final int codePoint = url.codePointAt(start);
			final int next = start + Character.charCount(codePoint);
			switch (codePoint) {
				case '?':
					if (inQuery || inFragment) {
						appendEncodedCodePoint(codePoint, charset, result);
						break;
					}
					result.appendCodePoint('?');
					inQuery = true;
					break;
				case '#':
					if (inFragment) {
						appendEncodedCodePoint(codePoint, charset, result);
						break;
					}
					result.appendCodePoint('#');
					inFragment = true;
					break;
				case '\\':
				case '/':
					inPath = true;
					result.appendCodePoint('/');
					break;
				case '[':
				case ']':
					if (inPath)
						appendEncodedCodePoint(codePoint, charset, result);
					else
						result.appendCodePoint(codePoint);
					break;
				case '%':
					// do not escape percent characters
					// if valid escape sequence follows
					if (next < length) {
						final int c1 = url.codePointAt(next);
						if (HexUtil.unhexDigit(c1) >= 0) {
							final int nextnext = next + Character.charCount(c1);
							if ((nextnext < length) && (HexUtil.unhexDigit(url.codePointAt(nextnext)) >= 0)) {
								result.appendCodePoint(codePoint);
								break;
							}
						}
					}
					result.append("%25");
					break;
				// unreserved characters
				case '-':
				case '.':
				case '_':
				case '~':
				// delimiters
				case '!':
				case '$':
				case '&':
				case '\'':
				case '(':
				case ')':
				case '*':
				case '+':
				case ',':
				case ';':
				case '=':
				case ':':
				case '@':
					result.appendCodePoint(codePoint);
					break;
				default:
					if (isUnreserved(codePoint)) {
						result.appendCodePoint(codePoint);
						break;
					}
					appendEncodedCodePoint(codePoint, charset, result);
					break;
			}
			start = next;
		}
	}
	
	private static void appendEncodedCodePoint(final int codePoint, final Charset charset, final StringBuilder result) {
		final ByteBuffer bytes = charset.encode(CharBuffer.wrap(Character.toChars(codePoint)));
		while (bytes.hasRemaining()) {
			result.appendCodePoint('%');
			final byte b = bytes.get();
			result.appendCodePoint(HexUtil.hexDigit((b & 0xF0) >> 4));
			result.appendCodePoint(HexUtil.hexDigit(b & 0x0F));
		}
	}
	
	private static boolean isUnreserved(final int codePoint) {
		return ((codePoint > 0x2F) && (codePoint < 0x3A))
				|| ((codePoint > 0x40) && (codePoint < 0x5B))
				|| ((codePoint > 0x60) && (codePoint < 0x7B));
	}
	
}
