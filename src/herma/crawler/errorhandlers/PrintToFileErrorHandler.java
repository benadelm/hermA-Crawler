/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.errorhandlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import herma.crawler.ErrorHandler;
import herma.crawler.LinkType;


public class PrintToFileErrorHandler implements ErrorHandler {
	
	private final Path pFile;
	private final Object pLock;
	
	public PrintToFileErrorHandler(final Path file) {
		pFile = file;
		pLock = new Object();
	}
	
	@Override
	public void handleError(final String url, final String message) {
		synchronized (pLock) {
			try (final BufferedWriter writer = Files.newBufferedWriter(pFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
				writer.append(url).append('\n');
				writer.append(message).append("\n\n");
				writer.flush();
			} catch (final IOException e) {
			}
		}
	}
	
	@Override
	public void handleError(final String url, final Exception exception) {
		final String exceptionType = exception.getClass().getTypeName();
		final String exceptionMessage = exception.getLocalizedMessage();
		synchronized (pLock) {
			try (final BufferedWriter writer = Files.newBufferedWriter(pFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
				writer.append(url).append('\n');
				writer.append(exceptionType).append(": ").append(exceptionMessage).append("\n\n");
				writer.flush();
			} catch (final IOException e) {
			}
		}
	}
	
	@Override
	public void handleError(final String referringUrl, final String referredUrl, final LinkType linkType, final Exception exception) {
		final String exceptionType = exception.getClass().getTypeName();
		final String exceptionMessage = exception.getLocalizedMessage();
		final String linkTypeString = linkTypeString(linkType);
		synchronized (pLock) {
			try (final BufferedWriter writer = Files.newBufferedWriter(pFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
				writer.append(referredUrl).append('\n');
				writer.append('(').append(linkTypeString).append(" from ").append(referringUrl).append(")\n");
				writer.append(exceptionType).append(": ").append(exceptionMessage).append("\n\n");
				writer.flush();
			} catch (final IOException e) {
			}
		}
	}

	private String linkTypeString(final LinkType linkType) {
		switch (linkType) {
			case LINK:
				return "link";
			case REDIRECT:
				return "redirection";
			default:
				throw new IllegalArgumentException();
		}
	}
	
}
