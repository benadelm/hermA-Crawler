/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;

import java.io.IOException;
import java.nio.file.Path;

import herma.crawler.textextraction.TextExtractionMethodInfo;

/**
 * Provides access to the results of linguistic preprocessing.
 * <p>
 * Direct access is provided to the sequence of <i>tokens</i>
 * (represented by {@link Token} instances) in the preprocessing
 * result, and there are methods to save the original input text,
 * the token sequence, and the overall result of the preprocessing
 * (which is expected to contain lemmatization) to files.
 * </p>
 * 
 * @see Token
 */
public interface PreParsingResult {
	
	/**
	 * Returns an {@link Iterable} of {@link Token} instances
	 * representing the tokens from the preprocessed text
	 * together with their lemmata (basic forms).
	 * <p>
	 * The {@link Iterable} returns the tokens in the same order
	 * in which they appear in the text.
	 * </p>
	 * 
	 * @return
	 * an {@link Iterable} of {@link Token} instances
	 * representing the tokens from the preprocessed text;
	 * neither the {@link Iterable} nor any of the elements
	 * in it are {@code null}
	 * 
	 * @see Token
	 */
	Iterable<? extends Token> getTokens();
	
	/**
	 * Returns a {@link TextExtractionMethodInfo} instance
	 * with meta-data about the text extraction method
	 * used to extract the linguistically preprocessed text
	 * from a web document.
	 * 
	 * @return
	 * a {@link TextExtractionMethodInfo} instance, not {@code null}
	 */
	TextExtractionMethodInfo getTextExtractionMethod();
	
	/**
	 * Saves the text extracted from the web document to disk.
	 * <p>
	 * This method writes to the specified file
	 * the text originally extracted from the web document.
	 * </p>
	 * <p>
	 * The file may already exist
	 * and will in that case be overwritten
	 * (or replaced) by this method.
	 * </p>
	 * 
	 * @param file
	 * a {@link Path} locating the file
	 * to save the text to
	 * 
	 * @throws IOException
	 * if an I/O error occurs
	 */
	void saveOriginalAs(Path filename) throws IOException;
	
	/**
	 * Saves the token sequence of the preprocessed text to disk.
	 * <p>
	 * This method writes to the specified file
	 * the sequence of token forms (word forms and punctuation)
	 * that tokenization has extracted from the text.
	 * </p>
	 * <p>
	 * The file may already exist
	 * and will in that case be overwritten
	 * (or replaced) by this method.
	 * </p>
	 * 
	 * @param file
	 * a {@link Path} locating the file
	 * to save the token sequence to
	 * 
	 * @throws IOException
	 * if an I/O error occurs
	 */
	void saveTokenizationAs(Path filename) throws IOException;
	
	/**
	 * Saves the full result of the linguistic preprocessing
	 * to disk.
	 * <p>
	 * This method writes to the specified file
	 * the full result of the linguistic preprocessing,
	 * which is expected to contain a lemma (basic form)
	 * for every token.
	 * </p>
	 * <p>
	 * The file may already exist
	 * and will in that case be overwritten
	 * (or replaced) by this method.
	 * </p>
	 * 
	 * @param file
	 * a {@link Path} locating the file
	 * to save the preprocessing result to
	 * 
	 * @throws IOException
	 * if an I/O error occurs
	 */
	void saveLemmatizationAs(Path filename) throws IOException;
	
	/**
	 * Performs cleanup work such as removing temporary files.
	 * <p>
	 * This method should be called
	 * after using this instance for the last time.
	 * </p>
	 * <p>
	 * Using any method of this instance after calling this method
	 * is invalid and might yield unexpected results.
	 * </p>
	 */
	void dispose();
	
}
