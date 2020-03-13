/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

/**
 * Can start further processing after a web document
 * has been classified as relevant,
 * including measures to store meta-data about the document
 * as well as the document itself.
 */
public interface FurtherProcessing {
	
	/**
	 * Starts further processing of a web document
	 * after it has been classified as relevant.
	 * <p>
	 * This method assumes that if it is called,
	 * the document has been classified as relevant.
	 * This may not be double-checked,
	 * and this method will not fail
	 * (for example, by throwing some exception)
	 * if a document is passed to it
	 * that has not been classified as relevant.
	 * </p>
	 * 
	 * @param originalInfo
	 * meta-data for the web document; not {@code null}
	 * 
	 * @param items
	 * items to be further processed for this web document;
	 * neither this {@link Iterable} nor any of its elements
	 * are {@code null}
	 * 
	 * @see OriginalInfo
	 * @see FurtherProcessingItem
	 */
	void startFurtherProcessing(OriginalInfo originalInfo, Iterable<? extends FurtherProcessingItem> items);
	
}
