/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;


public class TextExtractionMethodInfo {
	
	public static final TextExtractionMethodInfo HTML5MAIN = new TextExtractionMethodInfo("HTML5Main", "a");
	public static final TextExtractionMethodInfo CANOLA = new TextExtractionMethodInfo("Canola", "b");
	public static final TextExtractionMethodInfo NUM_WORDS_RULES = new TextExtractionMethodInfo("NumWordsRules", "c");
	public static final TextExtractionMethodInfo XPDF = new TextExtractionMethodInfo("XPDF pdftotext", "d");
	public static final TextExtractionMethodInfo TXT = new TextExtractionMethodInfo("plain text", "e");
	
	private final String pName;
	private final String pShortcut;
	
	public TextExtractionMethodInfo(final String name, final String shortcut) {
		pName = name;
		pShortcut = shortcut;
	}
	
	public String getName() {
		return pName;
	}
	
	public String getShortcut() {
		return pShortcut;
	}
	
}
