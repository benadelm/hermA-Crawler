/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import herma.crawler.config.Configuration;

public class PythonSetup {
	
	private static final String PYTHON_COMMAND_KEY = "python3";
	private static final String DEFAULT_PYTHON_COMMAND = "python3";
	
	public static String loadPythonCommand(final Configuration config) {
		return config.getString(PYTHON_COMMAND_KEY, DEFAULT_PYTHON_COMMAND);
	}
	
}
