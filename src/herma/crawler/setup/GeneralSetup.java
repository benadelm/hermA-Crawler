/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.nio.file.Path;
import java.text.ParseException;

import herma.crawler.config.Configuration;

public class GeneralSetup {
	
	private static final String OUTPUT_DIR_KEY = "output";
	private static final String PREFIX_KEY = "prefix";
	private static final String DOWNLOAD_THREADS_KEY = "downloadThreads";
	
	public static Path loadPathBase(final Configuration config) {
		return config.getPathBase("pathBase");
	}
	
	public static Path loadOutputDirectory(final Configuration config, final Path pathBase) {
		final Path result = config.getPath(pathBase, OUTPUT_DIR_KEY);
		
		if (result == null)
			config.addConfigurationError("You have to specify an output directory (key \"" + OUTPUT_DIR_KEY + "\").");
		
		return result;
	}
	
	public static Path loadToolsPath(final Configuration config, final Path pathBase) {
		final Path result = config.getPath(pathBase, "tools");
		if (result == null)
			return pathBase;
		return result;
	}
	
	public static String loadCrawlPrefix(final Configuration config) {
		final String result = config.getString(PREFIX_KEY);
		
		if (result == null)
			config.addConfigurationError("You have to specify a prefix for output files (key \"" + PREFIX_KEY + "\").");
		
		return result;
	}
	
	public static int loadNumberOfDownloadThreads(final Configuration config) {
		final Integer resultObj;
		try {
			resultObj = config.getInt(DOWNLOAD_THREADS_KEY);
		} catch (final ParseException e) {
			return 0;
		}
		if (resultObj == null) {
			config.addConfigurationError("You have to specify the number of download threads (key \"" + DOWNLOAD_THREADS_KEY + "\").");
			return 0;
		}
		final int result = resultObj.intValue();
		if (result > 0)
			return result;
		config.addConfigurationError("The number of download threads (key \"" + DOWNLOAD_THREADS_KEY + "\") must be greater than zero.");
		return 0;
	}
	
}
