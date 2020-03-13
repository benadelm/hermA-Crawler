/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandling.util;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;

public class ContentTypeUtil {
	
	public static Charset getEncodingOrDefault(final String mime, final Charset defaultCharset) throws IllegalCharsetNameException, UnsupportedCharsetException {
		final Charset charset = getEncoding(mime);
		if (charset == null)
			return defaultCharset;
		return charset;
	}
	
	public static Charset getEncoding(final String mime) throws IllegalCharsetNameException, UnsupportedCharsetException {
		final HeaderElement elem = BasicHeaderValueParser.parseHeaderElement(mime, BasicHeaderValueParser.INSTANCE);
		if (elem == null)
			return null;
		final NameValuePair param = elem.getParameterByName("charset");
		if (param == null)
			return null;
		return getCharset(param.getValue());
	}

	public static Charset getCharset(final String charsetName) throws IllegalCharsetNameException, UnsupportedCharsetException {
		switch (charsetName.toLowerCase(Locale.ROOT)) {
			case "latin-1":
				return StandardCharsets.ISO_8859_1;
			case "utf8":
				return StandardCharsets.UTF_8;
			default:
				return Charset.forName(charsetName);
		}
	}
	
}
