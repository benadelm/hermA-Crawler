/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import herma.crawler.config.Configuration;
import herma.crawler.contenthandlers.html.links.ForeignHostFilterLinkFactory;

public class ForeignHostFilterLinkFactorySetup {
	
	private static final String SKIP_FOREIGN_LINKS_KEY = "foreignHostFilter.skipForeignLinks";
	
	// TODO: Kommentare weg
	// true = komplett ignorieren
	// false = initiale Heuristik (nur bei Treffern verfolgen)
	private static final boolean DEFAULT_SKIP_FOREIGN_LINKS = false;
	
	public static ForeignHostFilterLinkFactory setupForeignHostFilterLinkFactory(final Configuration config) {
		final String skipForeignLinksString = config.getString(SKIP_FOREIGN_LINKS_KEY);
		final boolean skipForeignLinks;
		if (skipForeignLinksString == null) {
			skipForeignLinks = DEFAULT_SKIP_FOREIGN_LINKS;
		} else if ("true".equals(skipForeignLinksString)) {
			skipForeignLinks = true;
		} else if ("false".equals(skipForeignLinksString)) {
			skipForeignLinks = false;
		} else {
			config.addConfigurationError(SKIP_FOREIGN_LINKS_KEY + " must be either \"true\" or \"false\" (not \"" + skipForeignLinksString + "\")");
			return null;
		}
		
		return new ForeignHostFilterLinkFactory(skipForeignLinks);
	}
	
}
