/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Pattern;

import herma.crawler.textextraction.ExtractedText;
import herma.crawler.textextraction.TextExtractionMethodInfo;


public class DummyPreParsingPipeline implements PreParsingPipeline {
	
	private static final Pattern SPACES = Pattern.compile("\\s+");
	
	@Override
	public PreParsingResult apply(final ExtractedText extractedText) {
		try {
			final ArrayList<Token> result = new ArrayList<>();
			for (final String text : extractedText.getTexts())
				for (final String word : SPACES.split(text))
					result.add(new Token(word, word));
			return new DummyPreParsingResult(result, extractedText.getTextExtractionMethod());
		} finally {
			extractedText.removeTempFiles();
		}
	}
	
	private static class DummyPreParsingResult implements PreParsingResult {
		
		private final ArrayList<Token> pTokens;
		private final TextExtractionMethodInfo pTextExtractionMethod;
		
		public DummyPreParsingResult(final ArrayList<Token> tokens, final TextExtractionMethodInfo textExtractionMethod) {
			pTokens = tokens;
			pTextExtractionMethod = textExtractionMethod;
		}
		
		@Override
		public Iterable<? extends Token> getTokens() {
			return pTokens;
		}

		@Override
		public TextExtractionMethodInfo getTextExtractionMethod() {
			return pTextExtractionMethod;
		}

		@Override
		public void saveOriginalAs(final Path filename) throws IOException {
			// leer
		}

		@Override
		public void saveTokenizationAs(final Path filename) throws IOException {
			// leer
		}

		@Override
		public void saveLemmatizationAs(final Path filename) throws IOException {
			// leer
		}

		@Override
		public void dispose() {
			// leer
		}
	}
	
}
