/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.regex.Pattern;

import herma.crawler.textextraction.ExtractedText;
import herma.crawler.util.RunUtil;


public class PipelineMateMarMoT implements PreParsingPipeline {
	
	private static final Pattern TAB_PATTERN = Pattern.compile(Pattern.quote("\t"));
	
	private final String pPythonCommand;
	private final String pJavaCommand;
	
	private final Path pTokenizerScript;
	
	private final Path pMarmotJar;
	private final String pMarmotClass;
	
	private final Path pMateJar;
	private final String pMateClass;
	
	private final Path pMarmotModel;
	private final Path pMateModel;
	
	private final Path pPythonLibs;
	private final Path pNltkData;
	
	private final String pJavaMemoryMarMoT;
	private final String pJavaMemoryMate;
	
	private final Semaphore pToolRunSemaphore;
	
	public PipelineMateMarMoT(final String pythonCommand, final String javaCommand, final Path toolsDir, final String javaMemoryMarMoT, final String javaMemoryMate, final Path tokenizerScript, final Path marmotJar, final String marmotClass, final Path marmotModel, final Path mateJar, final String mateClass, final Path mateModel, final Path pythonLibsPath, final Path nltkDataPath, final int maxConcurrentToolInvocations) {
		pPythonCommand = pythonCommand;
		pJavaCommand = javaCommand;
		
		pTokenizerScript = tokenizerScript;
		
		pMarmotJar = marmotJar;
		pMarmotClass = marmotClass;
		
		pMateJar = mateJar;
		pMateClass = mateClass;
		
		pMarmotModel = marmotModel;
		pMateModel = mateModel;
		
		pPythonLibs = pythonLibsPath;
		pNltkData = nltkDataPath;
		
		pJavaMemoryMarMoT = "-Xmx" + javaMemoryMarMoT;
		pJavaMemoryMate = "-Xmx" + javaMemoryMate;
		
		pToolRunSemaphore = new Semaphore(maxConcurrentToolInvocations, true);
	}
	
	@Override
	public PreParsingResult apply(final ExtractedText extractedText) {
		pToolRunSemaphore.acquireUninterruptibly();
		try {
			return runPipeline(extractedText);
		} finally {
			pToolRunSemaphore.release();
		}
	}

	private PreParsingResult runPipeline(final ExtractedText extractedText) {
		try {
			final Path[] inputFiles = extractedText.getTempFilesPaths();
			final Path tokensFile = Files.createTempFile("hermA-crawler-", ".txt");
			try {
				tokenize(inputFiles, tokensFile);
				
				final Path morphFile = Files.createTempFile("hermA-crawler-", ".txt");
				try {
					morphologize(tokensFile, morphFile);
					
					final Path lemmaFile = Files.createTempFile("hermA-crawler-", ".txt");
					try {
						lemmatize(morphFile, lemmaFile);
						
						return new MateMarMoTPreParsingResult(inputFiles, tokensFile, lemmaFile, extractedText.getTextExtractionMethod());
					} catch (final Exception e) {
						Files.deleteIfExists(lemmaFile);
						throw e;
					}
				} finally {
					Files.deleteIfExists(morphFile);
				}
			} catch (final Exception e) {
				Files.deleteIfExists(tokensFile);
				throw e;
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private void tokenize(final Path[] inputFiles, final Path tokensFile) throws IOException {
		final Path tempFile = Files.createTempFile("hermA-crawler-", ".txt");
		try {
			final String tempFilePath = tempFile.toString();
			try (final BufferedWriter writer = Files.newBufferedWriter(tokensFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
				boolean inner = false;
				for (final Path inputFile : inputFiles) {
					callWithPythonpath(pPythonCommand, pTokenizerScript.toString(), inputFile.toString(), tempFilePath);
					try (final BufferedReader reader = Files.newBufferedReader(tempFile, StandardCharsets.UTF_8)) {
						boolean betweenSentences = inner;
						while (true) {
							final String line = reader.readLine();
							if (line == null)
								break;
							if ("".equals(line)) {
								if (inner)
									betweenSentences = true;
							} else {
								inner = true;
								if (betweenSentences)
									writer.write('\n');
								betweenSentences = false;
								writer.write(line);
								writer.write('\n');
							}
						}
					}
				}
				writer.write('\n');
				writer.flush();
			}
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}
	
	private void morphologize(final Path tokensFile, final Path morphFile) throws IOException {
		final Path tempFile = Files.createTempFile("hermA-TP4Crawler-", ".txt");
		try {
			call(pJavaCommand, pJavaMemoryMarMoT, "-Dfile.encoding=UTF-8", "-cp", pMarmotJar.toString(), pMarmotClass, "--model-file", pMarmotModel.toString(), "--test-file", "form-index=0," + tokensFile.toString(), "--pred-file", tempFile.toString());
			
			try (final BufferedWriter writer = Files.newBufferedWriter(morphFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
				try (final BufferedReader reader = Files.newBufferedReader(tempFile, StandardCharsets.UTF_8)) {
					while (true) {
						final String line = reader.readLine();
						if (line == null)
							break;
						if ("".equals(line))
							writer.write("");
						else
							convertMarMoTCoNLLToMate(writer, line);
						writer.write('\n');
					}
				}
				writer.flush();
			}
		} finally {
			Files.deleteIfExists(tempFile);
		}
	}
	
	private static void convertMarMoTCoNLLToMate(final BufferedWriter writer, final String marmotLine) throws IOException {
		final String[] parts = TAB_PATTERN.split(marmotLine);
		final String token = parts[1];
		final String pos = splitPos(parts[5]);
		writer.write(parts[0]);
		writer.write("\t");
		switch (token) {
			case "``":
			case "''":
				writer.write("\"");
				break;
			case "\u2012":
			case "\u2013":
			case "\u2014":
			case "\u2015":
				writer.write("-");
				break;
			default:
				writer.write(token);
				break;
		}
		writer.write("\t_\t_\t_\t");
		writer.write("$LRB".equals(pos) ? "$(" : pos);
		writer.write("\t_\t");
		writer.write(parts[7]);
		writer.write("\t_\t_\t_\t_\t_\t_");
	}
	
	private static String splitPos(final String pos) {
		final int pipeIndex = pos.indexOf('|');
		if (pipeIndex < 0)
			return pos;
		return pos.substring(0, pipeIndex);
	}
	
	private void lemmatize(final Path morphFile, final Path lemmaFile) throws IOException {
		call(pJavaCommand, pJavaMemoryMate, "-Dfile.encoding=UTF-8", "-cp", pMateJar.toString(), pMateClass, "-model", pMateModel.toString(), "-test", morphFile.toString(), "-out", lemmaFile.toString());
	}
	
	private static void call(final String... command) throws IOException {
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		
		processBuilder.redirectInput(Redirect.PIPE);
		processBuilder.redirectError(Redirect.PIPE);
		processBuilder.redirectOutput(Redirect.PIPE);
		
		callAndWait(processBuilder);
	}
	
	private void callWithPythonpath(final String... command) throws IOException {
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		
		processBuilder.redirectInput(Redirect.PIPE);
		processBuilder.redirectError(Redirect.PIPE);
		processBuilder.redirectOutput(Redirect.PIPE);
		
		setupEnvironment(processBuilder);
		
		callAndWait(processBuilder);
	}
	
	private void setupEnvironment(final ProcessBuilder processBuilder) {
		final Map<String, String> env = processBuilder.environment();
		
		if (pPythonLibs != null)
			addPath(env, "PYTHONPATH", pPythonLibs);
		if (pNltkData != null)
			addPath(env, "NLTK_DATA", pNltkData);
	}
	
	private void addPath(final Map<String, String> environmentVariables, final String environmentVariableName, final Path pathToAdd) {
		final String path = environmentVariables.getOrDefault(environmentVariableName, null);
		if (path == null)
			environmentVariables.put(environmentVariableName, pathToAdd.toString());
		else
			environmentVariables.put(environmentVariableName, path + File.pathSeparator + pathToAdd.toString());
	}
	
	private static void callAndWait(final ProcessBuilder processBuilder) throws IOException {
		final Process process = processBuilder.start();
		RunUtil.consumeStreamsToNowhere(process);
		
		try {
			process.waitFor();
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
}
