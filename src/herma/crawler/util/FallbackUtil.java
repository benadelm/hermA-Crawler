/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.util;

import java.util.function.BiFunction;

/**
 * Contains helper methods for fallback patterns.
 */
public class FallbackUtil {
	
	/**
	 * Performs an operation on a sequence of objects
	 * and returns the first non-{@code null} result.
	 * <p>
	 * If the sequence is empty
	 * or the operation returns {@code null} for all items,
	 * {@code null} is returned.
	 * </p>
	 * 
	 * @param <T>
	 * the type of the sequence items
	 * (or a super-type thereof)
	 * 
	 * @param <I>
	 * the type of the additional parameter of the operation
	 * (or a sub-type thereof)
	 * 
	 * @param <O>
	 * the type of the output of the operation
	 * (or a super-type thereof)
	 * 
	 * @param fallbackChain
	 * the sequence of items; not {@code null}
	 * 
	 * @param operation
	 * the operation to be performed on the sequence items;
	 * not {@code null}
	 * 
	 * @param input
	 * an additional parameter passed to the operation;
	 * whether this parameter may be {@code null},
	 * depends on the operation
	 * 
	 * @return
	 * <ul>
	 * <li>
	 * if the sequence contains at least one item
	 * and the operation returns a non-{@code null} reference
	 * for at least one sequence item:
	 * the result of the operation for the first sequence item
	 * for which the operation returned a non-{@code null} result
	 * </li>
	 * <li>
	 * otherwise: {@code null}
	 * </li>
	 * </ul>
	 */
	public static <I, T, O> O fallback(final Iterable<? extends T> fallbackChain, final BiFunction<? super T, ? super I, ? extends O> operation, final I input) {
		for (final T t : fallbackChain) {
			final O result = operation.apply(t, input);
			if (result != null)
				return result;
		}
		return null;
	}
	
}
