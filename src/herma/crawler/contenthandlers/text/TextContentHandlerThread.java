/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.text;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.time.Instant;
import java.util.concurrent.Semaphore;

import herma.crawler.Metadata;
import herma.crawler.OriginalInfo;
import herma.crawler.WebDocumentProvider;
import herma.crawler.contenthandlers.BytesOriginalInfo;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.contenthandlers.ContentHandlerThread;
import herma.crawler.contenthandling.util.ContentTypeUtil;
import herma.crawler.contenthandling.util.TitelUtil;
import herma.crawler.contenthandling.util.UrlUtil;
import herma.crawler.textextraction.ExtractedText;
import herma.crawler.textextraction.InMemoryExtractedText;
import herma.crawler.textextraction.TextExtractionMethodInfo;
import herma.crawler.util.IOUtil;

class TextContentHandlerThread extends ContentHandlerThread implements WebDocumentProvider {
	
	private final Instant pDownloadTime;
	private final String pMime;
	private final byte[] pBytes;
	
	private String pText;
	private String pOwnHost;
	
	public TextContentHandlerThread(final Semaphore threadSemaphore, final String url, final Instant downloadTime, final String mime, final byte[] bytes, final ContentHandlerContext context) {
		super(threadSemaphore, url, context);
		
		pDownloadTime = downloadTime;
		pMime = mime;
		pBytes = bytes;
	}
	
	@Override
	public void handleContent() {
		pText = getTextString();
		if (pText == null)
			return;

		pOwnHost = UrlUtil.extractHost(pUrl);
		
		if (pRelevanceDecider.isRelevant(this)) {
			pAgenda.relevantResultFoundFor(pOwnHost);
		}
	}

	private String getTextString() {
		final Charset charset = getCharset();
		if (charset == null)
			return null;
		return IOUtil.decodeBytes(pBytes, charset);
	}
	
	private Charset getCharset() {
		try {
			return ContentTypeUtil.getEncodingOrDefault(pMime, StandardCharsets.US_ASCII);
		} catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
			pErrorHandler.handleError(pUrl, e);
			return null;
		}
	}
	
	@Override
	public String getUrl() {
		return pUrl;
	}
	
	@Override
	public ExtractedText[] extractTexts() {
		return new ExtractedText[] { new InMemoryExtractedText(TextExtractionMethodInfo.TXT, new String[] { pText }) };
	}
	
	@Override
	public OriginalInfo createOriginalInfo() {
		return new BytesOriginalInfo(new Metadata(pUrl, pOwnHost, pDownloadTime, pMime, TitelUtil.titleFromUrl(pUrl, "txt")), ".txt", pBytes);
	}
	
}
