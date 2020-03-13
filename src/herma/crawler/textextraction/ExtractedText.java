/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.textextraction;

import java.nio.file.Path;

/**
 * Provides access to the text extracted from a document
 * as well as some meta-data concerning the extraction.
 * <p>
 * The document text may be split into text passages.
 * Splitting the document text into passages happens
 * at a text extractor’s discretion. It is possible that
 * an extractor always returns only one text passage
 * (which contains the whole document text). 
 * </p>
 * <p>
 * Splitting the document text into text passages is a <i>hint</i>
 * that these passages should be processed separately and
 * independently of each other (for instance, in a linguistic
 * processing pipeline) in spite of belonging to the same document.
 * The order of the text passages matters and should be preserved
 * by further processing steps.
 * </p>
 * <p>
 * Through this interface, the extracted text passages can be accessed
 * as {@link String} instances ({@link #getTexts()})
 * or as (temporary) files ({@link #getTempFilesPaths()}).
 * Hence, it has to be ensured that these files are deleted
 * after their usage (to avoid resource leaks).
 * {@link #removeTempFiles()} ensures that none of the
 * temporary files which {@link #getTempFilesPaths()} would return
 * remains on disk, so {@link #removeTempFiles()} should be called
 * when the files are not needed anymore.
 * (If the files are needed beyond the potential lifetime
 * of the respective {@link ExtractedText} instance,
 * their disposal has to be ensured by other means.)
 * </p>
 * <p>
 * If no temporary files are needed at all
 * (and the result of {@link #getTempFilesPaths()}
 * is never needed), it is advisable to use
 * {@link #removeTempFiles()} instead of calling
 * {@link #getTempFilesPaths()} just to delete the
 * returned files, as the implementation may
 * not have to create them in the first place.
 * </p>
 * <p>
 * Any <i>other</i> cleanup the implementation has to perform
 * happens in {@link #cleanup()}, so that method should
 * <i>always</i> be called when an instance of
 * {@link ExtractedText} is not needed anymore.
 * </p>
 */
public interface ExtractedText {
	
	/**
	 * Returns the extracted text passages
	 * as {@link String} instances.
	 * 
	 * @return
	 * the extracted text passages as {@link String} instances;
	 * neither this array nor any of its elements are {@code null}
	 */
	String[] getTexts();
	
	/**
	 * Returns an array of {@link Path} objects
	 * locating temporary files
	 * containing the extracted text passages.
	 * <p>
	 * It has to be ensured that these files are deleted
	 * after their usage (to avoid resource leaks).
	 * {@link #removeTempFiles()} ensures that none of the
	 * temporary files which this method returns
	 * remains on disk. When the files are not needed anymore,
	 * {@link #removeTempFiles()} should be called,
	 * or the files should be deleted by other means.
	 * </p>
	 * 
	 * @return
	 * an array of {@link Path} objects of temporary files
	 * with the extracted text passages;
	 * neither this array nor any of its elements are {@code null}
	 */
	Path[] getTempFilesPaths();
	
	/**
	 * Returns a {@link TextExtractionMethodInfo} instance
	 * with meta-data about the text extraction method.
	 * 
	 * @return
	 * a {@link TextExtractionMethodInfo} instance, not {@code null}
	 */
	TextExtractionMethodInfo getTextExtractionMethod();
	
	/**
	 * Ensures that none of the temporary files
	 * which {@link #getTempFilesPaths()} would return
	 * exists on disk (any more).
	 */
	void removeTempFiles();
	
	/**
	 * Releases resources used by this instance,
	 * <b>except</b> temporary files which
	 * {@link #getTempFilesPaths()} would return.
	 * Use {@link #removeTempFiles()} to remove them.
	 */
	void cleanup();
	
}
