/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import herma.crawler.agenda.AgendaItem;
import herma.crawler.agenda.AgendaOut;

public class DownloadThread extends Thread {
	
	private final AgendaOut pAgenda;
	private final Downloader pDownloader;
	
	public DownloadThread(final AgendaOut agenda, final Downloader downloader) {
		pAgenda = agenda;
		pDownloader = downloader;
	}
	
	@Override
	public void run() {
		while (true) {
			final AgendaItem next;
			try {
				next = pAgenda.getNext();
			} catch (final InterruptedException e) {
				break;
			}
			if (next == null)
				break;
			try {
				pDownloader.download(next);
			} catch (final Exception e) {
				System.err.println("\n\n******** " + e.getClass().getCanonicalName() + " in downloader ********\n");
			} finally {
				pAgenda.elementProcessed();
			}
		}
	}
	
}
