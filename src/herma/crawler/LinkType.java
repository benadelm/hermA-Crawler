/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

/**
 * Types of references from a web document to other documents.
 */
public enum LinkType {
	
	/**
	 * A reference from within (the head or body of)
	 * a web document, such as the {@code href} attributes
	 * of {@code <a>} or {@code <link>} elements in HTML.
	 */
	LINK,
	
	/**
	 * A redirect, usually indicated by a {@code location} header
	 * in an HTTP response.
	 */
	REDIRECT
}
