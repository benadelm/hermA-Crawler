/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.relevance;

/**
 * A view on the decision about the relevance of a web document.
 */
public interface RelevanceDecision {
	
	/**
	 * Returns whether the web document is relevant.
	 * 
	 * @return
	 * {@code true} if the web document is relevant,
	 * {@code false} otherwise.
	 */
	boolean isRelevant();
	
	/**
	 * Returns additional information specific to the
	 * way this relevance decision has been reached.
	 * <p>
	 * At the moment, this additional information is
	 * hardly structured; it merely consists of
	 * (an arbitrary number of) {@link String} arrays.
	 * </p>
	 * <p>
	 * The strings do not contain any of the following
	 * code points:
	 * </p>
	 * <ul>
	 * <li>U+0009 CHARACTER TABULATION ({@code '\t'})</li>
	 * <li>U+000A LINE FEED ({@code '\n'})</li>
	 * <li>U+000D CARRIAGE RETURN ({@code '\r'})</li>
	 * </ul>
	 * 
	 * @return
	 * an {@link Iterable} of {@link String} arrays;
	 * neither the {@link Iterable} nor any array in it
	 * nor any element of any of the arrays is {@code null}
	 */
	Iterable<String[]> getRelevanceOutputs();
	
}
