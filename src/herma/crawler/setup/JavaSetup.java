/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import herma.crawler.config.Configuration;

public class JavaSetup {
	
	private static final String JAVA_COMMAND_KEY = "java";
	private static final String DEFAULT_JAVA_COMMAND = "java";
	
	public static String loadJavaCommand(final Configuration config) {
		return config.getString(JAVA_COMMAND_KEY, DEFAULT_JAVA_COMMAND);
	}
	
}
