/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.text.ParseException;
import java.util.concurrent.Semaphore;

import herma.crawler.ThreadManager;
import herma.crawler.config.Configuration;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.contenthandlers.ProcessingThreadStarter;

public class ProcessingThreadStarterSetup {
	
	private static final String MAX_PROCESSING_THREADS_KEY = "maxProcessingThreads";
	
	public static ProcessingThreadStarter setupProcessingThreadStarter(final Configuration config, final ThreadManager threadManager, final ContentHandlerContext contentHandlerContext) {
		if (threadManager == null)
			return null;
		if (contentHandlerContext == null)
			return null;
		
		final Integer maxProcessingThreadsObj;
		try {
			maxProcessingThreadsObj = config.getInt(MAX_PROCESSING_THREADS_KEY);
		} catch (final ParseException e) {
			return null;
		}
		
		if (maxProcessingThreadsObj == null) {
			config.addConfigurationError("You have to specify a maximum number of processing threads (key \"" + MAX_PROCESSING_THREADS_KEY + "\").");
			return null;
		}
		
		final int maxProcessingThreads = maxProcessingThreadsObj.intValue();
		if (maxProcessingThreads > 0) {
			final Semaphore threadSemaphore = new Semaphore(maxProcessingThreads, true);
			return new ProcessingThreadStarter(threadManager, threadSemaphore, contentHandlerContext, contentHandlerContext.getAgenda());
		}
		
		config.addConfigurationError("The maximum number of processing threads (key \"" + MAX_PROCESSING_THREADS_KEY + "\") must be greater than zero.");
		return null;
	}
	
}
