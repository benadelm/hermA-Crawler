/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.meta;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import herma.crawler.db.MongoDbConnection;
import herma.crawler.db.MongoDbContext;

public class MongoDbStringSet implements StringSet {
	
	private static final String KEY = "key";
	private static final UpdateOptions UPDATE_OPTIONS;
	
	private final MongoDbContext pDb;
	private final String pCollectionName;
	
	static {
		UPDATE_OPTIONS = new UpdateOptions();
		UPDATE_OPTIONS.upsert(true);
	}
	
	public MongoDbStringSet(final MongoDbContext db, final String collectionName) {
		pDb = db;
		pCollectionName = collectionName;
		createCollection();
	}
	
	private void createCollection() {
		final Bson index = Indexes.hashed(KEY);
		final IndexOptions indexOptions = new IndexOptions();
		
		try (final MongoDbConnection connection = pDb.openConnection()) {
			getSet(connection).createIndex(index, indexOptions);
		}
	}
	
	@Override
	public boolean add(final String str) {
		final Bson filter = Filters.eq(KEY, str);
		final Bson update = Updates.setOnInsert(KEY, str);
		
		try (final MongoDbConnection connection = pDb.openConnection()) {
			return (getSet(connection).updateOne(filter, update, UPDATE_OPTIONS).getMatchedCount() == 0);
		}
	}
	
	private MongoCollection<Document> getSet(final MongoDbConnection connection) {
		return connection.getDatabase().getCollection(pCollectionName);
	}
	
}
