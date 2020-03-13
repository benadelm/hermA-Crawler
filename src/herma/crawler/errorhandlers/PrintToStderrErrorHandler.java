/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.errorhandlers;

import herma.crawler.ErrorHandler;
import herma.crawler.LinkType;


public class PrintToStderrErrorHandler implements ErrorHandler {
	
	@Override
	public synchronized void handleError(final String url, final String message) {
		System.err.println(url);
		System.err.print("--> ");
		System.err.println(message);
	}
	
	@Override
	public synchronized void handleError(final String url, final Exception exception) {
		System.err.println(url);
		System.err.print("--> ");
		System.err.print(exception.getClass().getTypeName());
		System.err.print(": ");
		System.err.println(exception.getLocalizedMessage());
	}
	
	@Override
	public synchronized void handleError(final String referringUrl, final String referredUrl, final LinkType linkType, final Exception exception) {
		System.err.println(referredUrl);
		System.err.print('(');
		switch (linkType) {
			case LINK:
				System.err.print("verlinkt");
				break;
			case REDIRECT:
				System.err.print("weitergeleitet");
				break;
			default:
				throw new IllegalArgumentException();
		}
		System.err.print(" von ");
		System.err.print(referringUrl);
		System.err.println(')');
		System.err.print("--> ");
		System.err.print(exception.getClass().getTypeName());
		System.err.print(": ");
		System.err.println(exception.getLocalizedMessage());
	}
	
}
