/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

import herma.crawler.agenda.AgendaItem;
import herma.crawler.contenthandlers.ProcessingThreadStarter;
import herma.crawler.contenthandling.util.UrlUtil;
import herma.crawler.links.LinkProcessor;
import herma.crawler.meta.LinkCollector;
import herma.crawler.meta.MetaInformationStringCollection;

public class Downloader {
	
	private static final int RETRIES = 10;
	private static final String RETRIES_STR = Integer.toString(RETRIES);
	
	private final CloseableHttpClient pClient;
    private final HttpContext pHttpContext;
	
    private final LinkProcessor pLinkProcessor;
    private final Restager pRestager;
    
	private final HashMap<String, ContentHandler> pContentHandlers;
	private final HashSet<String> pIgnoreMime;
	private final ProcessingThreadStarter pProcessingThreadStarter;
	private final ErrorHandler pErrorHandler;
	
	private final LinkCollector pLinkCollector;
	private final MetaInformationStringCollection pUnhandledMimeTypes;
	
	public Downloader(final CloseableHttpClient client, final LinkProcessor linkProcessor, final Restager restager, final ProcessingThreadStarter processingThreadStarter, final ErrorHandler errorHandler, final LinkCollector linkCollector, final MetaInformationStringCollection unhandledMimeTypes) {
		pClient = client;
		pHttpContext = HttpClientContext.create();
		
		pLinkProcessor = linkProcessor;
		pRestager = restager;
		
		pContentHandlers = new HashMap<>();
		pIgnoreMime = new HashSet<>();
		pProcessingThreadStarter = processingThreadStarter;
		pErrorHandler = errorHandler;
		
		pLinkCollector = linkCollector;
		pUnhandledMimeTypes = unhandledMimeTypes;
	}
	
	public Map<String, ContentHandler> getContentHandlers() {
		return pContentHandlers;
	}
	
	public Set<String> getIgnoreMime() {
		return pIgnoreMime;
	}
	
	public void download(final AgendaItem agendaItem) {
		final String url = agendaItem.getUrl();
		final HttpGet request;
		try {
			request = new HttpGet(url);
		} catch (final Exception e) {
			pErrorHandler.handleError(url, e);
			return;
		}
		
		ArrayList<Exception> exceptions = null;
		for (int i = 0; i < RETRIES; i++) {
			final boolean processingThreadSpawned;
			try {
				processingThreadSpawned = makeRequest(request, url, agendaItem);
			} catch (final Exception e) {
				if (exceptions == null)
					exceptions = new ArrayList<>();
				exceptions.add(e);
				continue;
			}
			if (processingThreadSpawned)
				return;
			pRestager.processingFinished(url);
			return;
		}
		
		if ((exceptions == null) || exceptions.isEmpty())
			return;
		
		logErrors(url, exceptions);
	}

	private boolean makeRequest(final HttpGet request, final String url, final AgendaItem agendaItem) throws IOException, ClientProtocolException {
		final ProcessingThreadCreator processingThreadCreator;
		try (final CloseableHttpResponse response = pClient.execute(request, pHttpContext)) {
			final Instant requestTime = Instant.now();
			
			final Header location = response.getFirstHeader("location");
			if (location != null) {
				redirect(agendaItem, url, location);
				return false;
			}
			
			final HttpEntity entity = response.getEntity();
			if (entity == null) {
				pErrorHandler.handleError(url, "Response contains only headers.");
				return false;
			}
			final Header contentType = entity.getContentType();
			if (contentType == null) {
				pErrorHandler.handleError(url, "Response does not contain a Content-Type header.");
				return false;
			}
			final String mainContentType = mainContentType(contentType.getElements());
			final ContentHandler contentHandler = findContentHandler(mainContentType);
			if (contentHandler == null) {
				if (pIgnoreMime.contains(mainContentType))
					return false;
				pUnhandledMimeTypes.add(mainContentType);
				pErrorHandler.handleError(url, "I cannot deal with this Content-Type: " + contentType.getValue());
				return false;
			}
			processingThreadCreator = contentHandler.handle(url, Math.incrementExact(agendaItem.getDepth()), requestTime, response);
		}
		if (processingThreadCreator == null)
			return false;
		pProcessingThreadStarter.startProcessingThread(processingThreadCreator);
		return true;
	}
	
	private void redirect(final AgendaItem agendaItem, final String url, final Header location) {
		final String newLocation = getNewLocation(url, location);
		final URIBuilder targetBuilder = pLinkProcessor.processLink(url, newLocation, LinkType.LINK, StandardCharsets.UTF_8);
		if (targetBuilder != null) {
			final long depth = agendaItem.getDepth();
			final String target = targetBuilder.toString();
			pLinkCollector.addLink(url, target, LinkType.REDIRECT, depth);
			pRestager.restage(target, UrlUtil.extractHost(targetBuilder), depth);
		}
	}
	
	private static String getNewLocation(final String requestedUrl, final Header location) {
		final String targetRel = location.getValue();
		if (targetRel.startsWith("//")) {
			final int substPos = requestedUrl.indexOf("//");
			if (substPos < 0)
				return "http:" + targetRel;
			return requestedUrl.substring(0, substPos) + targetRel;
		}
		if (targetRel.startsWith("/")) {
			final int hostStart = requestedUrl.indexOf("//");
			if (hostStart < 0)
				return targetRel;
			final int substPos = requestedUrl.indexOf('/', hostStart + 2);
			if (substPos < 0)
				return requestedUrl + targetRel;
			return requestedUrl.substring(0, substPos) + targetRel;
		}
		if (targetRel.contains(":"))
			return targetRel;
		final int substPos = requestedUrl.lastIndexOf('/');
		if (substPos < 0)
			return targetRel;
		else if (requestedUrl.codePointBefore(substPos) == '/')
			return requestedUrl + '/' + targetRel;
		return requestedUrl.substring(0, substPos + 1) + targetRel;
	}
	
	private ContentHandler findContentHandler(final String mainContentType) {
		return pContentHandlers.getOrDefault(mainContentType, null);
	}
	
	private void logErrors(final String url, final ArrayList<Exception> errors) {
		final ArrayList<String> messages = new ArrayList<>();
		final HashSet<String> messagesSet = new HashSet<>();
		for (final Exception error : errors) {
			final String msg = error.getClass().getCanonicalName() + ": " + error.getLocalizedMessage();
			if (messagesSet.add(msg))
				messages.add(msg);
		}
		switch (messages.size()) {
			case 0:
				pErrorHandler.handleError(url, "No usable response even after " + RETRIES_STR + " (re)tries.");
				break;
			case 1:
				pErrorHandler.handleError(url, messages.get(0) + " [" + RETRIES_STR + " (re)tries]");
				break;
			default:
				final StringBuilder messageBuilder = new StringBuilder();
				for (final String message : messages)
					messageBuilder.append(message).appendCodePoint('\n');
				messageBuilder.appendCodePoint('[').append(RETRIES_STR).append(" (re)tries]");
				pErrorHandler.handleError(url, messageBuilder.toString());
				break;
		}
	}
	
	private static String mainContentType(final HeaderElement[] parsedContentType) {
		if ((parsedContentType == null) || (parsedContentType.length != 1))
			return null;
		return parsedContentType[0].getName().toLowerCase(Locale.ROOT);
	}
	
}
