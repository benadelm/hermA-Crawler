/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers;

import java.util.concurrent.Semaphore;

import herma.crawler.ErrorHandler;
import herma.crawler.agenda.AgendaIn;
import herma.crawler.meta.MetaInformationStringCollection;
import herma.crawler.relevance.RelevanceDecider;

public abstract class ContentHandlerThread extends Thread {
	
	// TODO: smelly class design (protected fields)
	// however, it fulfils the intention of code reduction in the subclasses
	// (the subclasses do not have to load error handler etc. themselves)
	
	private final Semaphore pThreadSemaphore;
	private final MetaInformationStringCollection pProcessedUrls;
	
	protected final String pUrl;
	
	protected final AgendaIn pAgenda;

	protected final ErrorHandler pErrorHandler;
	protected final RelevanceDecider pRelevanceDecider;
	
	public ContentHandlerThread(final Semaphore threadSemaphore, final String url, final ContentHandlerContext context) {
		pThreadSemaphore = threadSemaphore;
		pProcessedUrls = context.getProcessedUrls();
		
		pUrl = url;
		
		pAgenda = context.getAgenda();
		
		pErrorHandler = context.getErrorHandler();
		pRelevanceDecider = context.getRelevanceDecider();
	}
	
	@Override
	public void run() {
		try {
			try {
				try {
					handleContent();
				} finally {
					pAgenda.processingFinished(pUrl);
					pProcessedUrls.add(pUrl);
				}
			} finally {
				pAgenda.decrementPendingCount();
			}
		} finally {
			pThreadSemaphore.release();
		}
	}
	
	protected abstract void handleContent();
	
}
