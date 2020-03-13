/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers;

import herma.crawler.ErrorHandler;
import herma.crawler.agenda.AgendaIn;
import herma.crawler.meta.LinkCollector;
import herma.crawler.meta.MetaInformationStringCollection;
import herma.crawler.relevance.RelevanceDecider;

public class ContentHandlerContext {
	
	private final AgendaIn pAgenda;
	
	private final ErrorHandler pErrorHandler;
	private final RelevanceDecider pRelevanceDecider;
	
	private final LinkCollector pLinkCollector;
	private final MetaInformationStringCollection pProcessedUrls; // TODO: really in this class?
	
	public ContentHandlerContext(final AgendaIn agenda, final ErrorHandler errorHandler, final RelevanceDecider relevanceDecider, final LinkCollector linkCollector, final MetaInformationStringCollection processedUrlsCollection) {
		pAgenda = agenda;
		
		pErrorHandler = errorHandler;
		pRelevanceDecider = relevanceDecider;
		
		pLinkCollector = linkCollector;
		pProcessedUrls = processedUrlsCollection;
	}
	
	public final AgendaIn getAgenda() {
		return pAgenda;
	}
	
	public final ErrorHandler getErrorHandler() {
		return pErrorHandler;
	}
	
	public final RelevanceDecider getRelevanceDecider() {
		return pRelevanceDecider;
	}
	
	public final LinkCollector getLinkCollector() {
		return pLinkCollector;
	}
	
	public final MetaInformationStringCollection getProcessedUrls() {
		return pProcessedUrls;
	}
}
