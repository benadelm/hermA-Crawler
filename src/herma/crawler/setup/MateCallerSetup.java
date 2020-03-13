/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.nio.file.Path;
import java.text.ParseException;

import herma.crawler.config.Configuration;
import herma.crawler.linguisticprocessing.MateCaller;

public class MateCallerSetup {
	
	private static final String MATE_JAR_KEY = "mate.parser.jar";
	private static final String MATE_MODEL_KEY = "mate.parser.model";
	
	private static final String MAX_SENTENCE_LENGTH_KEY = "maxSentenceLength";
	private static final int DEFAULT_MAX_SENTENCE_LENGTH = 430; // arbitrary; compromise between parser performance and empirical sentence length distribution in data from trial crawls
	
	private static final String MAX_CONCURRENT_PARSER_INVOCATIONS_KEY = "maxConcurrentParserInvocations";
	private static final int DEFAULT_MAX_CONCURRENT_PARSER_INVOCATIONS = 5;
	
	private static final String DEFAULT_MATE_CLASS = "is2.parser.Parser";
	private static final String DEFAULT_JAVA_MEMORY = "10g";
	
	public static MateCaller setupMateCaller(final Configuration config, final Path toolsDir, final String javaCommand) {
		boolean error = false;
		
		final Path mateJar = config.getPath(toolsDir, MATE_JAR_KEY);
		if (mateJar == null) {
			config.addConfigurationError("You have to specify a path to the JAR file for the MATE parser (key \"" + MATE_JAR_KEY + "\").");
			error = true;
		}
		
		final Path mateModel = config.getPath(toolsDir, MATE_MODEL_KEY);
		if (mateModel == null) {
			config.addConfigurationError("You have to specify a path to the model file for the MATE parser (key \"" + MATE_MODEL_KEY + "\").");
			error = true;
		}
		
		final int maxConcurrentParserInvocations;
		final int maxSentenceLength;
		try {
			maxConcurrentParserInvocations = config.getInt(MAX_CONCURRENT_PARSER_INVOCATIONS_KEY, DEFAULT_MAX_CONCURRENT_PARSER_INVOCATIONS);
			maxSentenceLength = config.getInt(MAX_SENTENCE_LENGTH_KEY, DEFAULT_MAX_SENTENCE_LENGTH);
		} catch (final ParseException e) {
			return null;
		}
		
		if (maxConcurrentParserInvocations <= 0) {
			config.addConfigurationError("The maximum number of concurrent parser invocations (key \"" + MAX_CONCURRENT_PARSER_INVOCATIONS_KEY + "\") must be greater than zero.");
			error = true;
		}
		
		if (maxSentenceLength <= 0) {
			config.addConfigurationError("The maximum sentence length (key \"" + MAX_SENTENCE_LENGTH_KEY + "\") must be greater than zero.");
			error = true;
		}
		
		if (error)
			return null;
		
		final String mateClass = config.getString("mate.parser.class", DEFAULT_MATE_CLASS);
		final String javaMemory = config.getString("mate.parser.Xmx", DEFAULT_JAVA_MEMORY);
		
		return new MateCaller(javaCommand, javaMemory, mateJar, mateClass, mateModel, maxConcurrentParserInvocations, maxSentenceLength);
	}
	
}
