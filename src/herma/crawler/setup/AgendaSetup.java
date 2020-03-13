/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.client.utils.URIBuilder;

import herma.crawler.agenda.AgendaIn;
import herma.crawler.config.Configuration;
import herma.crawler.contenthandling.util.UrlUtil;
import herma.crawler.links.LinkProcessor;

public class AgendaSetup {
	
	private static final String SEEDS_FILE_KEY = "seeds";
	
	public static void initiallyFillAgenda(final AgendaIn agenda, final Configuration config, final Path pathBase, final LinkProcessor linkProcessor) {
		final Path seedUrlsFile = config.getPath(pathBase, SEEDS_FILE_KEY);
		if (seedUrlsFile == null)
			config.addConfigurationError("You have to specify a file with seed URLs (key \"" + SEEDS_FILE_KEY + "\").");
		else
			initiallyFillAgenda(agenda, seedUrlsFile, linkProcessor);
	}
	
	public static void initiallyFillAgenda(final AgendaIn agenda, final Path seedUrlsFile, final LinkProcessor linkProcessor) {
		try (final BufferedReader reader = Files.newBufferedReader(seedUrlsFile, StandardCharsets.UTF_8)) {
			while (true) {
				final String line = reader.readLine();
				if (line == null)
					break;
				initiallyAddToAgenda(agenda, line, linkProcessor);
			}
		} catch (final IOException e) {
			throw new UncheckedIOException(e);
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void initiallyAddToAgenda(final AgendaIn agenda, final String urlString, final LinkProcessor linkProcessor) throws URISyntaxException {
		final URIBuilder uriBuilder = linkProcessor.processUrl(urlString, StandardCharsets.UTF_8);
		initiallyAddToAgenda(agenda, uriBuilder);
	}
	
	public static void initiallyAddToAgenda(final AgendaIn agenda, final URIBuilder url) {
		agenda.add(url.toString(), UrlUtil.extractHost(url), 0L);
	}
	
}
