/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import herma.crawler.config.Configuration;
import herma.crawler.relevance.AcceptEverythingRelevanceDecider;
import herma.crawler.relevance.linguistic.AfterPreprocessingRelevanceDecider;
import herma.crawler.relevance.linguistic.KeyphrasesRelevanceDecider;

public class KeyphrasesRelevanceDeciderSetup {
	
	private static final String KEYPHRASES_KEY = "keyphrases";
	private static final String MATCH_EXACT_KEY = "keyphrases.exact";
	private static final boolean DEFAULT_MATCH_EXACT = false;
	
	public static AfterPreprocessingRelevanceDecider setupKeyphrasesRelevanceDecider(final Configuration config, final Path pathBase) {
		final Path keyphrasesFile = config.getPath(pathBase, KEYPHRASES_KEY);
		if (keyphrasesFile == null) {
			config.addConfigurationWarning("No keyphrases file (key \"" + KEYPHRASES_KEY + "\") has been specified. Any web document will be considered relevant.");
			return AcceptEverythingRelevanceDecider.INSTANCE;
		}
		
		final String exactString = config.getString(MATCH_EXACT_KEY);
		final boolean exact;
		if (exactString == null) {
			exact = DEFAULT_MATCH_EXACT;
		} else if ("true".equals(exactString)) {
			exact = true;
		} else if ("false".equals(exactString)) {
			exact = false;
		} else {
			config.addConfigurationError(MATCH_EXACT_KEY + " must be either \"true\" or \"false\" (not \"" + exactString + "\")");
			return null;
		}
		
		try (final Stream<String> lines = Files.lines(keyphrasesFile, StandardCharsets.UTF_8)) {
			return new KeyphrasesRelevanceDecider(lines::iterator, exact);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
}
