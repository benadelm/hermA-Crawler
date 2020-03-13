/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import herma.crawler.config.Configuration;
import herma.crawler.db.Delay;
import herma.crawler.db.TimedConnectionScheduler;

public class TimedConnectionSchedulerSetup {
	
	private static final int DEFAULT_DB_CLOSE_DELAY_HOURS = 24;
	
	public static TimedConnectionScheduler setupTimedConnectionScheduler(final Configuration config) {
		final int dbCloseDelayHours;
		try {
			dbCloseDelayHours = config.getInt("db.closeDelayHours", DEFAULT_DB_CLOSE_DELAY_HOURS);
		} catch (final ParseException e) {
			return null;
		}
		
		return new TimedConnectionScheduler(new Delay(dbCloseDelayHours, TimeUnit.HOURS));
	}
	
}
