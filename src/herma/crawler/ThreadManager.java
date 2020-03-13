/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.util.ArrayList;
import java.util.HashSet;

public class ThreadManager {
	
	private final HashSet<Thread> pThreads = new HashSet<>();
	
	public void addThread(final Thread thread) {
		synchronized (pThreads) {
			pThreads.add(thread);
		}
	}
	
	public void join() throws InterruptedException {
		final ArrayList<Thread> waitThreads = new ArrayList<>();
		while (true) {
			synchronized (pThreads) {
				for (final Thread thread : pThreads)
					if (thread.isAlive())
						waitThreads.add(thread);
			}
			if (waitThreads.isEmpty())
				break;
			for (final Thread thread : waitThreads)
				thread.join();
			waitThreads.clear();
		}
	}
	
}
