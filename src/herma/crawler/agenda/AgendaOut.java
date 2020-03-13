/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.agenda;

/**
 * A view on the crawling agenda
 * to be used for taking URLs out.
 */
public interface AgendaOut {
	
	/**
	 * Takes the next URL out of the agenda.
	 * <p>
	 * This method will block until a next URL is available
	 * or the agenda is empty and no-one will be adding new URLs.
	 * If the latter is the case, this method returns {@code null}.
	 * Otherwise a non-{@code null} {@link AgendaItem}
	 * for the next URL is returned.
	 * </p>
	 * <p>
	 * If this method has been called,
	 * {@link #elementProcessed()}
	 * <b>must</b> be called at some point in time
	 * (see there for a more detailed description).
	 * </p>
	 * 
	 * @return
	 * a non-{@code null} {@link AgendaItem} for the next URL;
	 * or {@code null} if the agenda is empty
	 * and no-one will be adding new URLS
	 * 
	 * @throws InterruptedException
	 * if the method is waiting for a next URL to become available
	 * and the current thread is interrupted
	 * 
	 * @see #elementProcessed()
	 */
	AgendaItem getNext() throws InterruptedException;
	
	/**
	 * Informs the agenda that an element taken out with
	 * {@link #getNext()}
	 * has been processed by the component that took it out
	 * of the agenda.
	 * <p>
	 * A component that takes some element out of the agenda
	 * may add new URLs to the agenda, or it may cause other
	 * components to do so.
	 * By calling this method, the component that took an
	 * element out of the agenda signals that it (itself)
	 * will not add any new URLs to the agenda until calling
	 * {@link #getNext()}
	 * for the next time. (It may have caused other components
	 * to do so in the future, but those components will
	 * already have registered themselves as ones that might
	 * add new URLs to the agenda.)
	 * </p>
	 * 
	 * @see #getNext()
	 */
	void elementProcessed();
	
}
