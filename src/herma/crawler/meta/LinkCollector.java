/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.meta;

import herma.crawler.LinkType;

public class LinkCollector {
	
	private final MetaInformationStringCollection pLinks;
	
	public LinkCollector(final MetaInformationStringCollection linksCollection) {
		pLinks = linksCollection;
	}
	
	public void addLink(final String fromUrl, final String toUrl, final LinkType type, final long fromDepth) {
		pLinks.add(fromUrl + '\t' + toUrl + '\t' + linkTypeId(type) + '\t' + Long.toString(fromDepth));
	}
	
	private static String linkTypeId(final LinkType linkType) {
		switch (linkType) {
			case LINK:
				return "0";
			case REDIRECT:
				return "1";
			default:
				throw new IllegalArgumentException();
		}
	}
	
}
