/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.nio.file.Path;
import java.text.ParseException;

import herma.crawler.SaveAndParseFurtherProcessing;
import herma.crawler.config.Configuration;
import herma.crawler.linguisticprocessing.ParserCaller;

public class SaveAndParseFurtherProcessingSetup {
	
	private static final int DEFAULT_ID_DIGITS = 6;
	
	public static SaveAndParseFurtherProcessing setupSaveAndParseFurtherProcessing(final Configuration config, final Path outputDirectory, final ParserCaller parserCaller, final String crawlPrefix) {
		if (parserCaller == null)
			return null;
		
		final int idDigits;
		try {
			idDigits = config.getInt("idDigits", DEFAULT_ID_DIGITS);
		} catch (final ParseException e) {
			return null;
		}
		
		return new SaveAndParseFurtherProcessing(outputDirectory, parserCaller, crawlPrefix, idDigits);
	}
	
}
