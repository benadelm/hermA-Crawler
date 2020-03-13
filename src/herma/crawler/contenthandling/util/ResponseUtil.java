/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandling.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;

public class ResponseUtil {
	
	public static byte[] getEntityBytes(final HttpEntity entity) throws IOException {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		entity.writeTo(buffer);
		return buffer.toByteArray();
	}
	
}
