/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

/**
 * Can handle (reports of) errors that arise during operation.
 */
public interface ErrorHandler {
	
	/**
	 * Handles (the report of) non-technical errors
	 * during processing of some web document,
	 * such as missing or unexpected content.
	 * 
	 * @param url
	 * the URL of the web document during processing of which
	 * the error has occurred; not {@code null}
	 * 
	 * @param message
	 * some description of the error, not {@code null}
	 */
	void handleError(String url, String message);
	
	/**
	 * Handles (the report of) technical errors (Java Exceptions)
	 * during processing of some web document,
	 * such as aborted connections.
	 * 
	 * @param url
	 * the URL of the web document during processing of which
	 * the error has occurred; not {@code null}
	 * 
	 * @param exception
	 * the {@link Exception} that has occurred; not {@code null}
	 */
	void handleError(String url, Exception exception);
	
	/**
	 * Handles (the report of) technical errors (Java Exceptions)
	 * in connection with links between web documents,
	 * such as erroneous or malformed URLs.
	 * 
	 * @param referringUrl
	 * the URL of the referring web document; not {@code null}
	 * 
	 * @param referredUrl
	 * the (possibly erroneous or malformed) URL
	 * which is being referred to; not {@code null}
	 * 
	 * @param linkType
	 * the type of referral; not {@code null}
	 * 
	 * @param exception
	 * the {@link Exception} that has occurred; not {@code null}
	 */
	void handleError(String referringUrl, String referredUrl, LinkType linkType, Exception exception);
	
}
