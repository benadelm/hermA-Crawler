/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;


public class Token {
	
	private final String pForm;
	private final String pLemma;
	
	Token(final String form, final String lemma) {
		pForm = form;
		pLemma = lemma;
	}
	
	public String getForm() {
		return pForm;
	}
	
	public String getLemma() {
		return pLemma;
	}
	
}
