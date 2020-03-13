/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.links;

/**
 * Contains helper methods for efficiently dealing with
 * hexadecimal numbers appearing in URL (percent) encoding.
 */
class HexUtil {
	
	/**
	 * Returns the numeric value of a hexadecimal digit
	 * or {@code -1} if the argument is not a hexadecimal digit.
	 * <p>
	 * For example:
	 * </p>
	 * <ul>
	 * <li>
	 * If the argument is {@code '3'},
	 * the return value is {@code 3}.
	 * </li>
	 * <li>
	 * If the argument is {@code 'A'} or {@code 'a'},
	 * the return value is {@code 10}.
	 * </li>
	 * <li>
	 * If the argument is {@code 12345},
	 * the return value is {@code -1}.
	 * </li>
	 * </ul>
	 * 
	 * @param codePoint
	 * the code point that hopefully is a hexadecimal digit
	 * 
	 * @return
	 * if the given code point is a hexadecimal digit,
	 * the numerical value of that digit;
	 * {@code -1} otherwise
	 */
	public static int unhexDigit(final int codePoint) {
		int v = Character.toLowerCase(codePoint) - '0';
		if (v < 0)
			return -1;
		if (v < 10)
			return v;
		v -= 49;
		if (v < 0)
			return -1;
		if (v < 6)
			return v + 10;
		return -1;
	}
	
	/**
	 * Returns the hexadecimal digit
	 * corresponding to the given number.
	 * <p>
	 * The number must be at least zero
	 * and at most {@code 15}
	 * (so that it can be represented
	 * by a single hexadecimal digit).
	 * <b>This is not checked.</b>
	 * If the value range of the argument is violated,
	 * the result is meaningless.
	 * </p>
	 * <p>
	 * For example:
	 * </p>
	 * <ul>
	 * <li>
	 * If the argument is {@code 6},
	 * the return value is {@code '6'}.
	 * </li>
	 * <li>
	 * If the argument is {@code 10},
	 * the return value is {@code 'A'}.
	 * </li>
	 * <li>
	 * If the argument is {@code 12345},
	 * the return value is a meaningless number.
	 * </li>
	 * </ul>
	 * 
	 * @param hex
	 * the number
	 * (greater than or equal to zero,
	 * strictly less than {@code 16})
	 * to be represented by a hexadecimal digit
	 * 
	 * @return
	 * if the given value is within the range of numbers
	 * that can be represented by a single hexadecimal digit:
	 * that digit; otherwise: meaningless
	 */
	public static int hexDigit(final int hex) {
		if (hex < 10)
			return '0' + hex;
		return '7' + hex;
	}
	
}
