/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.db;

import com.mongodb.client.MongoClient;

public class MongoDbContext {
	
	private final DbConnectionScheduler pScheduler;
	private final String pDatabaseName;
	
	public MongoDbContext(final DbConnectionScheduler scheduler, final String databaseName) {
		pScheduler = scheduler;
		pDatabaseName = databaseName;
	}
	
	public MongoDbConnection openConnection() {
		final MongoClient mongoClient = pScheduler.acquireClient();
		try {
			return new MongoDbConnection(pScheduler, mongoClient, mongoClient.getDatabase(pDatabaseName));
		} catch (final Exception e) {
			pScheduler.releaseClient(mongoClient);
			throw e;
		}
	}
	
}
