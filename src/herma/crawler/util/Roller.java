/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class Roller {
	
	private static final DateTimeFormatter TIMESTAMP_FORMATTER;
	
	static {
		final DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
		dateTimeFormatterBuilder.appendValue(ChronoField.YEAR, 4);
		dateTimeFormatterBuilder.appendValue(ChronoField.MONTH_OF_YEAR, 2);
		dateTimeFormatterBuilder.appendValue(ChronoField.DAY_OF_MONTH, 2);
		dateTimeFormatterBuilder.appendValue(ChronoField.HOUR_OF_DAY, 2);
		dateTimeFormatterBuilder.appendValue(ChronoField.MINUTE_OF_HOUR, 2);
		dateTimeFormatterBuilder.appendValue(ChronoField.SECOND_OF_MINUTE, 2);
		TIMESTAMP_FORMATTER = dateTimeFormatterBuilder.toFormatter(Locale.ROOT).withZone(ZoneOffset.UTC);
	}
	
	// method is not completely safe;
	// if the target file is created by someone else concurrently
	// between the call to exists and the call to move,
	// the file created by someone else is overwritten
	// (or there is an exception)
	public static void roll(final Path file) throws IOException {
		if (Files.notExists(file, LinkOption.NOFOLLOW_LINKS))
			return;
		final String timestamp = TIMESTAMP_FORMATTER.format(Instant.now());
		final Path directory = file.getParent();
		final String baseFilename = file.getFileName().toString() + '-' + timestamp;
		String filename = baseFilename;
		long trial = 0L;
		while (true) {
			final Path p = directory.resolve(filename);
			if (Files.exists(p, LinkOption.NOFOLLOW_LINKS)) {
				trial++;
				filename = baseFilename + '.' + Long.toString(trial);
				continue;
			}
			try {
				Files.move(file, p, StandardCopyOption.ATOMIC_MOVE);
			} catch (final IOException e) {
				Files.move(file, p);
			}
			return;
		}
	}
	
}
