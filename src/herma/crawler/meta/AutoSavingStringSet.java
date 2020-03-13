/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler.meta;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoSavingStringSet implements MetaInformationStringCollection {
	
	// TODO: calling add and stop concurrently may result in strings being lost
	// However, at the moment, the two methods are never called concurrently.
	
	private final StringSet pSet;
	private final StringSaveQueue pSaveQueue;
	private final AtomicBoolean pRunning;
	
	public AutoSavingStringSet(final StringSet set, final Path file) {
		pSet = set;
		pRunning = new AtomicBoolean(true);
		pSaveQueue = new StringSaveQueue(file);
	}
	
	@Override
	public boolean add(final String str) {
		if (pRunning.get()) {
			if (pSet.add(str)) {
				pSaveQueue.add(str);
				return true;
			}
			return false;
		}
		throw new IllegalStateException();
	}
	
	public void stop() {
		pRunning.set(false);
		pSaveQueue.stop();
	}
	
}
