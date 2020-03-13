/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import herma.crawler.ContentHandler;
import herma.crawler.ErrorHandler;
import herma.crawler.ProcessingThreadCreator;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.textextraction.PdfTextExtractor;


public class PdfContentHandler implements ContentHandler {
	
	private final ErrorHandler pErrorHandler;

	private final PdfTitleExtractor pTitleExtractor;
	private final Iterable<? extends PdfTextExtractor> pTextExtractors;
	
	public PdfContentHandler(final PdfTitleExtractor titleExtractor, final Iterable<? extends PdfTextExtractor> textExtractors, final ContentHandlerContext context) {
		pErrorHandler = context.getErrorHandler();
		
		pTitleExtractor = titleExtractor;
		pTextExtractors = textExtractors;
	}
	
	@Override
	public ProcessingThreadCreator handle(final String url, final long depth, final Instant requestTime, final HttpResponse httpResponse) {
		final HttpEntity entity = httpResponse.getEntity();
		try {
			final Path tempFile = Files.createTempFile("hermA-crawler-", ".pdf");
			try {
				try (final InputStream inputStream = entity.getContent()) {
					Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
				}
				
				final String mime = entity.getContentType().getValue();
				return (threadSemaphore, context) -> new PdfContentHandlerThread(threadSemaphore, url, requestTime, mime, tempFile, pTitleExtractor, pTextExtractors, context);
			} catch (final Exception e) {
				Files.deleteIfExists(tempFile);
				throw e;
			}
		} catch (final IOException e) {
			pErrorHandler.handleError(url, e);
			return null;
		}
	}
	
}
