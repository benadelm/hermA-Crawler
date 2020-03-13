/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.contenthandlers.pdf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import herma.crawler.Metadata;
import herma.crawler.OriginalInfo;


class PdfOriginalInfo implements OriginalInfo {
	
	private final Metadata pMetadata;
	private final Path pTempFile;
	
	public PdfOriginalInfo(final Metadata metadata, final Path tempFile) {
		pMetadata = metadata;
		pTempFile = tempFile;
	}
	
	@Override
	public Metadata getMetadata() {
		return pMetadata;
	}
	
	@Override
	public String getFileNameExtension() {
		return ".pdf";
	}
	
	@Override
	public void saveTo(final Path file) throws IOException {
		// file could already exist, hence REPLACE_EXISTING.
		// The current implementation calls the method is ALWAYS with an already existing (but empty) file.
		try {
			Files.move(pTempFile, file, StandardCopyOption.ATOMIC_MOVE);
		} catch (final IOException e) {
			Files.move(pTempFile, file, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
}
