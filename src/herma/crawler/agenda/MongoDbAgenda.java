/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.agenda;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import herma.crawler.Restager;
import herma.crawler.db.MongoDbConnection;
import herma.crawler.db.MongoDbContext;

public class MongoDbAgenda implements AgendaIn, AgendaOut, Restager {
	
	private static final Integer INTEGER_ZERO = Integer.valueOf(0);
	private static final Integer INTEGER_ONE = Integer.valueOf(1);
	private static final Long LONG_ZERO = Long.valueOf(0L);
	private static final Long LONG_ONE = Long.valueOf(1L);
	
	private static final String COLLECTION_AGENDA = "agenda";
	private static final String COLLECTION_RESULT_COUNTS_BY_HOST = "resultcountsbyhost";
	
	private static final String FIELD_ID = "_id";
	
	private static final String FIELD_CATEGORY = "cat";
	private static final String FIELD_STATE = "state";
	private static final String FIELD_URL = "url";
	private static final String FIELD_DEPTH = "depth";
	
	private static final String FIELD_HOST = "host";
	private static final String FIELD_RESULTSCOUNT = "resultsCount";
	
	private static final Integer CAT_NORMAL = INTEGER_ZERO;
	private static final Integer CAT_REDIRECT = INTEGER_ONE;
	
	private static final Integer STATE_ENQUEUED = INTEGER_ZERO;
	private static final Integer STATE_DEQUEUED = INTEGER_ONE;
	private static final Integer STATE_PROCESSED = Integer.valueOf(2);
	
	private static final Bson FILTER_STATE_ENQUEUED = Filters.eq(FIELD_STATE, STATE_ENQUEUED);
	private static final Bson QUERY_STATE_DEQUEUED = Filters.eq(FIELD_STATE, STATE_DEQUEUED);
	private static final Bson QUERY_NORMAL = Filters.and(FILTER_STATE_ENQUEUED, Filters.eq(FIELD_CATEGORY, CAT_NORMAL));
	private static final Bson QUERY_REDIRECT = Filters.and(FILTER_STATE_ENQUEUED, Filters.eq(FIELD_CATEGORY, CAT_REDIRECT));
	
	private static final Bson SORT_ID_ASC = Sorts.ascending(FIELD_ID);
	private static final Bson SORT_ID_DESC = Sorts.descending(FIELD_ID);
	
	private static final Bson SET_CATEGORY_TO_REDIRECT = Updates.set(FIELD_CATEGORY, CAT_REDIRECT);
	private static final Bson UPDATE_RESET_STATE = Updates.set(FIELD_STATE, STATE_ENQUEUED);
	private static final Bson UPDATE_DEQUEUE = Updates.set(FIELD_STATE, STATE_DEQUEUED);
	private static final Bson UPDATE_PROCESSED = Updates.set(FIELD_STATE, STATE_PROCESSED);
	
	private static final Bson PROJECTION_RESULTSCOUNTS = Projections.include(FIELD_HOST, FIELD_RESULTSCOUNT);
	
	private static final UpdateOptions OPTIONS_UPSERT;
	private static final UpdateOptions OPTIONS_NO_UPSERT;
	private static final FindOneAndUpdateOptions DEQUEUE_OPTIONS_NORMAL;
	private static final FindOneAndUpdateOptions DEQUEUE_OPTIONS_REDIRECT;
	
	private final MongoDbContext pDb;
	private final Object pSynchronizer; // TODO: using something like java.util.concurrent.locks.ReadWriteLock instead to protect pAbort could improve performance
	
	private final AtomicInteger pPendingCount = new AtomicInteger(0);
	
	private boolean pAbort = false;
	
	static {
		DEQUEUE_OPTIONS_REDIRECT = new FindOneAndUpdateOptions();
		DEQUEUE_OPTIONS_REDIRECT.sort(SORT_ID_DESC);
		DEQUEUE_OPTIONS_REDIRECT.upsert(false);
		
		DEQUEUE_OPTIONS_NORMAL = new FindOneAndUpdateOptions();
		DEQUEUE_OPTIONS_NORMAL.sort(SORT_ID_ASC);
		DEQUEUE_OPTIONS_NORMAL.upsert(false);
		
		OPTIONS_UPSERT = new UpdateOptions();
		OPTIONS_UPSERT.upsert(true);
		
		OPTIONS_NO_UPSERT = new UpdateOptions();
		OPTIONS_NO_UPSERT.upsert(false);
	}
	
	public MongoDbAgenda(final MongoDbContext db) {
		pDb = db;
		pSynchronizer = new Object();
		createCollections();
	}
	
	private void createCollections() {
		try (final MongoDbConnection connection = pDb.openConnection()) {
			createCollectionAgenda(connection);
			createCollectionResultCountsByHost(connection);
		}
	}
	
	private void createCollectionAgenda(final MongoDbConnection connection) {
		final MongoCollection<Document> agenda = getAgenda(connection);
		agenda.createIndex(Indexes.ascending(FIELD_ID));
		agenda.createIndex(Indexes.hashed(FIELD_CATEGORY));
		agenda.createIndex(Indexes.hashed(FIELD_STATE));
		agenda.createIndex(Indexes.hashed(FIELD_URL));
	}
	
	private void createCollectionResultCountsByHost(final MongoDbConnection connection) {
		final MongoCollection<Document> resultCountsByHost = getResultCountsByHost(connection);
		resultCountsByHost.createIndex(Indexes.hashed(FIELD_HOST));
	}
	
	public void resetStates() {
		try (final MongoDbConnection connection = pDb.openConnection()) {
			getAgenda(connection).updateMany(QUERY_STATE_DEQUEUED, UPDATE_RESET_STATE, OPTIONS_NO_UPSERT);
		}
	}
	
	@Override
	public void incrementPendingCount() {
		pPendingCount.incrementAndGet();
	}
	
	@Override
	public void elementProcessed() {
		decrementPendingCountInternal();
	}

	@Override
	public void decrementPendingCount() {
		decrementPendingCountInternal();
	}

	private void decrementPendingCountInternal() {
		if (pPendingCount.decrementAndGet() == 0) {
			synchronized (pSynchronizer) {
				pSynchronizer.notifyAll();
			}
		}
	}
	
	public void abort() {
		synchronized (pSynchronizer) {
			pAbort = true;
			pSynchronizer.notifyAll();
		}
	}

	@Override
	public void add(final String url, final String host, final long depth) {
		tryEnqueue(createUrlFilter(url), Updates.setOnInsert(createAddDocument(url, depth)));
		registerHost(host);
	}

	private static Document createAddDocument(final String url, final long depth) {
		final Document newDocument = new Document();
		newDocument.append(FIELD_CATEGORY, CAT_NORMAL);
		newDocument.append(FIELD_DEPTH, Long.valueOf(depth));
		setupNewQueueItem(newDocument, url);
		return newDocument;
	}
	
	@Override
	public void restage(final String url, final String host, final long depth) {
		tryEnqueue(createUrlFilter(url), Updates.combine(SET_CATEGORY_TO_REDIRECT, Updates.min(FIELD_DEPTH, Long.valueOf(depth)), Updates.setOnInsert(createRestageDocument(url))));
		registerHost(host);
	}

	private static Document createRestageDocument(final String url) {
		final Document newDocument = new Document();
		setupNewQueueItem(newDocument, url);
		return newDocument;
	}

	private static void setupNewQueueItem(final Document document, final String url) {
		document.append(FIELD_URL, url);
		document.append(FIELD_STATE, STATE_ENQUEUED);
	}

	private void tryEnqueue(final Bson select, final Bson update) {
		notifyWaitingDequeuers(addToDatabase(select, update, OPTIONS_UPSERT));
	}

	private boolean addToDatabase(final Bson select, final Bson update, final UpdateOptions updateOptions) {
		try (final MongoDbConnection connection = pDb.openConnection()) {
			return (getAgenda(connection).updateOne(select, update, updateOptions).getMatchedCount() == 0);
		}
	}

	private void notifyWaitingDequeuers(final boolean added) {
		if (added) {
			synchronized (pSynchronizer) {
				pSynchronizer.notifyAll();
			}
		}
	}

	private void registerHost(final String host) {
		final Bson select = createHostFilter(host);
		final Bson update = Updates.setOnInsert(createHostRegisterDocument(host));
		
		try (final MongoDbConnection connection = pDb.openConnection()) {
			addHostToDatabase(getResultCountsByHost(connection), select, update);
		}
	}

	@Override
	public AgendaItem getNext() throws InterruptedException {
		try (final MongoDbConnection connection = pDb.openConnection()) {
			return toAgendaItem(getNext(getAgenda(connection)));
		}
	}

	private Document getNext(final MongoCollection<Document> agenda) throws InterruptedException {
		synchronized (pSynchronizer) {
			while (true) {
				if (pAbort)
					return null;
				{
					final Document redirect = tryDequeue(agenda, QUERY_REDIRECT, DEQUEUE_OPTIONS_REDIRECT);
					if (redirect != null) {
						pPendingCount.incrementAndGet();
						return redirect;
					}
				}
				{
					final Document normal = tryDequeue(agenda, QUERY_NORMAL, DEQUEUE_OPTIONS_NORMAL);
					if (normal != null) {
						pPendingCount.incrementAndGet();
						return normal;
					}
				}
				if (pPendingCount.get() == 0)
					return null;
				pSynchronizer.wait();
			}
		}
	}

	private Document tryDequeue(final MongoCollection<Document> agenda, Bson query, FindOneAndUpdateOptions queryOptions) {
		return agenda.findOneAndUpdate(query, UPDATE_DEQUEUE, queryOptions);
	}
	
	private static AgendaItem toAgendaItem(final Document document) {
		if (document == null)
			return null;
		return new AgendaItem(document.getString(FIELD_URL), document.getLong(FIELD_DEPTH).longValue());
	}
	
	@Override
	public void relevantResultFoundFor(final String host) {
		final Bson select = createHostFilter(host);
		final Bson insert = Updates.setOnInsert(createHostRegisterDocument(host));
		final Bson update = Updates.inc(FIELD_RESULTSCOUNT, LONG_ONE);
		
		try (final MongoDbConnection connection = pDb.openConnection()) {
			final MongoCollection<Document> resultCountsByHost = getResultCountsByHost(connection);
			addHostToDatabase(resultCountsByHost, select, insert);
			resultCountsByHost.updateOne(select, update, OPTIONS_UPSERT);
		}
	}

	private static Bson createHostFilter(final String host) {
		return Filters.eq(FIELD_HOST, host);
	}

	private static Document createHostRegisterDocument(final String host) {
		final Document newDocument = new Document();
		newDocument.append(FIELD_HOST, host);
		newDocument.append(FIELD_RESULTSCOUNT, LONG_ZERO);
		return newDocument;
	}

	private static void addHostToDatabase(final MongoCollection<Document> resultCountsByHost, final Bson select, final Bson update) {
		resultCountsByHost.updateOne(select, update, OPTIONS_UPSERT);
	}
	
	@Override
	public void processingFinished(final String url) {
		final Bson select = createUrlFilter(url);
		try (final MongoDbConnection connection = pDb.openConnection()) {
			getAgenda(connection).updateOne(select, UPDATE_PROCESSED, OPTIONS_NO_UPSERT);
		}
	}

	private static MongoCollection<Document> getAgenda(final MongoDbConnection connection) {
		return getCollection(connection, COLLECTION_AGENDA);
	}

	private static Bson createUrlFilter(final String url) {
		return Filters.eq(FIELD_URL, url);
	}
	
	public void save(final Path resultCountByHostFile) {
		try {
			writeResultCounts(resultCountByHostFile);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void writeResultCounts(final Path resultCountByHostFile) throws IOException {
		final Path resultCountsBackup = backupFile(resultCountByHostFile);
		try (final BufferedWriter writer = Files.newBufferedWriter(resultCountByHostFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			try (final MongoDbConnection connection = pDb.openConnection()) {
				final MongoCollection<Document> resultCountsByHost = getResultCountsByHost(connection);
				for (final Document entry : resultCountsByHost.find().projection(PROJECTION_RESULTSCOUNTS)) {
					final String host = entry.getString(FIELD_HOST);
					if (host != null)
						writer.write(host);
					writer.write('\t');
					final Long resultsCount = entry.getLong(FIELD_RESULTSCOUNT);
					if (resultsCount != null)
						writer.write(resultsCount.toString());
					writer.write('\n');
				}
			}
			writer.flush();
		}
		Files.deleteIfExists(resultCountsBackup);
	}
	
	private static MongoCollection<Document> getResultCountsByHost(final MongoDbConnection connection) {
		return getCollection(connection, COLLECTION_RESULT_COUNTS_BY_HOST);
	}

	private static MongoCollection<Document> getCollection(final MongoDbConnection connection, final String collectionName) {
		return connection.getDatabase().getCollection(collectionName);
	}
	
	private static Path backupFile(final Path file) throws IOException {
		final Path temp = Files.createTempFile(file.getParent(), file.getFileName().toString(), "");
		try {
			try {
				Files.move(file, temp, StandardCopyOption.ATOMIC_MOVE);
			} catch (final NoSuchFileException e) {
				// file does not exist; nothing to copy
			} catch (final IOException e) {
				Files.move(file, temp);
			}
		} catch (final Exception e) {
			Files.deleteIfExists(temp);
			throw e;
		}
		return temp;
	}
	
}
