/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import herma.crawler.config.Configuration;
import herma.crawler.db.DbConnectionScheduler;
import herma.crawler.db.Delay;
import herma.crawler.db.MongoDbContext;

public class MongoDbSetup {
	
	private static final String DATABASE_NAME_KEY = "db";
	
	private static final String BACKUP_INTERVAL_KEY = "db.backupIntervalHours";
	private static final int DEFAULT_BACKUP_INTERVAL_HOURS = 24;
	
	public static MongoDbContext setupMongoDbContext(final Configuration config, final String crawlPrefix, final DbConnectionScheduler dbConnectionScheduler) {
		final String result = config.getString(DATABASE_NAME_KEY);
		if (result == null) {
			config.addConfigurationError("You have to specify a database name for MongoDB (key \"" + DATABASE_NAME_KEY + "\").");
			return null;
		}
		return new MongoDbContext(dbConnectionScheduler, result);
	}
	
	public static Delay loadBackupInterval(final Configuration config) {
		final int backupIntervalHours;
		try {
			backupIntervalHours = config.getInt(BACKUP_INTERVAL_KEY, DEFAULT_BACKUP_INTERVAL_HOURS);
		} catch (final ParseException e) {
			return null;
		}
		
		return new Delay(backupIntervalHours, TimeUnit.HOURS);
	}
	
}
