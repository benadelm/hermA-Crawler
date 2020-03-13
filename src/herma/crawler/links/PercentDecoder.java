/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PercentDecoder {
	
	public static String decodePercentEncodedUtf8(final String str) {
		return decodePercentEncoded(str, StandardCharsets.UTF_8);
	}
	
	public static String decodePercentEncoded(final String str, final Charset charset) {
		int percentIndex = str.indexOf('%');
		if (percentIndex < 0)
			return str;
		final StringBuilder sb = new StringBuilder();
		final int n = str.length();
		final ByteBuffer buf = ByteBuffer.allocate(n);
		int start = 0;
		while (true) {
			sb.append(str, start, percentIndex);
			while (true) {
				percentIndex++;
				if (percentIndex >= n)
					return sb.appendCodePoint('%').toString();
				final int c1 = str.codePointAt(percentIndex);
				final int h1 = HexUtil.unhexDigit(c1);
				if (h1 < 0) {
					sb.appendCodePoint('%');
					break;
				} else {
					percentIndex++;
					if (percentIndex >= n)
						return sb.appendCodePoint('%').toString();
					final int c0 = str.codePointAt(percentIndex);
					final int h0 = HexUtil.unhexDigit(c0);
					if (h0 < 0) {
						sb.appendCodePoint(c1);
						break;
					} else {
						buf.put((byte)(h0 + (h1 << 4)));
						percentIndex++;
					}
				}
				start = percentIndex;
				percentIndex = str.indexOf('%', start);
				if (percentIndex != start)
					break;
			}
			buf.flip();
			sb.append(charset.decode(buf));
			buf.clear();
			if (percentIndex < 0)
				return sb.append(str, start, str.length()).toString();
		}
	}
	
}
