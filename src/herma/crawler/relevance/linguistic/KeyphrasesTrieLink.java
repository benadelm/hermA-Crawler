/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance.linguistic;


class KeyphrasesTrieLink {
	
	private final KeyphrasesTrie pSubtrie;
	private final int pAcceptLength;
	
	public static KeyphrasesTrieLink to(final KeyphrasesTrie subtrie) {
		return new KeyphrasesTrieLink(subtrie, 0);
	}
	
	public static KeyphrasesTrieLink accept(final int acceptLength) {
		return new KeyphrasesTrieLink(null, acceptLength);
	}
	
	private KeyphrasesTrieLink(final KeyphrasesTrie subtrie, final int acceptLength) {
		pSubtrie = subtrie;
		pAcceptLength = acceptLength;
	}
	
	public KeyphrasesTrie getSubtrie() {
		return pSubtrie;
	}
	
	public int getAcceptLength() {
		return pAcceptLength;
	}
	
}
