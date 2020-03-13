/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

public class Configuration {
	
	private static final NumberFormat NUMBER_FORMAT;
	
	private final Path pConfigurationFilePath;
	private final Properties pProperties;
	
	private final HashSet<String> pUsedKeys;
	
	private final ArrayList<String> pConfigurationErrors;
	private final ArrayList<String> pConfigurationWarnings;
	
	static {
		NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.ROOT);
		NUMBER_FORMAT.setGroupingUsed(false);
		NUMBER_FORMAT.setMinimumIntegerDigits(1);
		NUMBER_FORMAT.setMaximumIntegerDigits(Integer.MAX_VALUE);
	}
	
	public Configuration(final Path configurationFilePath) {
		pConfigurationFilePath = configurationFilePath;
		pProperties = new Properties();
		
		pUsedKeys = new HashSet<>();
		
		pConfigurationErrors = new ArrayList<>();
		pConfigurationWarnings = new ArrayList<>();
	}
	
	public void load() throws IOException, IllegalArgumentException {
		try (final BufferedReader reader = Files.newBufferedReader(pConfigurationFilePath, StandardCharsets.UTF_8)) {
			pProperties.load(reader);
		}
	}
	
	public String getString(final String key) {
		return getProperty(key);
	}
	
	public String getString(final String key, final String defaultValue) {
		return getProperty(key, defaultValue);
	}
	
	public Integer getInt(final String key) throws ParseException {
		final String numberString = getProperty(key);
		if (numberString == null)
			return null;
		final Number number;
		try {
			number = NUMBER_FORMAT.parse(numberString);
		} catch (final ParseException e) {
			addConfigurationError("Not a valid number: " + numberString + " (for " + key + ')');
			throw e;
		}
		return Integer.valueOf(number.intValue());
	}
	
	public int getInt(final String key, final int defaultValue) throws ParseException {
		final String numberString = getProperty(key);
		if (numberString == null)
			return defaultValue;
		final Number number;
		try {
			number = NUMBER_FORMAT.parse(numberString);
		} catch (final ParseException e) {
			addConfigurationError("Not a valid number: " + numberString + " (for " + key + ')');
			throw e;
		}
		return number.intValue();
	}
	
	public Path getPathBase(final String key) {
		return pConfigurationFilePath.resolveSibling(pConfigurationFilePath.getFileSystem().getPath(getProperty(key, "")));
	}
	
	public Path getPath(final Path pathBase, final String key) {
		final String pathString = getProperty(key);
		if (pathString == null)
			return null;
		return pathBase.resolve(pathBase.getFileSystem().getPath(pathString));
	}
	
	private String getProperty(final String key) {
		pUsedKeys.add(key);
		return pProperties.getProperty(key);
	}
	
	private String getProperty(final String key, final String defaultValue) {
		pUsedKeys.add(key);
		return pProperties.getProperty(key, defaultValue);
	}
	
	public void addConfigurationError(final String message) {
		pConfigurationErrors.add(message);
	}
	
	public void addConfigurationWarning(final String message) {
		pConfigurationWarnings.add(message);
	}
	
	public boolean validate() {
		checkForUnusedKeys();
		if (pConfigurationErrors.isEmpty()) {
			if (pConfigurationWarnings.isEmpty())
				return true;
			printWarnings();
			return true;
		}
		printErrors();
		if (pConfigurationWarnings.isEmpty())
			return false;
		System.err.println();
		System.err.println();
		printWarnings();
		return false;
	}
	
	private void checkForUnusedKeys() {
		final ArrayList<String> unusedKeys = new ArrayList<>(pProperties.stringPropertyNames());
		unusedKeys.removeAll(pUsedKeys);
		if (unusedKeys.isEmpty())
			return;
		Collections.sort(unusedKeys, String.CASE_INSENSITIVE_ORDER);
		final String lineSeparator = System.lineSeparator();
		pConfigurationWarnings.add("The following configuration keys are unknown and have been ignored:" + lineSeparator + String.join(lineSeparator, unusedKeys));
	}
	
	private void printErrors() {
		System.err.println("The crawler has not been correctly configured:");
		for (final String error : pConfigurationErrors) {
			System.err.println();
			System.err.println(error);
		}
	}
	
	private void printWarnings() {
		System.err.println("WARNING:");
		for (final String warning : pConfigurationWarnings) {
			System.err.println();
			System.err.println(warning);
		}
	}
	
}
