/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


public class OpenNewDbConnectionScheduler implements DbConnectionScheduler {
	
	@Override
	public MongoClient acquireClient() {
		MongoClient mongoClient = null;
		try {
			mongoClient = MongoClients.create();
		} catch (final Exception e) {
			if (mongoClient != null)
				mongoClient.close();
			throw e;
		}
		return mongoClient;
	}
	
	@Override
	public void releaseClient(final MongoClient client) {
		client.close();
	}
	
	@Override
	public void shutdown() {
		// keine Aktion erforderlich
	}
	
}
