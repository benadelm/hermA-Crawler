/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import herma.crawler.Metadata;
import herma.crawler.OriginalInfo;

public class BytesOriginalInfo implements OriginalInfo {
	
	private final Metadata pMetadata;
	private final String pExtension;
	private final byte[] pBytes;
	
	
	public BytesOriginalInfo(final Metadata metadata, final String extension, final byte[] bytes) {
		pMetadata = metadata;
		pExtension = extension;
		pBytes = bytes;
	}
	
	@Override
	public Metadata getMetadata() {
		return pMetadata;
	}
	
	@Override
	public String getFileNameExtension() {
		return pExtension;
	}
	
	@Override
	public void saveTo(final Path file) throws IOException {
		// file could already exist, hence REPLACE_EXISTING.
		// The current implementation calls the method is ALWAYS with an already existing (but empty) file.
		Files.write(file, pBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
	}
	
}
