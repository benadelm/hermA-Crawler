/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.util.concurrent.Semaphore;

import herma.crawler.contenthandlers.ContentHandlerContext;

/**
 * Can create threads for further processing of content.
 */
@FunctionalInterface
public interface ProcessingThreadCreator {
	
	/**
	 * Creates, but does not start, a new processing thread.
	 * 
	 * @param threadSemaphore
	 * a {@link Semaphore} the thread to create may use
	 * in case it has to spawn further threads; not {@code null}
	 * 
	 * @param context
	 * a {@link ContentHandlerContext} instance with information
	 * usual processing threads will need; not {@code null}
	 * 
	 * @return
	 * a newly created, not yet started processing thread;
	 * not {@code null}
	 */
	Thread createThread(Semaphore threadSemaphore, ContentHandlerContext context);
	
}
