/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

public class FilenameEncoder {
	
	private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
	
	public static String encodeForFilename(final String str) {
		final int n = str.length();
		final StringBuilder result = new StringBuilder();
		int next;
		for (int i = 0; i < n; i = next) {
			final int codePoint = str.codePointAt(i);
			next = i + Character.charCount(codePoint);
			switch (codePoint) {
				case 0xC4: // Ä
					result.append("Ae");
					break;
				case 0xD6: // Ö
					result.append("Oe");
					break;
				case 0xDC: // Ü
					result.append("Ue");
					break;
				case 0xE4: // ä
					result.append("ae");
					break;
				case 0xF6: // ö
					result.append("oe");
					break;
				case 0xFC: // ü
					result.append("ue");
					break;
				case 0xDF: // ß
					result.append("ss");
					break;
				case 0x20: // Leerzeichen
					result.appendCodePoint('_');
					break;
				default:
					if (isUnproblematicCodePoint(codePoint)) {
						result.appendCodePoint(codePoint);
						break;
					}
					final String noDiacriticsString = DIACRITICS.matcher(Normalizer.normalize(str.subSequence(i, next), Form.NFD)).replaceAll("");
					final int ndsl = noDiacriticsString.length();
					if (ndsl == 0)
						break;
					final int noDiacriticsCodePoint = noDiacriticsString.codePointAt(0);
					if ((ndsl == Character.charCount(noDiacriticsCodePoint)) && isUnproblematicCodePoint(noDiacriticsCodePoint))
						result.appendCodePoint(noDiacriticsCodePoint);
					else
						result.appendCodePoint('#');
					break;
					
			}
		}
		
		return result.toString();
	}
	
	private static boolean isUnproblematicCodePoint(final int codePoint) {
		return
				((codePoint > 0x2F) && (codePoint < 0x3A)) ||
				((codePoint > 0x40) && (codePoint < 0x5B)) ||
				((codePoint > 0x60) && (codePoint < 0x7B)) ||
				(codePoint == 0x23) || (codePoint == 0x24) ||
				(codePoint == 0x26) || (codePoint == 0x27) ||
				(codePoint == 0x28) || (codePoint == 0x29) ||
				(codePoint == 0x2B) || (codePoint == 0x2C) ||
				(codePoint == 0x2D) || (codePoint == 0x2E) ||
				(codePoint == 0x3D) || (codePoint == 0x40) ||
				(codePoint == 0x58) || (codePoint == 0x5D) ||
				(codePoint == 0x5F);
	}
}
