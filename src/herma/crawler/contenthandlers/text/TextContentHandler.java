/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.text;

import java.time.Instant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import herma.crawler.ContentHandler;
import herma.crawler.ErrorHandler;
import herma.crawler.ProcessingThreadCreator;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.contenthandling.util.ResponseUtil;


public class TextContentHandler implements ContentHandler {
	
	private final ErrorHandler pErrorHandler;
	
	public TextContentHandler(final ContentHandlerContext context) {
		pErrorHandler = context.getErrorHandler();
	}
	
	@Override
	public ProcessingThreadCreator handle(final String url, final long depth, final Instant requestTime, final HttpResponse httpResponse) {
		final HttpEntity entity = httpResponse.getEntity();
		final String mime = entity.getContentType().getValue();
		final byte[] responseBytes;
		try {
			responseBytes = ResponseUtil.getEntityBytes(entity);
		} catch (final Exception e) {
			pErrorHandler.handleError(url, e);
			return null;
		}
		
		return (threadSemaphore, context) -> new TextContentHandlerThread(threadSemaphore, url, requestTime, mime, responseBytes, context);
	}
	
}
