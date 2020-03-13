/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.util;

import java.nio.file.Path;

/**
 * Contains helper methods for dealing with paths.
 *
 */
public class PathUtil {
	
	/**
	 * Resolves a {@link String} against a {@link Path},
	 * just like {@link Path#resolve(String)}.
	 * However, this method returns {@code null}
	 * if the path is {@code null}
	 * (in contrast to invoking {@link Path#resolve(String)}
	 * on that path, which would result in a
	 * {@link NullPointerException}).
	 * 
	 * @param path1
	 * the {@link Path} against which to resolve the {@link String};
	 * may be {@code null}
	 * 
	 * @param path2
	 * the {@link String} to resolve against the {@link Path};
	 * see {@link Path#resolve(String)} for the restrictions
	 * that apply to this parameter
	 * 
	 * @return
	 * the result of {@link Path#resolve(String)} for the
	 * {@link Path} and the {@link String},
	 * or {@code null} if the {@link Path} is {@code null}
	 */
	public static Path combine(final Path path1, final String path2) {
		if (path1 == null)
			return null;
		return path1.resolve(path2);
	}
	
}
