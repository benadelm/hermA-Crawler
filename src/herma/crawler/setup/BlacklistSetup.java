/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

import herma.crawler.Blacklist;
import herma.crawler.config.Configuration;

public class BlacklistSetup {
	
	public static Blacklist setupBlacklist(final Configuration config, final Path pathBase) {
		final Path blacklistFile = config.getPath(pathBase, "blacklist");
		if (blacklistFile == null)
			return new Blacklist(Collections.emptyList());
		try (final Stream<String> lines = Files.lines(blacklistFile, StandardCharsets.UTF_8)) {
			return new Blacklist(lines::iterator);
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
}
