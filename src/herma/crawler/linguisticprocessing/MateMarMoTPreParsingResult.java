/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.linguisticprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.regex.Pattern;

import herma.crawler.textextraction.TextExtractionMethodInfo;
import herma.crawler.util.CleanupUtil;

public class MateMarMoTPreParsingResult implements PreParsingResult {
	
	private static final Pattern TAB_PATTERN = Pattern.compile(Pattern.quote("\t"));
	
	private final Path[] pInputFiles;
	private final Path pTokensFile;
	private final Path pLemmaFile;
	
	private final ArrayList<Token> pTokens;
	
	private final TextExtractionMethodInfo pTextExtractionMethod;
	
	public MateMarMoTPreParsingResult(final Path[] inputFiles, final Path tokensFile, final Path lemmaFile, final TextExtractionMethodInfo textExtractionMethod) {
		pInputFiles = inputFiles;
		pTokensFile = tokensFile;
		pLemmaFile = lemmaFile;
		
		pTokens = loadTokens(lemmaFile);
		
		pTextExtractionMethod = textExtractionMethod;
	}
	
	private static ArrayList<Token> loadTokens(final Path lemmaFile) {
		final ArrayList<Token> result = new ArrayList<>();
		try (final BufferedReader reader = Files.newBufferedReader(lemmaFile, StandardCharsets.UTF_8)) {
			while (true) {
				final String line = reader.readLine();
				if (line == null)
					break;
				final String[] parts = TAB_PATTERN.split(line);
				if (parts.length > 3)
					result.add(new Token(parts[1], parts[3]));
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		return result;
	}
	
	@Override
	public Iterable<? extends Token> getTokens() {
		return pTokens;
	}
	
	@Override
	public TextExtractionMethodInfo getTextExtractionMethod() {
		return pTextExtractionMethod;
	}
	
	@Override
	public void saveOriginalAs(final Path filename) throws IOException {
		// file could already exist, hence REPLACE_EXISTING.
		// The current implementation calls the method is ALWAYS with an already existing (but empty) file.
		boolean noEmptyLineBefore = false;
		try (final BufferedWriter writer = Files.newBufferedWriter(filename, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			for (final Path inputFile : pInputFiles) {
				try (final BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
					if (noEmptyLineBefore)
						writer.write('\n');
					while (true) {
						final String line = reader.readLine();
						if (line == null)
							break;
						noEmptyLineBefore = !"".equals(line);
						writer.write(line);
						writer.write('\n');
					}
				}
			}
			writer.flush();
		}
	}
	
	@Override
	public void saveTokenizationAs(final Path filename) throws IOException {
		// file could already exist, hence REPLACE_EXISTING.
		// The current implementation calls the method is ALWAYS with an already existing (but empty) file.
		Files.copy(pTokensFile, filename, StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Override
	public void saveLemmatizationAs(final Path filename) throws IOException {
		// file could already exist, hence REPLACE_EXISTING.
		// The current implementation calls the method is ALWAYS with an already existing (but empty) file.
		Files.copy(pLemmaFile, filename, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void dispose() {
		for (final Path inputFile : pInputFiles)
			CleanupUtil.nothrowCleanup(() -> Files.deleteIfExists(inputFile), "deleting a pre-parsing pipeline input file");
		CleanupUtil.nothrowCleanup(() -> Files.deleteIfExists(pTokensFile), "deleting a pre-parsing pipeline tokens file");
		CleanupUtil.nothrowCleanup(() -> Files.deleteIfExists(pLemmaFile), "deleting a pre-parsing pipeline lemma file");
	}
	
}
