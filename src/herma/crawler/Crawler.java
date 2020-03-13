/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import herma.crawler.agenda.AgendaOut;

/**
 * Contains a static method that starts the web crawler.
 */
public class Crawler {
	
	/**
	 * Starts the crawler and waits for it to terminate.
	 * <p>
	 * Termination is awaited by calling
	 * {@link ThreadManager#join()}
	 * for the given
	 * {@link ThreadManager}.
	 * </p>
	 * <p>
	 * The download threads are added to the
	 * {@link ThreadManager},
	 * so this method waits at least for all
	 * download threads to terminate,
	 * which happens when the agenda signals emptiness
	 * (because it is actually empty or, for example,
	 * because the crawler has been signalled to stop).
	 * A download thread will also terminate
	 * if there is an uncaught exception
	 * in {@link AgendaOut#getNext()}
	 * (except for {@link InterruptedException}).
	 * </p>
	 * <p>
	 * This method constructs a {@link CloseableHttpClient}
	 * from the given {@link HttpClientBuilder}
	 * and disposes
	 * ({@link CloseableHttpClient#close()})
	 * it properly before it returns.
	 * Any {@link IOException} thrown in the course of that
	 * will make this method throw an {@link UncheckedIOException}.
	 * </p>
	 * <p>
	 * If the thread calling this method is interrupted
	 * while waiting on
	 * {@link ThreadManager#join()},
	 * the {@link InterruptedException}
	 * is re-thrown as a {@link RuntimeException}.
	 * A thread blocked in an invocation of this method
	 * should not be interrupted. 
	 * </p>
	 * 
	 * @param nDownloadThreads
	 * how many download threads to start;
	 * if this is zero or negative,
	 * no download threads will be started
	 * 
	 * @param httpClientBuilder
	 * the
	 * {@link HttpClientBuilder}
	 * from which the
	 * {@link CloseableHttpClient}
	 * for downloading
	 * will be constructed
	 * 
	 * @param threadManager
	 * the
	 * {@link ThreadManager}
	 * to which the download threads
	 * will be added
	 * ({@link ThreadManager#addThread(Thread)})
	 * and whose
	 * {@link ThreadManager#join()}
	 * method will be used to await termination
	 * 
	 * @param agenda
	 * the crawling agenda
	 * 
	 * @param downloaderBuilder
	 * a
	 * {@link DownloaderBuilder}
	 * for constructing
	 * {@link Downloader}
	 * instances for the download threads
	 * 
	 * @param afterStartOperation
	 * an operation to be performed
	 * after starting the download threads,
	 * but before calling
	 * {@link ThreadManager#join()}
	 * 
	 * @see ThreadManager
	 * @see DownloaderBuilder
	 */
	public static void run(final int nDownloadThreads, final HttpClientBuilder httpClientBuilder, final ThreadManager threadManager, final AgendaOut agenda, final DownloaderBuilder downloaderBuilder, final Runnable afterStartOperation) {
		try (final CloseableHttpClient client = httpClientBuilder.build()) {
			downloaderBuilder.setClient(client);
			startDownloadThreads(nDownloadThreads, threadManager, agenda, downloaderBuilder);
			
			afterStartOperation.run();
			
			try {
				threadManager.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (final IOException e) {
			System.err.println(e.getClass().getTypeName() + " creating/closing HTTP client: " + e.getLocalizedMessage());
		}
	}
	
	private static void startDownloadThreads(final int nDownloadThreads, final ThreadManager threadManager, final AgendaOut agenda, final DownloaderBuilder downloaderBuilder) {
		for (int i = 0; i < nDownloadThreads; i++) {
			final DownloadThread thread = new DownloadThread(agenda, downloaderBuilder.build());
			thread.setName("download thread #" + Integer.toString(i));
			threadManager.addThread(thread);
			thread.start();
		}
	}
	
}
