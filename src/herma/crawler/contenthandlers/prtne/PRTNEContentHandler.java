/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.prtne;

import java.time.Instant;

import org.apache.http.HttpResponse;

import herma.crawler.ContentHandler;
import herma.crawler.ProcessingThreadCreator;
import herma.crawler.meta.MetaInformationStringCollection;


public class PRTNEContentHandler implements ContentHandler {
	
	private final MetaInformationStringCollection pPRTNEUrls; // TODO Potenziell relevant, Text bisher nicht extrahierbar; vorläufig
	
	public PRTNEContentHandler(final MetaInformationStringCollection prtneUrlsCollection) {
		pPRTNEUrls = prtneUrlsCollection;
	}
	
	@Override
	public ProcessingThreadCreator handle(final String url, final long depth, final Instant requestTime, final HttpResponse httpResponse) {
		pPRTNEUrls.add(url + '\t' + httpResponse.getEntity().getContentType().getValue());
		return null;
	}
	
}
