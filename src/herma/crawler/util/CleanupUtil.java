/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.util;

/**
 * Contains helper methods for certain cleanup operations.
 */
public class CleanupUtil {
	
	private static final String DEFAULT_LOCATION_STRING = "in a cleanup operation";
	
	/**
	 * Performs the given cleanup operation,
	 * which may throw an exception.
	 * If an exception is thrown,
	 * it is caught and does not bubble up
	 * to the caller of this method.
	 * <p>
	 * This method calls
	 * {@link #nothrowCleanup(Cleanup, String)}
	 * with
	 * {@value #DEFAULT_LOCATION_STRING}
	 * as second parameter.
	 * </p>
	 * 
	 * @param cleanup
	 * the cleanup operation
	 */
	public static void nothrowCleanup(final Cleanup cleanup) {
		nothrowCleanup(cleanup, DEFAULT_LOCATION_STRING);
	}
	
	/**
	 * Performs the given cleanup operation,
	 * which may throw an exception.
	 * If an exception is thrown,
	 * it is caught and does not bubble up
	 * to the caller of this method.
	 * 
	 * @param cleanup
	 * the cleanup operation
	 * 
	 * @param location
	 * some informative {@link String}
	 * describing the cleanup operation
	 * (for instance, {@code "closing a file"})
	 * for logging purposes
	 */
	public static void nothrowCleanup(final Cleanup cleanup, final String location) {
		try {
			cleanup.cleanup();
		} catch (final Throwable e) {
			System.err.println(e.getClass().getTypeName() + ' ' + location + ": " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * A functional interface for cleanup operations
	 * which may throw any kind of exception.
	 */
	@FunctionalInterface
	public static interface Cleanup {
		
		/**
		 * The cleanup operation.
		 * 
		 * @throws Throwable
		 * The cleanup operation may throw
		 * any kind of exception.
		 */
		void cleanup() throws Throwable;
		
	}
	
}
