/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;

import java.nio.file.Path;

/**
 * Can call a parser.
 */
public interface ParserCaller {
	
	/**
	 * Starts running a parser.
	 * <p>
	 * This method starts the parser,
	 * but does not wait for it to finish.
	 * </p>
	 * <p>
	 * The parser output will be written
	 * to the specified output file.
	 * </p>
	 * <p>
	 * Input to the parser is a file with the result of
	 * linguistic preprocessing. There might be sentences
	 * in the input which are too long for the parser to
	 * parse within a reasonable amount of time.
	 * Those sentences may be removed from the input by the
	 * implementation.
	 * In that case, the modified input (without the
	 * sentences that are too long) is written to the specified
	 * <i>alternative input file</i> and the method returns
	 * {@code true}. Otherwise, the method returns {@code false}
	 * to indicate that the <i>alternative input file</i> has
	 * not been used and can be discarded.
	 * </p>
	 * <p>
	 * There is no output to indicate whether starting the parser
	 * has succeeded.
	 * </p>
	 * 
	 * @param morphFile
	 * (a {@link Path} locating) the file
	 * with the input to the parser; not {@code null}
	 * 
	 * @param alternativeInputFile
	 * (a {@link Path} locating) the <i>alternative input file</i>;
	 * not {@code null}
	 * 
	 * @param outputFile
	 * (a {@link Path} locating) the output file;
	 * not {@code null}
	 * 
	 * @return
	 * {@code true} if the <i>alternative input file</i> is
	 * being used (and should thus be retained);
	 * {@code false} otherwise
	 */
	boolean callParser(Path morphFile, Path alternativeInputFile, Path outputFile);
	
}
