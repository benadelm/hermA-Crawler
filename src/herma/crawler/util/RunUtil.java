/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Contains helper methods for dealing with the invocation
 * of external programs.
 */
public class RunUtil {
	
	private static final int BUFFER_SIZE = 4096;
	
	public static void consumeStreamsToNowhere(final Process process) {
		startConsumerThread(process.getInputStream());
		startConsumerThread(process.getErrorStream());
	}
	
	public static void consumeErrorStreamToNowhere(final Process process) {
		startConsumerThread(process.getErrorStream());
	}
	
	private static void startConsumerThread(final InputStream stream) {
		if (stream == null)
			return;
		final ConsumerThread thread = new ConsumerThread(stream, BUFFER_SIZE);
		thread.setDaemon(true);
		thread.start();
	}
	
	private static class ConsumerThread extends Thread {
		
		private final InputStream pStream;
		private final byte[] pBuffer;
		
		public ConsumerThread(final InputStream stream, final int bufferSize) {
			pStream = stream;
			pBuffer = new byte[bufferSize];
		}
		
		@Override
		public void run() {
			while (true) {
				final int read;
				try {
					read = pStream.read(pBuffer);
				} catch (final IOException e) {
					System.err.println(e.getClass().getCanonicalName() + " beim Lesen der Ausgabe/Error-Pipe eines Subprozesses: " + e.getLocalizedMessage());
					return;
				}
				if (read < 0)
					break;
			}
		}
	}
	
}
