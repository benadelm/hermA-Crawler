/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers;

import java.util.concurrent.Semaphore;

import herma.crawler.ProcessingThreadCreator;
import herma.crawler.ThreadManager;
import herma.crawler.agenda.AgendaIn;

public class ProcessingThreadStarter {

	private final ThreadManager pThreadManager;
	private final Semaphore pThreadSemaphore;
	
	private final ContentHandlerContext pContext;
	
	private final AgendaIn pAgenda;
	
	public ProcessingThreadStarter(final ThreadManager threadManager, final Semaphore threadSemaphore, final ContentHandlerContext context, final AgendaIn agenda) {
		pThreadManager = threadManager;
		pThreadSemaphore = threadSemaphore;
		
		pContext = context;
		
		pAgenda = agenda;
	}
	
	public void startProcessingThread(final ProcessingThreadCreator threadCreator) {
		pAgenda.incrementPendingCount();
		try {
			pThreadSemaphore.acquireUninterruptibly();
			try {
				final Thread thread = threadCreator.createThread(pThreadSemaphore, pContext);
				pThreadManager.addThread(thread);
				thread.start();
			} catch (final Exception e) {
				pThreadSemaphore.release();
				throw e;
			}
		} catch (final Exception e) {
			pAgenda.decrementPendingCount();
			throw e;
		}
	}
}
