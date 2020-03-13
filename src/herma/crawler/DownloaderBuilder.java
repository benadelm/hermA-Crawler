/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.impl.client.CloseableHttpClient;

import herma.crawler.contenthandlers.ProcessingThreadStarter;
import herma.crawler.links.LinkProcessor;
import herma.crawler.meta.LinkCollector;
import herma.crawler.meta.MetaInformationStringCollection;

public class DownloaderBuilder {
	
	private CloseableHttpClient pClient = null;
	private LinkProcessor pLinkProcessor = null;
	private Restager pRestager = null;
	private ProcessingThreadStarter pProcessingThreadStarter = null;
	private ErrorHandler pErrorHandler = null;
	private LinkCollector pLinkCollector = null;
	private MetaInformationStringCollection pUnhandledMimeTypes;
	private HashMap<String, ContentHandler> pContentHandlers = null;
	private HashSet<String> pIgnoreMime = null;
	
	public void setClient(final CloseableHttpClient client) {
		pClient = client;
	}
	
	public void setLinkProcessor(final LinkProcessor linkProcessor) {
		pLinkProcessor = linkProcessor;
	}
	
	public void setRestager(final Restager restager) {
		pRestager = restager;
	}
	
	public void setProcessingThreadStarter(final ProcessingThreadStarter processingThreadStarter) {
		pProcessingThreadStarter = processingThreadStarter;
	}
	
	public void setErrorHandler(final ErrorHandler errorHandler) {
		pErrorHandler = errorHandler;
	}
	
	public void setLinkCollector(final LinkCollector linkCollector) {
		pLinkCollector = linkCollector;
	}
	
	public void setUnhandledMimeTypes(final MetaInformationStringCollection unhandledMimeTypes) {
		pUnhandledMimeTypes = unhandledMimeTypes;
	}
	
	public void setContentHandlers(final HashMap<String, ContentHandler> contentHandlers) {
		pContentHandlers = contentHandlers;
	}
	
	public void setIgnoreMime(final HashSet<String> ignoreMime) {
		pIgnoreMime = ignoreMime;
	}
	
	public Downloader build() {
		final Downloader downloader = new Downloader(pClient, pLinkProcessor, pRestager, pProcessingThreadStarter, pErrorHandler, pLinkCollector, pUnhandledMimeTypes);
		downloader.getContentHandlers().putAll(pContentHandlers);
		downloader.getIgnoreMime().addAll(pIgnoreMime);
		return downloader;
	}
	
}
