/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance.linguistic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import herma.crawler.relevance.RelevanceDecision;


class KeyphrasesRelevanceDecision implements RelevanceDecision {
	
	private final ArrayList<String[]> pOutput;
	
	public KeyphrasesRelevanceDecision(final ArrayList<String> foundKeyphrases) {
		if (foundKeyphrases == null) {
			pOutput = null;
		} else {
			final HashMap<String, BigInteger> counts = new HashMap<>();
			for (final String foundKeyphrase : foundKeyphrases)
				counts.put(foundKeyphrase, BigInteger.ONE.add(counts.getOrDefault(foundKeyphrase, BigInteger.ZERO)));
			pOutput = new ArrayList<>();
			for (final Entry<String, BigInteger> entry : counts.entrySet())
				pOutput.add(new String[] { entry.getKey(), entry.getValue().toString() });
		}
	}
	
	@Override
	public boolean isRelevant() {
		return pOutput != null;
	}
	
	@Override
	public Iterable<String[]> getRelevanceOutputs() {
		return pOutput;
	}
	
}
