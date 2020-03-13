/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.time.Instant;

import org.apache.http.HttpResponse;

/**
 * Can handle responses of certain content types
 * received from HTTP requests.
 */
public interface ContentHandler {
	
	/**
	 * Deals with an {@link HttpResponse}
	 * received from an HTTP request.
	 * <p>
	 * It is not expected that the {@link HttpResponse} remains
	 * valid after a call to this method returns, so this method
	 * will not create any reference to it that persists
	 * afterwards. Any cleanup to release resources used by the
	 * {@link HttpResponse} may be safely (at least as far as this
	 * method is concerned) performed after this method returns.
	 * </p>
	 * <p>
	 * This method does not perform any (time-consuming) processing
	 * of the response’s content; if such processing is necessary,
	 * it extracts the required data from the response and creates
	 * a {@link ProcessingThreadCreator} instance.
	 * If no processing is necessary, or if processing will not
	 * be possible for the given HTTP response,
	 * this method returns {@code null}.
	 * </p>
	 * 
	 * @param url
	 * the URL for which the {@link HttpResponse} has been retrieved;
	 * not {@code null}
	 * 
	 * @param depth
	 * the <i>next</i> depth in the search tree; for example,
	 * for seed URLs (depth zero) this will be {@code 1} (one)
	 * 
	 * @param requestTime
	 * an {@link Instant} representing the point in time
	 * at which the response was received; not {@code null}
	 * 
	 * @param httpResponse
	 * the {@link HttpResponse}; not {@code null}
	 * 
	 * @return
	 * a {@link ProcessingThreadCreator} instance
	 * for further processing of the response’ content;
	 * or {@code null}, if no such processing shall happen
	 * 
	 * @see ProcessingThreadCreator
	 */
	ProcessingThreadCreator handle(String url, long depth, Instant requestTime, HttpResponse httpResponse);
	
}
