/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.meta;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MemoryStringSet implements StringSet {
	
	private final Set<String> pSet;
	
	public MemoryStringSet() {
		pSet = Collections.synchronizedSet(new HashSet<>());
	}
	
	@Override
	public boolean add(final String str) {
		return pSet.add(str);
	}
	
}
