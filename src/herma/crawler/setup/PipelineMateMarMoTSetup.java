/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.nio.file.Path;
import java.text.ParseException;

import herma.crawler.config.Configuration;
import herma.crawler.linguisticprocessing.PipelineMateMarMoT;

public class PipelineMateMarMoTSetup {
	
	private static final String TOKENIZER_SCRIPT_KEY = "tokenizer.script";
	private static final String MARMOT_JAR_KEY = "marmot.jar";
	private static final String MARMOT_MODEL_KEY = "marmot.model";
	private static final String MATE_JAR_KEY = "mate.lemmatizer.jar";
	private static final String MATE_MODEL_KEY = "mate.lemmatizer.model";
	
	private static final String MARMOT_CLASS_KEY = "marmot.class";
	private static final String DEFAULT_MARMOT_CLASS = "marmot.morph.cmd.Annotator";
	
	private static final String MATE_CLASS_KEY = "mate.lemmatizer.class";
	private static final String DEFAULT_MATE_CLASS = "is2.lemmatizer.Lemmatizer";
	
	private static final String MAX_CONCURRENT_PIPELINE_INVOCATIONS_KEY = "maxConcurrentPipelineInvocations";
	private static final int DEFAULT_MAX_CONCURRENT_PIPELINE_INVOCATIONS = 20;
	
	private static final String DEFAULT_JAVA_MEMORY_MARMOT = "2g";
	private static final String DEFAULT_JAVA_MEMORY_MATE = "1g";
	
	public static PipelineMateMarMoT setupPipelineMateMarMoT(final Configuration config, final Path pathBase, final Path toolsDir, final String pythonCommand, final String javaCommand) {
		boolean error = false;
		
		final Path tokenizerScript = config.getPath(toolsDir, TOKENIZER_SCRIPT_KEY);
		if (tokenizerScript == null) {
			config.addConfigurationError("You have to specify a path to the tokenizer Python script (key \"" + TOKENIZER_SCRIPT_KEY + "\").");
			error = true;
		}
		
		final Path marmotJar = config.getPath(toolsDir, MARMOT_JAR_KEY);
		if (marmotJar == null) {
			config.addConfigurationError("You have to specify a path to the JAR file for MarMoT (key \"" + MARMOT_JAR_KEY + "\").");
			error = true;
		}
		
		final Path marmotModel = config.getPath(toolsDir, MARMOT_MODEL_KEY);
		if (marmotModel == null) {
			config.addConfigurationError("You have to specify a path to the model file for MarMoT (key \"" + MARMOT_MODEL_KEY + "\").");
			error = true;
		}
		
		final Path mateJar = config.getPath(toolsDir, MATE_JAR_KEY);
		if (mateJar == null) {
			config.addConfigurationError("You have to specify a path to the JAR file for the MATE lemmatizer (key \"" + MATE_JAR_KEY + "\").");
			error = true;
		}
		
		final Path mateModel = config.getPath(toolsDir, MATE_MODEL_KEY);
		if (mateModel == null) {
			config.addConfigurationError("You have to specify a path to the model file for the MATE lemmatizer (key \"" + MATE_MODEL_KEY + "\").");
			error = true;
		}
		
		final int maxConcurrentPipelineInvocations;
		try {
			maxConcurrentPipelineInvocations = config.getInt(MAX_CONCURRENT_PIPELINE_INVOCATIONS_KEY, DEFAULT_MAX_CONCURRENT_PIPELINE_INVOCATIONS);
		} catch (final ParseException e) {
			return null;
		}
		
		if (maxConcurrentPipelineInvocations <= 0) {
			config.addConfigurationError("The maximum number of concurrent linguistic pipeline invocations (key \"" + MAX_CONCURRENT_PIPELINE_INVOCATIONS_KEY + "\") must be greater than zero.");
			return null;
		}
		
		if (error)
			return null;
		
		final String javaMemoryMarMoT = config.getString("marmot.Xmx", DEFAULT_JAVA_MEMORY_MARMOT);
		final String javaMemoryMate = config.getString("mate.lemmatizer.Xmx", DEFAULT_JAVA_MEMORY_MATE);
		
		final String marmotClass = config.getString(MARMOT_CLASS_KEY, DEFAULT_MARMOT_CLASS);
		final String mateClass = config.getString(MATE_CLASS_KEY, DEFAULT_MATE_CLASS);
		
		final Path pythonLibPath = config.getPath(pathBase, "pythonLib");
		final Path nltkDataPath = config.getPath(pathBase, "nltkData");
		
		return new PipelineMateMarMoT(pythonCommand, javaCommand, toolsDir, javaMemoryMarMoT, javaMemoryMate, tokenizerScript, marmotJar, marmotClass, marmotModel, mateJar, mateClass, mateModel, pythonLibPath, nltkDataPath, maxConcurrentPipelineInvocations);
	}
	
}
