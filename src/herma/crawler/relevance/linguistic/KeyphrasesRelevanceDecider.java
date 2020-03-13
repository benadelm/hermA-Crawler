/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance.linguistic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

import herma.crawler.linguisticprocessing.PreParsingResult;
import herma.crawler.linguisticprocessing.Token;
import herma.crawler.relevance.RelevanceDecision;


// TODO: war WortfeldRelevanceDecider
public class KeyphrasesRelevanceDecider implements AfterPreprocessingRelevanceDecider {
	
	private static final Pattern SPACES_PATTERN = Pattern.compile("\\s+");
	
	private final KeyphrasesTrie pTrie;
	private final boolean pExact;
	
	public KeyphrasesRelevanceDecider(final Iterable<String> wortfeld, final boolean exact) {
		pTrie = new KeyphrasesTrie();
		for (final String wort : wortfeld)
			pTrie.add(SPACES_PATTERN.split(wort.toLowerCase(Locale.ROOT)));
		pExact = exact;
	}
	
	@Override
	public RelevanceDecision isRelevant(final PreParsingResult preParsingResult) {
		final ArrayList<String> formStack = new ArrayList<>();
		final ArrayList<String> matches = new ArrayList<>();
		HashSet<KeyphrasesTrie> currentState = new HashSet<>();
		HashSet<KeyphrasesTrie> nextState = new HashSet<>();
		int n = 0;
		for (final Token token : preParsingResult.getTokens()) {
			final String lemmaLowercased = token.getLemma().toLowerCase(Locale.ROOT);
			formStack.add(token.getForm());
			n = Math.incrementExact(n);
			currentState.add(pTrie);
			for (final KeyphrasesTrie state : currentState) {
				for (final KeyphrasesTrieLink next : state.next(lemmaLowercased, pExact)) {
					final int acceptLength = next.getAcceptLength();
					if (acceptLength > 0) {
						matches.add(String.join(" ", formStack.subList(n - acceptLength, n)));
						continue;
					}
					nextState.add(next.getSubtrie());
				}
			}
			final HashSet<KeyphrasesTrie> temp = currentState;
			currentState = nextState;
			nextState = temp;
			nextState.clear();
		}
		return new KeyphrasesRelevanceDecision((matches.size() == 0) ? null : matches);
	}
	
}
