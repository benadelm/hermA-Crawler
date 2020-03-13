/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A view on a retrieved web document to be used by the
 * final stage of a document processing chain.
 */
public interface OriginalInfo {
	
	/**
	 * Returns meta-data about the document.
	 * 
	 * @return
	 * a {@link Metadata} instance storing
	 * meta-data about the document; not {@code null}
	 */
	Metadata getMetadata();
	
	/**
	 * Returns the file name extension to be used
	 * when saving the original web document to disk.
	 * <p>
	 * If this method returns an extension,
	 * the leading dot is included.
	 * For example, this method would return {@code ".txt"}
	 * instead of just {@code "txt"}.
	 * An empty string ({@code ""}) may be returned to indicate
	 * that the web document shall be saved without extension.
	 * </p>
	 * 
	 * @return
	 * the file name extension to be used
	 * when saving the original web document to disk;
	 * not {@code null}
	 */
	String getFileNameExtension();
	
	/**
	 * Saves the original web document to disk.
	 * <p>
	 * This method writes to the specified file
	 * the exact byte sequence of the content of the HTTP response
	 * by which this web document has been retrieved.
	 * </p>
	 * <p>
	 * The file may already exist
	 * and will in that case be overwritten
	 * (or replaced) by this method.
	 * </p>
	 * 
	 * @param file
	 * a {@link Path} locating the file
	 * to save the document to
	 * 
	 * @throws IOException
	 * if an I/O error occurs
	 */
	void saveTo(Path file) throws IOException;
}
