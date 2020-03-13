/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance.linguistic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

class KeyphrasesTrie {
	
	private final HashMap<String, KeyphrasesTrieLink> pTable = new HashMap<>();
	
	public Iterable<KeyphrasesTrieLink> next(final String word, final boolean exact) {
		final HashSet<KeyphrasesTrieLink> result = new HashSet<>();
		if (exact) {
			final KeyphrasesTrieLink entry = pTable.getOrDefault(word, null);
			if (entry != null)
				result.add(entry);
		} else {
			for (final Entry<String, KeyphrasesTrieLink> entry : pTable.entrySet())
				if (word.contains(entry.getKey()))
					result.add(entry.getValue());
		}
		return result;
	}
	
	public void add(final String[] expression) {
		KeyphrasesTrie current = this;
		final int n = expression.length - 1;
		for (int i = 0; i < n; i++) {
			final String word = expression[i];
			final KeyphrasesTrieLink tableItem = current.pTable.getOrDefault(word, null);
			if (tableItem == null) {
				final KeyphrasesTrie neu = new KeyphrasesTrie();
				current.pTable.put(word, KeyphrasesTrieLink.to(neu));
				current = neu;
				continue;
			}
			if (tableItem.getAcceptLength() > 0)
				break;
			current = tableItem.getSubtrie();
		}
		final String word = expression[n];
		final KeyphrasesTrieLink tableItem = current.pTable.getOrDefault(word, null);
		if ((tableItem == null) || (tableItem.getAcceptLength() == 0))
			current.pTable.put(word, KeyphrasesTrieLink.accept(expression.length));
	}
	
}
