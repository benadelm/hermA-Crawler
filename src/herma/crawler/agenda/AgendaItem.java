/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.agenda;


public class AgendaItem {
	
	private final String pUrl;
	private final long pDepth;
	
	public AgendaItem(final String url, final long depth) {
		pUrl = url;
		pDepth = depth;
	}
	
	public String getUrl() {
		return pUrl;
	}
	
	public long getDepth() {
		return pDepth;
	}
	
}
