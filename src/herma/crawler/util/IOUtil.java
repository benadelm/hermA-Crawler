/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Contains helper methods for dealing with IO.
 */
public class IOUtil {
	
	/**
	 * Decodes an array of bytes ({@code byte[]})
	 * into a {@link String} based on a {@link Charset}.
	 * 
	 * @param bytes
	 * the array of bytes to be decoded
	 * 
	 * @param charset
	 * the {@link Charset} to be used for decoding
	 * 
	 * @return
	 * a {@link String} containing the decoded characters
	 */
	public static String decodeBytes(final byte[] bytes, final Charset charset) {
		return charset.decode(ByteBuffer.wrap(bytes)).toString();
	}
	
	/**
	 * Creates a directory like
	 * {@link Files#createDirectory(Path, java.nio.file.attribute.FileAttribute...)},
	 * but does not fail if the directory already exists.
	 * (The method does not fail either if a non-directory file
	 * with the given path already exists.)
	 * Unlike the similar method
	 * {@link Files#createDirectories(Path, java.nio.file.attribute.FileAttribute...)},
	 * this method does fail if the parent directory does not exist.
	 * 
	 * @param path
	 * (a {@link Path} locating) the directory to be created
	 * 
	 * @throws IOException
	 * if an I/O error occurs
	 */
	public static void createDirectoryIfNotExists(final Path path) throws IOException {
		// TODO: it would make more sense to generate warnings/errors if directories exist (non-resume mode) or not exist (resume mode)
		// in that case, this method would not be needed anymore
		try {
			Files.createDirectory(path);
		} catch (final FileAlreadyExistsException e) {
			// folder already exists, no problem
		}
	}
	
}
