/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
//import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.http.impl.client.HttpClientBuilder;

import herma.crawler.agenda.MongoDbAgenda;
import herma.crawler.agenda.MongoDbAgendaBackupThread;
import herma.crawler.config.Configuration;
import herma.crawler.contenthandlers.ContentHandlerContext;
import herma.crawler.contenthandlers.html.HtmlContentHandler;
import herma.crawler.contenthandlers.html.links.BasicHtmlLinkExtractor;
import herma.crawler.contenthandlers.html.links.HtmlLinkExtractor;
import herma.crawler.contenthandlers.html.links.LinkFactory;
import herma.crawler.contenthandlers.pdf.PdfContentHandler;
import herma.crawler.contenthandlers.pdf.PdfTitleExtractor;
import herma.crawler.contenthandlers.prtne.PRTNEContentHandler;
import herma.crawler.contenthandlers.text.TextContentHandler;
import herma.crawler.db.MongoDbContext;
import herma.crawler.db.TimedConnectionScheduler;
import herma.crawler.errorhandlers.PrintToFileErrorHandler;
import herma.crawler.linguisticprocessing.MateCaller;
import herma.crawler.linguisticprocessing.PipelineMateMarMoT;
import herma.crawler.links.LinkProcessor;
import herma.crawler.meta.LinkCollector;
import herma.crawler.relevance.linguistic.LinguisticPipelineRelevanceDecider;
import herma.crawler.setup.AgendaSetup;
import herma.crawler.setup.BlacklistSetup;
import herma.crawler.setup.ForeignHostFilterLinkFactorySetup;
import herma.crawler.setup.GeneralSetup;
import herma.crawler.setup.HttpClientSetup;
import herma.crawler.setup.JavaSetup;
import herma.crawler.setup.KeyphrasesRelevanceDeciderSetup;
import herma.crawler.setup.LinkProcessorSetup;
import herma.crawler.setup.MateCallerSetup;
import herma.crawler.setup.MongoDbSetup;
import herma.crawler.setup.PipelineMateMarMoTSetup;
import herma.crawler.setup.ProcessingThreadStarterSetup;
import herma.crawler.setup.PythonSetup;
import herma.crawler.setup.SaveAndParseFurtherProcessingSetup;
import herma.crawler.setup.TimedConnectionSchedulerSetup;
import herma.crawler.setup.XpdfSetup;
import herma.crawler.stopping.DirectoryStopListener;
import herma.crawler.stopping.StopListener;
import herma.crawler.textextraction.BoilerpipeTextExtractor;
import herma.crawler.textextraction.Html5MainTextExtractor;
import herma.crawler.textextraction.HtmlTextExtractor;
import herma.crawler.textextraction.PdfTextExtractor;
import herma.crawler.textextraction.XpdfTextExtractor;
import herma.crawler.util.IOUtil;
import herma.crawler.util.PathUtil;

public class Main {
	
	public static void main(final String[] args) {
		if (args.length != 1) {
			System.err.println("Invalid number of command line arguments.");
			System.err.println("Expecting one argument: config file path");
			System.err.println("(configuration file is expected to be a UTF-8 Java properties file)");
			System.exit(2);
			return;
		}
		
		final Path configPath = FileSystems.getDefault().getPath(args[0]).toAbsolutePath().normalize();
		final Configuration config = new Configuration(configPath);
		try {
			config.load();
		} catch (final IOException | IllegalArgumentException e) {
			printConfigurationLoadError(configPath, e);
			System.exit(1);
			return;
		}
		
		final Path pathBase = GeneralSetup.loadPathBase(config);
		final Path outputDirectory = GeneralSetup.loadOutputDirectory(config, pathBase);
		final String crawlPrefix = GeneralSetup.loadCrawlPrefix(config);
		final int downloadThreads = GeneralSetup.loadNumberOfDownloadThreads(config);
		
		// ******** database-specific ********
		
		Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF); // plain Java logging
		//LogManager.getLogger("org.mongodb.driver").setLevel(Level.OFF); // if you link to log4j
		
		final TimedConnectionScheduler dbConnectionScheduler = TimedConnectionSchedulerSetup.setupTimedConnectionScheduler(config);
		final MongoDbContext db = MongoDbSetup.setupMongoDbContext(config, crawlPrefix, dbConnectionScheduler);
		
		final MongoDbAgenda agenda = createAgenda(db);
		
		final Path metaDirectory = PathUtil.combine(outputDirectory, "meta");
		final MetaCollector metaCollector = new MetaCollector(db, metaDirectory);
		
		final Path resumeDirectory = PathUtil.combine(outputDirectory, "resume");
		final Path hostRelevanceFile = PathUtil.combine(resumeDirectory, "hosts.txt");
		
		final MongoDbAgendaBackupThread backupThread = new MongoDbAgendaBackupThread(agenda, hostRelevanceFile, MongoDbSetup.loadBackupInterval(config));
		
		// ******** END database-specific ********
		
		// ******** general setup ********
		
		final ThreadManager threadManager = new ThreadManager();
		final ErrorHandler errorHandler = new PrintToFileErrorHandler(PathUtil.combine(outputDirectory, "errors.txt"));
		
		final Blacklist blacklist = BlacklistSetup.setupBlacklist(config, pathBase);
		final LinkProcessor linkProcessor = new LinkProcessor(blacklist, metaCollector.createMetaInformationStringCollection("unfollowedschemata", true), errorHandler);
		LinkProcessorSetup.setupLinkProcessor(linkProcessor);
		
		AgendaSetup.initiallyFillAgenda(agenda, config, pathBase, linkProcessor);
		
		final LinkCollector linkCollector = new LinkCollector(metaCollector.createMetaInformationStringCollection("links-raw", false));
		
		final StopListener stopListener = new DirectoryStopListener(outputDirectory, agenda::abort);
		
		// ******** END general setup ********
		
		// ******** linguistic processing tools setup ********
		
		final String pythonCommand = PythonSetup.loadPythonCommand(config);
		final String javaCommand = JavaSetup.loadJavaCommand(config);
		
		final Path toolsPath = GeneralSetup.loadToolsPath(config, pathBase);
		final PipelineMateMarMoT preParsingPipeline = PipelineMateMarMoTSetup.setupPipelineMateMarMoT(config, pathBase, toolsPath, pythonCommand, javaCommand);
		final MateCaller parserCaller = MateCallerSetup.setupMateCaller(config, toolsPath, javaCommand);
		final SaveAndParseFurtherProcessing furtherProcessing = SaveAndParseFurtherProcessingSetup.setupSaveAndParseFurtherProcessing(config, outputDirectory, parserCaller, crawlPrefix);
		final LinguisticPipelineRelevanceDecider relevanceDecider = new LinguisticPipelineRelevanceDecider(errorHandler, preParsingPipeline, KeyphrasesRelevanceDeciderSetup.setupKeyphrasesRelevanceDecider(config, pathBase), furtherProcessing);
		
		// ******** END linguistic processing tools setup ********
		
		// ******** content handling setup ********
		
		final ContentHandlerContext contentHandlerContext = new ContentHandlerContext(agenda, errorHandler, relevanceDecider, linkCollector, metaCollector.createMetaInformationStringCollection("processedurls", true));
		
		final DownloaderBuilder downloaderBuilder = new DownloaderBuilder();
		downloaderBuilder.setLinkProcessor(linkProcessor);
		downloaderBuilder.setRestager(agenda);
		downloaderBuilder.setProcessingThreadStarter(ProcessingThreadStarterSetup.setupProcessingThreadStarter(config, threadManager, contentHandlerContext));
		downloaderBuilder.setErrorHandler(errorHandler);
		downloaderBuilder.setLinkCollector(linkCollector);
		downloaderBuilder.setUnhandledMimeTypes(metaCollector.createMetaInformationStringCollection("unhandledmime", true));
		downloaderBuilder.setContentHandlers(setupContentHandlers(config, pathBase, linkProcessor, contentHandlerContext, metaCollector));
		downloaderBuilder.setIgnoreMime(setupIgnoreMime());
		
		// ******** END content handling setup ********
		
		final HttpClientBuilder httpClientBuilder = HttpClientSetup.setupHttpClient(config);
		
		if (!config.validate()) {
			System.exit(1);
			return;
		}
		
		// ******** configuration finished ********
		
		if (!Files.isDirectory(outputDirectory)) {
			System.err.print("Intended output directory [");
			System.err.print(outputDirectory.toString());
			System.err.println("] cannot be found or is not a directory.");
			System.exit(1);
			return;
		}
		
		try {
			furtherProcessing.createDirectories();
			metaCollector.createDirectories();
			IOUtil.createDirectoryIfNotExists(resumeDirectory);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
		
		stopListener.startListening();
		
		Crawler.run(downloadThreads, httpClientBuilder, threadManager, agenda, downloaderBuilder, backupThread::start);
		
		stopListener.stopListening();
		parserCaller.stopWaiting();
		metaCollector.stop();
		
		backupThread.terminate();
		agenda.save(hostRelevanceFile);
		
		dbConnectionScheduler.shutdown();
		
		System.out.println("Will now terminate.");
	}
	
	private static void printConfigurationLoadError(final Path configPath, final Exception e) {
		System.err.print("Cannot load configuration file [");
		System.err.print(configPath.toString());
		System.err.println("]:");
		printConfigurationLoadErrorMessage(e);
	}
	
	private static void printConfigurationLoadErrorMessage(final Exception exception) {
		try {
			throw exception;
		} catch (final NoSuchFileException e) {
			printReason(e, "File not found.");
		} catch (final AccessDeniedException e) {
			printReason(e, "Access denied.");
		} catch (final FileSystemException e) {
			printReason(e, "I/O error.");
		} catch (final FileNotFoundException e) {
			System.err.print("File not found.");
		} catch (final IOException e) {
			System.err.print("I/O error.");
		} catch (final Exception e) {
			System.err.print(e.getClass().getTypeName());
			System.err.print(": ");
			System.err.print(e.getLocalizedMessage());
		}
		System.err.println();
	}
	
	private static void printReason(final FileSystemException e, final String defaultReason) {
		final String reason = e.getReason();
		if (reason == null)
			System.err.print(defaultReason);
		else
			System.err.print(reason);
	}
	
	private static MongoDbAgenda createAgenda(final MongoDbContext db) {
		if (db == null)
			return null;
		final MongoDbAgenda agenda = new MongoDbAgenda(db);
		agenda.resetStates();
		return agenda;
	}
	
	private static HashSet<String> setupIgnoreMime() {
		final HashSet<String> ignoreMime = new HashSet<>();
		ignoreMime.add("image/jpeg");
		ignoreMime.add("image/gif");
		ignoreMime.add("image/svg+xml");
		ignoreMime.add("image/x-ico");
		ignoreMime.add("image/png");
		ignoreMime.add("audio/mpeg");
		ignoreMime.add("video/mp4");
		ignoreMime.add("application/octet-stream");
		ignoreMime.add("application/x-shockwave-flash");
		ignoreMime.add("application/pgp-signature");
		ignoreMime.add("text/vcard");
		return ignoreMime;
	}
	
	private static HashMap<String, ContentHandler> setupContentHandlers(final Configuration config, final Path pathBase, final LinkProcessor linkProcessor, final ContentHandlerContext contentHandlerContext, final MetaCollector metaCollector) {
		final ArrayList<HtmlTextExtractor> htmlTextExtractors = new ArrayList<>();
		htmlTextExtractors.add(new Html5MainTextExtractor());
		htmlTextExtractors.add(new BoilerpipeTextExtractor());
		
		final LinkFactory linkFilter = ForeignHostFilterLinkFactorySetup.setupForeignHostFilterLinkFactory(config);
		final HtmlLinkExtractor linkExtractor = new BasicHtmlLinkExtractor(linkProcessor, linkFilter);
		
		final ArrayList<PdfTextExtractor> pdfTextExtractors = new ArrayList<>();
		final XpdfTextExtractor xpdfTextExtractor = XpdfSetup.setupXpdfTextExtractor(config, pathBase);
		if (xpdfTextExtractor != null)
			pdfTextExtractors.add(xpdfTextExtractor);
		
		final PdfTitleExtractor pdfTitleExtractor = XpdfSetup.setupPdfTitleExtractor(config, pathBase);
		
		final HtmlContentHandler htmlContentHandler = new HtmlContentHandler(htmlTextExtractors, linkExtractor, contentHandlerContext);
		final PdfContentHandler pdfContentHandler = new PdfContentHandler(pdfTitleExtractor, pdfTextExtractors, contentHandlerContext);
		final TextContentHandler textContentHandler = new TextContentHandler(contentHandlerContext);
		final PRTNEContentHandler prtneContentHandler = new PRTNEContentHandler(metaCollector.createMetaInformationStringCollection("prtne", false));
		
		final HashMap<String, ContentHandler> contentHandlers = new HashMap<>();
		contentHandlers.put("text/html", htmlContentHandler);
		contentHandlers.put("text/plain", textContentHandler);
		
		if (pdfTextExtractors.isEmpty()) {
			contentHandlers.put("application/pdf", prtneContentHandler);
			contentHandlers.put("application/x-pdf", prtneContentHandler);
		} else {
			contentHandlers.put("application/pdf", pdfContentHandler);
			contentHandlers.put("application/x-pdf", pdfContentHandler);
		}
		
		contentHandlers.put("application/xml", prtneContentHandler);
		contentHandlers.put("text/xml", prtneContentHandler);
		contentHandlers.put("application/rss+xml", prtneContentHandler);
		contentHandlers.put("application/postscript", prtneContentHandler);
		contentHandlers.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", prtneContentHandler);
		contentHandlers.put("application/vnd.ms-excel", prtneContentHandler);
		contentHandlers.put("application/msword", prtneContentHandler);
		contentHandlers.put("application/vnd.ms-powerpoint", prtneContentHandler);
		contentHandlers.put("application/rtf", prtneContentHandler);
		contentHandlers.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", prtneContentHandler);
		contentHandlers.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", prtneContentHandler);
		contentHandlers.put("application/zip", prtneContentHandler);
		
		return contentHandlers;
	}
	
}
