/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.agenda;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class MemoryAgendaBackupThread extends Thread {
	
	private static final long BACKUP_INTERVAL_MS = 86400000L; // 24h
	
	private final MemoryAgenda pAgenda;
	
	private final Path pQueueFile;
	private final Path pVisitedUrlsFile;
	private final Path pResultCountByHostFile;
	
	private final AtomicBoolean pRunning;
	
	public MemoryAgendaBackupThread(final MemoryAgenda agenda, final Path queueFile, final Path visitedUrlsFile, final Path resultCountByHostFile) {
		pAgenda = agenda;
		
		pQueueFile = queueFile;
		pVisitedUrlsFile = visitedUrlsFile;
		pResultCountByHostFile = resultCountByHostFile;
		
		pRunning = new AtomicBoolean(true);
	}
	
	public void terminate() {
		pRunning.set(false);
		interrupt();
	}
	
	@Override
	public void run() {
		while (pRunning.get()) {
			pAgenda.save(pQueueFile, pVisitedUrlsFile, pResultCountByHostFile);
			// sleep löst auch dann eine InterruptedException aus,
			// wenn das interrupt() vor seinem Eintritt ausgeführt wurde
			// (d.h. wenn das Interrupt-Flag des Threads beim Eintritt
			// in die sleep-Methode schon gesetzt ist)
			try {
				Thread.sleep(BACKUP_INTERVAL_MS);
			} catch (final InterruptedException e) {
				// ignorieren
			}
		}
	}
	
}
