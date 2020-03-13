/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.agenda;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import herma.crawler.db.Delay;

public class MongoDbAgendaBackupThread {
	
	private final MongoDbAgenda pAgenda;
	
	private final Path pResultCountByHostFile;
	
	private final Delay pSaveDelay;
	private final ScheduledExecutorService pPeriodicSaver;
	private ScheduledFuture<?> pNextScheduledSave;
	
	private final Runnable pTask;
	
	public MongoDbAgendaBackupThread(final MongoDbAgenda agenda, final Path resultCountByHostFile, final Delay saveDelay) {
		pAgenda = agenda;
		
		pResultCountByHostFile = resultCountByHostFile;
		
		pSaveDelay = saveDelay;
		pPeriodicSaver = Executors.newScheduledThreadPool(1);
		
		pTask = this::save;
	}
	
	public void start() {
		synchronized (pPeriodicSaver) {
			pNextScheduledSave = pPeriodicSaver.schedule(pTask, 0L, TimeUnit.MILLISECONDS);
		}
	}
	
	public void terminate() {
		synchronized (pPeriodicSaver) {
			cancelScheduledTask();
			pPeriodicSaver.shutdown();
		}
		try {
			while (!pPeriodicSaver.awaitTermination(1L, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void save() {
		synchronized (pPeriodicSaver) {
			cancelScheduledTask();
		}
		pAgenda.save(pResultCountByHostFile);
		synchronized (pPeriodicSaver) {
			if (pPeriodicSaver.isShutdown())
				return;
			pNextScheduledSave = pSaveDelay.delay(pPeriodicSaver, pTask);
		}
	}
	
	private void cancelScheduledTask() {
		pNextScheduledSave.cancel(false);
	}
	
}
