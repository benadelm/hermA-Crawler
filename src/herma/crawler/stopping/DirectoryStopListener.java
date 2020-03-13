/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.stopping;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectoryStopListener implements StopListener {
	
	private static final String STOP_FILENAME = "stop";
	
	private final WatchThread pWatchThread;
	
	public DirectoryStopListener(final Path watchDir, final Runnable stopper) {
		pWatchThread = new WatchThread(watchDir, stopper);
	}
	
	@Override
	public void startListening() {
		pWatchThread.start();
	}
	
	@Override
	public void stopListening() {
		pWatchThread.stopWatching();
	}
	
	private static class WatchThread extends Thread {
		
		private final Path pWatchDir;
		private final Runnable pStopper;
		
		private final AtomicBoolean pWatching;
		
		public WatchThread(final Path watchDir, final Runnable stopper) {
			pWatchDir = watchDir;
			pStopper = stopper;
			
			pWatching = new AtomicBoolean(true);
		}
		
		@Override
		public void run() {
			final Path awaitedFile = pWatchDir.resolve(STOP_FILENAME);
			try (final WatchService watchService = pWatchDir.getFileSystem().newWatchService()) {
				
				pWatchDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.OVERFLOW);
				
				while (pWatching.get()) {
					final WatchKey key;
					try {
						key = watchService.take();
					} catch (final InterruptedException x) {
						continue;
					}
					
					if (key.pollEvents().size() > 0) {
						if (Files.deleteIfExists(awaitedFile)) {
							pStopper.run();
							return;
						}
					}
					
					if (key.reset())
						continue;
					
					throw new IllegalStateException("The WatchKey is no longer valid (reset returned false)");
				}
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		
		public void stopWatching() {
			pWatching.set(false);
			interrupt();
		}
		
	}
	
}
