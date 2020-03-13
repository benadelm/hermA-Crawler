/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.db;

import com.mongodb.client.MongoClient;

/**
 * Manages connections to a MongoDb database.
 */
public interface DbConnectionScheduler {
	
	/**
	 * Returns a {@link MongoClient} that can be used
	 * to access a MongoDb database.
	 * <p>
	 * The returned {@link MongoClient} instance
	 * <b>must</b> be released with
	 * {@link #releaseClient(MongoClient)}
	 * after using it. It is highly recommended
	 * to use a <i>try-finally</i> block for that:
	 * </p>
	 * <pre>final MongoClient mongoClient = dbConnectionScheduler.acquireClient();
	 *try {
	 *	// access database
	 *} finally {
	 *	dbConnectionScheduler.releaseClient(mongoClient);
	 *}</pre>
	 * <p>
	 * A caller failing to properly release the {@link MongoClient}
	 * obtained by a call to this method
	 * may cause deadlocks or other highly undesirable side effects.
	 * </p>
	 * 
	 * @return
	 * a {@link MongoClient}, not {@code null}
	 */
	MongoClient acquireClient();
	
	/**
	 * Releases a {@link MongoClient} obtained from
	 * {@link #acquireClient()}.
	 * <p>
	 * Due to connection sharing, depending on the implementation,
	 * the {@link MongoClient} may still be usable after this
	 * method returns. However, it might become unusable at any time
	 * and thus must not be used at all after this method
	 * has been called on it.
	 * </p>
	 * 
	 * @param client
	 * the {@link MongoClient} to be released
	 */
	void releaseClient(MongoClient client);
	
	/**
	 * Informs this connection scheduler that new connections
	 * will not be requested anymore.
	 * <p>
	 * Any call to {@link #acquireClient()} after this method
	 * returns is illegal and may yield unexpected results.
	 * Any call to {@link #releaseClient(MongoClient)}
	 * should have returned before this method is called.
	 * This method may block until every {@link MongoClient}
	 * has been released.
	 * </p>
	 */
	void shutdown();
	
}
