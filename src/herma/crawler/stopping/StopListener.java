/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.stopping;

/**
 * Can listen for a stop signal to the web crawler.
 * <p>
 * When a stop signal is detected,
 * an implementation will typically perform some action
 * to make the crawler terminate.
 * </p>
 *
 */
public interface StopListener {
	
	/**
	 * Starts listening for a stop signal.
	 * <p>
	 * If the stop listener has already been stopped
	 * (by a call to {@link #stopListening()})
	 * or received a stop signal and performed the action
	 * to make the crawler terminate,
	 * it may not be possible to start listening again.
	 * </p>
	 */
	void startListening();
	
	/**
	 * Stops listening for a stop signal.
	 * <p>
	 * If a stop signal has already been detected,
	 * the action to make the web crawler terminate
	 * is (or has already been) performed
	 * and this method has no effect.
	 * If no stop signal has been detected yet,
	 * the stop listener stops listening
	 * and does not perform the action
	 * to make the web crawler terminate.
	 * </p>
	 * <p>
	 * This means that the return from a call to this method
	 * will never <i>happen before</i> (the beginning of) the
	 * action to make the web crawler terminate.
	 * (However, the action might be initiated concurrently
	 * to this call, or might be already running
	 * when this method is called.)
	 * </p>
	 * <p>
	 * After a call to this method returns,
	 * it may not be possible to start listening again
	 * (by calling {@link #startListening()}).
	 * </p>
	 */
	void stopListening();
	
}
