/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.pdf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.Semaphore;

import herma.crawler.Metadata;
import herma.crawler.OriginalInfo;
import herma.crawler.WebDocumentProvider;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.contenthandlers.ContentHandlerThread;
import herma.crawler.contenthandling.util.TitelUtil;
import herma.crawler.contenthandling.util.UrlUtil;
import herma.crawler.textextraction.ExtractedText;
import herma.crawler.textextraction.PdfTextExtractor;
import herma.crawler.util.FallbackUtil;

class PdfContentHandlerThread extends ContentHandlerThread implements WebDocumentProvider {
	
	private final Instant pDownloadTime;
	private final String pMime;
	private final Path pTempFile;
	
	private final PdfTitleExtractor pTitleExtractor;
	private final Iterable<? extends PdfTextExtractor> pTextExtractors;
	
	private String pOwnHost;
	
	public PdfContentHandlerThread(final Semaphore threadSemaphore, final String url, final Instant downloadTime, final String mime, final Path tempFile, final PdfTitleExtractor titleExtractor, final Iterable<? extends PdfTextExtractor> textExtractors, final ContentHandlerContext context) {
		super(threadSemaphore, url, context);
		
		pDownloadTime = downloadTime;
		pMime = mime;
		pTempFile = tempFile;
		
		pTitleExtractor = titleExtractor;
		pTextExtractors = textExtractors;
	}
	
	@Override
	public void handleContent() {
		try {
			pOwnHost = UrlUtil.extractHost(pUrl);
			if (pRelevanceDecider.isRelevant(this)) {
				pAgenda.relevantResultFoundFor(pOwnHost);
				return; // deleteTempFile must NOT be called (therefore, try-finally would not work)
			}
		} catch (final Exception e) {
			deleteTempFile();
			return;
		}
		deleteTempFile();
	}

	private String getTitle() {
		if (pTitleExtractor == null)
			return guessTitleFromUrl();
		final String title;
		try {
			title = pTitleExtractor.extractTitle(pTempFile);
		} catch (final IOException e) {
			return guessTitleFromUrl();
		}
		if ((title == null) || "".equals(title))
			return guessTitleFromUrl();
		return title;
	}

	private String guessTitleFromUrl() {
		return TitelUtil.titleFromUrl(pUrl, "pdf");
	}
	
	private void deleteTempFile() {
		try {
			Files.deleteIfExists(pTempFile);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public String getUrl() {
		return pUrl;
	}
	
	@Override
	public ExtractedText[] extractTexts() {
		return FallbackUtil.fallback(pTextExtractors, PdfTextExtractor::extractText, pTempFile);
	}
	
	@Override
	public OriginalInfo createOriginalInfo() {
		return new PdfOriginalInfo(new Metadata(pUrl, pOwnHost, pDownloadTime, pMime, getTitle()), pTempFile);
	}
	
}
