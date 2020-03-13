/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import java.io.StringReader;

import org.jsoup.nodes.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeFilter;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CanolaExtractor;
//import de.l3s.boilerpipe.extractors.NumWordsRulesExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;

public class BoilerpipeTextExtractor implements HtmlTextExtractor {
	
	@Override
	public ExtractedText[] extractText(final Document htmlDocument) {
		// currently only uses the Canola boilerpipe extractor
		// commented-out code for using the NumWordsRules extractor, too
		final String html = htmlDocument.outerHtml();
		final ExtractedText canolaResult = tryBoilerpipe(html, TextExtractionMethodInfo.CANOLA, CanolaExtractor.INSTANCE);
		//final ExtractedText numWordsRulesResult = tryBoilerpipe(html, TextExtractionMethodInfo.NUM_WORDS_RULES, NumWordsRulesExtractor.INSTANCE);
		final boolean hasCanola = (canolaResult != null);
		//final boolean hasNumWordsRules = (numWordsRulesResult != null);
		/*if (hasCanola && hasNumWordsRules)
			return new ExtractedText[] { canolaResult, numWordsRulesResult };*/
		if (hasCanola)
			return new ExtractedText[] { canolaResult };
		/*if (hasNumWordsRules)
			return new ExtractedText[] { numWordsRulesResult };*/
		return null;
	}
	
	private ExtractedText tryBoilerpipe(final String html, final TextExtractionMethodInfo method, final BoilerpipeFilter boilerpipeFilter) {
		try {
			return boilerpipe(html, method, boilerpipeFilter);
		} catch (final BoilerpipeProcessingException | SAXException e) {
			// TODO: somehow report errors?
			return null;
		}
	}

	private static ExtractedText boilerpipe(final String html, final TextExtractionMethodInfo method, final BoilerpipeFilter boilerpipeFilter) throws BoilerpipeProcessingException, SAXException {
		final InputSource inputSource = new InputSource(new StringReader(html));
		final TextDocument document = (new BoilerpipeSAXInput(inputSource)).getTextDocument();
		boilerpipeFilter.process(document);
		return new InMemoryExtractedText(method, new String[] { document.getContent() });
	}
	
}
