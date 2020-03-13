/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.meta;

/**
 * A concurrently accessible set (collection without duplicates)
 * of {@link String} instances,
 * to be used for collections of unique meta information strings.
 *
 */
public interface StringSet {
	
	/**
	 * Adds a {@link String} to this set.
	 * Returns {@code true} if the string
	 * is new (was not already present
	 * in the set).
	 * Returns {@code false} if the string
	 * was already present in the set
	 * (that is, has already been added
	 * in a previous call).
	 * This corresponds to the possible return values of
	 * sets from the Java Collections Framework.
	 * 
	 * @param str
	 * the {@link String} to be added
	 * 
	 * @return
	 * {@code true} if the string is new;
	 * {@code false} if the string
	 * was already present in the set
	 */
	boolean add(String str);
	
}
