/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import herma.crawler.linguisticprocessing.ParserCaller;
import herma.crawler.linguisticprocessing.PreParsingResult;
import herma.crawler.relevance.RelevanceDecision;
import herma.crawler.textextraction.TextExtractionMethodInfo;
import herma.crawler.util.CleanupUtil;
import herma.crawler.util.IOUtil;
import herma.crawler.util.PathUtil;


public class SaveAndParseFurtherProcessing implements FurtherProcessing {
	
	private static final String URLS_FILE_NAME = "urls.txt";
	private static final String FILES_FILE_NAME = "files.txt";
	private static final String RELEVANCE_FILE_NAME = "matches.txt";
	private static final String ORIGINAL_DOWNLOAD_DIR_NAME = "original";
	private static final String TXT_DOWNLOAD_DIR_NAME = "txt";
	private static final String EXTRACT_DOWNLOAD_DIR_NAME = "01_Originale";
	private static final String TOKEN_DOWNLOAD_DIR_NAME = "02_Tokenisierung";
	private static final String LEMMA_DOWNLOAD_DIR_NAME = "03_POS_Lemma";
	private static final String PARSER_INPUT_DOWNLOAD_DIR_NAME = "03a_ParserInput";
	private static final String PARSE_DOWNLOAD_DIR_NAME = "04_Parse";
	
	private final Path pUrlsFile;
	private final Path pFilesFile;
	private final Path pRelevanceFile;
	private final Path pOriginalDownloadDir;
	private final Path pTxtDir;
	private final Path pExtractDir;
	private final Path pTokenDir;
	private final Path pLemmaDir;
	private final Path pParserInputDir;
	private final Path pParseDir;
	
	private final ParserCaller pParserCaller;
	
	private final String pCrawlPrefix;
	private final NumberFormat pFilenameIdNumberFormat;
	
	private final AtomicLong pRunningItemId;
	
	public SaveAndParseFurtherProcessing(final Path outputDir, final ParserCaller parserCaller, final String crawlPrefix, final int idDigits) {
		pUrlsFile = PathUtil.combine(outputDir, URLS_FILE_NAME);
		pFilesFile = PathUtil.combine(outputDir, FILES_FILE_NAME);
		pRelevanceFile = PathUtil.combine(outputDir, RELEVANCE_FILE_NAME);
		pOriginalDownloadDir = PathUtil.combine(outputDir, ORIGINAL_DOWNLOAD_DIR_NAME);
		
		pTxtDir = PathUtil.combine(outputDir, TXT_DOWNLOAD_DIR_NAME);
		
		pExtractDir = PathUtil.combine(pTxtDir, EXTRACT_DOWNLOAD_DIR_NAME);
		pTokenDir = PathUtil.combine(pTxtDir, TOKEN_DOWNLOAD_DIR_NAME);
		pLemmaDir = PathUtil.combine(pTxtDir, LEMMA_DOWNLOAD_DIR_NAME);
		pParserInputDir = PathUtil.combine(pTxtDir, PARSER_INPUT_DOWNLOAD_DIR_NAME);
		pParseDir = PathUtil.combine(pTxtDir, PARSE_DOWNLOAD_DIR_NAME);
		
		pParserCaller = parserCaller;
		
		pCrawlPrefix = crawlPrefix;
		pFilenameIdNumberFormat = createFilenameNumberFormat(idDigits);
		
		pRunningItemId = new AtomicLong(0L);
	}
	
	private static NumberFormat createFilenameNumberFormat(final int idDigits) {
		final NumberFormat result = NumberFormat.getInstance(Locale.ROOT);
		
		result.setMinimumIntegerDigits(idDigits);
		result.setMaximumIntegerDigits(idDigits);
		result.setGroupingUsed(false);
		
		return result;
	}
	
	public void createDirectories() throws IOException {
		IOUtil.createDirectoryIfNotExists(pOriginalDownloadDir);
		IOUtil.createDirectoryIfNotExists(pTxtDir);
		
		IOUtil.createDirectoryIfNotExists(pExtractDir);
		IOUtil.createDirectoryIfNotExists(pTokenDir);
		IOUtil.createDirectoryIfNotExists(pLemmaDir);
		IOUtil.createDirectoryIfNotExists(pParserInputDir);
		IOUtil.createDirectoryIfNotExists(pParseDir);
		
		loadLastItemId();
	}
	
	// TODO: I do not like that dependence on file name generation
	// problem though: to avoid it, the running ID would have
	// to be saved separately; that requires database operations
	// and would thus not be so easy, either
	private void loadLastItemId() throws IOException {
		long maxId = 0L;
		try (final BufferedReader reader = Files.newBufferedReader(pUrlsFile, StandardCharsets.UTF_8)) {
			while (true) {
				final String line = reader.readLine();
				if (line == null)
					break;
				final int lastTabIndex = line.lastIndexOf('\t');
				if (lastTabIndex < 0)
					continue;
				final int prevTabIndex = line.lastIndexOf('\t', lastTabIndex - 1);
				if (prevTabIndex < 0)
					continue;
				final String filename = line.substring(prevTabIndex + 1, lastTabIndex);
				final int dotIndex = filename.lastIndexOf('.');
				final String idStr = filename.substring(((dotIndex < 0) ? filename.lastIndexOf('_') : filename.lastIndexOf('_', dotIndex)) + 1);
				final long id;
				try {
					id = Long.parseLong(idStr);
				} catch (final NumberFormatException e) {
					continue;
				}
				if (id > maxId)
					maxId = id;
			}
		} catch (final FileNotFoundException | NoSuchFileException e) {
			return;
		}
		pRunningItemId.set(maxId);
	}
	
	@Override
	public void startFurtherProcessing(final OriginalInfo originalInfo, final Iterable<? extends FurtherProcessingItem> items) {
		final String filenameBase;
		final FileInfo originalFile;
		
		try {
			final long id = pRunningItemId.incrementAndGet();
			
			final Metadata metadata = originalInfo.getMetadata();
			final String title = metadata.getTitle();
			filenameBase = FilenameGenerator.generateFilename(pCrawlPrefix, metadata.getHost(), pFilenameIdNumberFormat.format(id));
			originalFile = saveOriginal(originalInfo, filenameBase);
			
			addLine(pUrlsFile, pUrlsFile, metadata.getDownloadUrl(), DateTimeFormatter.ISO_INSTANT.format(metadata.getDownloadTime()), cleanForTabSeparatedFile(metadata.getMime()), canonicalPath(originalFile.relativePath), cleanForTabSeparatedFile(title));
		} catch (final Exception e) {
			for (final FurtherProcessingItem item : items)
				disposePreParsingResult(item.getPreParsingResult());
			throw e;
		}
		
		for (final FurtherProcessingItem item : items) {
			final PreParsingResult preParsingResult = item.getPreParsingResult();
			try {
				saveFilesAndStartParsing(originalFile, filenameBase, preParsingResult, item.getRelevanceDecision());
			} finally {
				disposePreParsingResult(preParsingResult);
			}
		}
	}
	
	private static void disposePreParsingResult(final PreParsingResult preParsingResult) {
		CleanupUtil.nothrowCleanup(preParsingResult::dispose, "disposing a pre-parsing pipeline output (in further processing)");
	}
	
	private void saveFilesAndStartParsing(final FileInfo originalFile, final String filenameBase, final PreParsingResult preParsingResult, final RelevanceDecision relevanceDecision) {
		final TextExtractionMethodInfo textExtractionMethod = preParsingResult.getTextExtractionMethod();
		final String filenameBaseExtracted = filenameBase + '_' + textExtractionMethod.getShortcut();
		
		final FileInfo extracted = saveExtractedText(preParsingResult, filenameBaseExtracted);
		final FileInfo tokenized = saveTokenization(preParsingResult, filenameBaseExtracted);
		final FileInfo lemmatized = saveLemmatization(preParsingResult, filenameBaseExtracted);
		final FileInfo parserInput = createParserInputFile(filenameBaseExtracted);
		final FileInfo parsed = createParserOutputFile(filenameBaseExtracted);
		
		final String canonicalExtractedPath = canonicalPath(extracted.relativePath);
		addLine(pFilesFile, pFilesFile, canonicalPath(originalFile.relativePath), textExtractionMethod.getName(), canonicalExtractedPath, canonicalPath(tokenized.relativePath), canonicalPath(lemmatized.relativePath), canonicalPath(parsed.relativePath));
		
		final Iterable<String[]> relevanceOutputs = relevanceDecision.getRelevanceOutputs();
		if (relevanceOutputs != null) {
			for (final String[] relevanceOutput : relevanceOutputs) {
				final String[] output = new String[relevanceOutput.length + 1];
				System.arraycopy(relevanceOutput, 0, output, 1, relevanceOutput.length);
				output[0] = canonicalExtractedPath;
				addLine(pRelevanceFile, pRelevanceFile, output);
			}
		}
		
		if (pParserCaller.callParser(lemmatized.absolutePath, parserInput.absolutePath, parsed.absolutePath))
			return;
		
		try {
			Files.deleteIfExists(parserInput.absolutePath);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private FileInfo saveOriginal(final OriginalInfo originalInfo, final String encodedTitle) {
		return saveByCall(pOriginalDownloadDir, encodedTitle, originalInfo.getFileNameExtension(), originalInfo::saveTo);
	}
	
	private FileInfo saveExtractedText(final PreParsingResult preParsingResult, final String encodedTitle) {
		return saveByCall(pExtractDir, encodedTitle, ".txt", preParsingResult::saveOriginalAs);
	}
	
	private FileInfo saveTokenization(final PreParsingResult preParsingResult, final String encodedTitle) {
		return saveByCall(pTokenDir, encodedTitle, ".txt", preParsingResult::saveTokenizationAs);
	}
	
	private FileInfo saveLemmatization(final PreParsingResult preParsingResult, final String encodedTitle) {
		return saveByCall(pLemmaDir, encodedTitle, ".txt", preParsingResult::saveLemmatizationAs);
	}
	
	private FileInfo createParserInputFile(final String encodedTitle) {
		try {
			return createNewFile(pParserInputDir, encodedTitle, ".txt");
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private FileInfo createParserOutputFile(final String encodedTitle) {
		try {
			return createNewFile(pParseDir, encodedTitle, ".txt");
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	// extension including dot (.txt, not txt)
	private static FileInfo saveByCall(final Path directory, final String preferredFilename, final String extension, final FileSaver fileSaver) {
		try {
			final FileInfo newFile = createNewFile(directory, preferredFilename, extension);
			try {
				fileSaver.saveTo(newFile.absolutePath);
			} catch (final Exception e) {
				Files.deleteIfExists(newFile.absolutePath);
				throw e;
			}
			return newFile;
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	// extension including dot (.txt, not txt)
	private static FileInfo createNewFile(final Path directory, final String preferredFilename, final String extension) throws IOException {
		int counter = 0;
		String filename = preferredFilename;
		while (true) {
			final String filenameWithExtension = filename + extension;
			final Path pathRel = directory.getFileSystem().getPath(filenameWithExtension);
			final Path path = directory.resolve(pathRel);
			try {
				Files.createFile(path);
			} catch (final FileAlreadyExistsException e) {
				counter++;
				filename = preferredFilename + '_' + Integer.toString(counter);
				continue;
			}
			return new FileInfo(filenameWithExtension, pathRel, path);
		}
	}
	
	private void addLine(final Object lock, final Path file, final String... items) {
		boolean first = true;
		synchronized (lock) {
			try (final BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
				for (final String item : items) {
					if (first)
						first = false;
					else
						writer.write('\t');
					writer.write(item);
				}
				writer.write('\n');
				writer.flush();
			} catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	
	private static String cleanForTabSeparatedFile(final String str) {
		final int n = str.length();
		final StringBuilder result = new StringBuilder();
		int next;
		for (int i = 0; i < n; i = next) {
			final int codePoint = str.codePointAt(i);
			next = i + Character.charCount(codePoint);
			if (Character.isISOControl(codePoint))
				result.appendCodePoint(' ');
			else
				result.appendCodePoint(codePoint);
		}
		
		return result.toString();
	}
	
	private static String canonicalPath(final Path path) {
		final StringBuilder sb = new StringBuilder();
		if (path.isAbsolute())
			sb.appendCodePoint('/');
		boolean first = true;
		for (final Path nameElement : path) {
			if (first)
				first = false;
			else
				sb.appendCodePoint('/');
			sb.append(nameElement.toString());
		}
		return sb.toString();
	}
	
	@FunctionalInterface
	private static interface FileSaver {
		
		void saveTo(Path file) throws IOException;
		
	}
	
	private static class FileInfo {
		public final Path relativePath;
		public final Path absolutePath;
		
		public FileInfo(final String filename, final Path relativePath, final Path absolutePath) {
			this.relativePath = relativePath;
			this.absolutePath = absolutePath;
		}
	}
	
}
