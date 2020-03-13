/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.db;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Delay {
	
	private final long pDelay;
	private final TimeUnit pTimeUnit;
	
	public Delay(final long delay, final TimeUnit timeUnit) {
		pDelay = delay;
		pTimeUnit = timeUnit;
	}
	
	public ScheduledFuture<?> delay(final ScheduledExecutorService scheduledExecutorService, final Runnable runnable) {
		return scheduledExecutorService.schedule(runnable, pDelay, pTimeUnit);
	}
}
