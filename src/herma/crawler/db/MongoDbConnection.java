/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDbConnection implements AutoCloseable {
	
	private final DbConnectionScheduler pScheduler;
	private final MongoClient pClient;
	private final MongoDatabase pDatabase;
	
	MongoDbConnection(final DbConnectionScheduler scheduler, final MongoClient client, final MongoDatabase database) {
		pScheduler = scheduler;
		pClient = client;
		pDatabase = database;
	}
	
	public MongoDatabase getDatabase() {
		return pDatabase;
	}
	
	@Override
	public void close() {
		pScheduler.releaseClient(pClient);
	}
	
}
