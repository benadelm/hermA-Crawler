/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.meta;

/**
 * Can take strings to be collected as meta information
 * about the web crawling process.
 * The collection may allow duplicate elements
 * (like a <i>list</i>) or not (like a <i>set</i>).
 */
public interface MetaInformationStringCollection {
	
	/**
	 * Adds a {@link String} to this collection.
	 * Returns {@code true} if the item
	 * was added to the collection.
	 * Returns {@code false} if the item
	 * was not added to the collection
	 * (for example because the collection maintains
	 * unique elements and the item is already present).
	 * This corresponds to the possible return values of
	 * sets from the Java Collections Framework.
	 * 
	 * @param str
	 * the {@link String} to be added
	 * 
	 * @return
	 * {@code true} if the string was added to the collection;
	 * {@code false} otherwise
	 */
	boolean add(String str);
	
}
