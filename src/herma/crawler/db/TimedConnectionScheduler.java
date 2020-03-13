/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.db;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


public class TimedConnectionScheduler implements DbConnectionScheduler {
	
	private final ScheduledExecutorService pPeriodicCloser;
	private final Delay pCloseDelay;
	private final Runnable pConnectionCloser;
	private final Object pLock;
	
	private MongoClient pClient;
	private long pNumberOfAcquiredClients;
	private boolean pClosing;
	private ScheduledFuture<?> pNextScheduledClose;
	
	public TimedConnectionScheduler(final Delay closeDelay) {
		pPeriodicCloser = Executors.newScheduledThreadPool(1);
		pCloseDelay = closeDelay;
		pConnectionCloser = this::closeConnection;
		pLock = new Object();
		
		pClient = null;
		pNumberOfAcquiredClients = 0L;
		pClosing = false;
		pNextScheduledClose = null;
	}
	
	@Override
	public MongoClient acquireClient() {
		synchronized (pLock) {
			while (pClosing || (pNumberOfAcquiredClients == Long.MAX_VALUE)) {
				try {
					pLock.wait();
				} catch (final InterruptedException e) {
					// should not happen
				}
			}
			if (pClient == null) {
				pClient = newClient();
				pNextScheduledClose = pCloseDelay.delay(pPeriodicCloser, pConnectionCloser);
			}
			pNumberOfAcquiredClients++;
			return pClient;
		}
	}
	
	private static MongoClient newClient() {
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
		synchronized (pLock) {
			if (client != pClient)
				return;
			pNumberOfAcquiredClients--;
			pLock.notifyAll();
		}
	}
	
	@Override
	public void shutdown() {
		synchronized (pLock) {
			cancelNextScheduledClose();
			pPeriodicCloser.shutdown();
		}
		closeConnection();
	}
	
	private void closeConnection() {
		synchronized (pLock) {
			if (pClosing)
				return;
			if (pClient == null)
				return;
			cancelNextScheduledClose();
			pClosing = true;
			while (pNumberOfAcquiredClients > 0) {
				try {
					pLock.wait();
				} catch (final InterruptedException e) {
					// ?
				}
			}
			pClient.close();
			pClient = null;
			pClosing = false;
			pLock.notifyAll();
		}
	}
	
	private void cancelNextScheduledClose() {
		if (pNextScheduledClose != null) {
			pNextScheduledClose.cancel(false);
			pNextScheduledClose = null;
		}
	}
	
}
