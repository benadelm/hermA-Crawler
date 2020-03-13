/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.html;

import java.time.Instant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import herma.crawler.ContentHandler;
import herma.crawler.ErrorHandler;
import herma.crawler.ProcessingThreadCreator;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.contenthandlers.html.links.HtmlLinkExtractor;
import herma.crawler.contenthandling.util.ResponseUtil;
import herma.crawler.textextraction.HtmlTextExtractor;


public class HtmlContentHandler implements ContentHandler {
	
	private final ErrorHandler pErrorHandler;
	
	private final Iterable<? extends HtmlTextExtractor> pTextExtractors;
	private final HtmlLinkExtractor pLinkExtractor;
	
	public HtmlContentHandler(final Iterable<? extends HtmlTextExtractor> textExtractors, final HtmlLinkExtractor linkExtractor, final ContentHandlerContext context) {
		pErrorHandler = context.getErrorHandler();
		
		pTextExtractors = textExtractors;
		pLinkExtractor = linkExtractor;
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
		
		return (threadSemaphore, context) -> new HtmlContentHandlerThread(threadSemaphore, url, depth, requestTime, mime, responseBytes, pTextExtractors, pLinkExtractor, context);
	}
	
}
