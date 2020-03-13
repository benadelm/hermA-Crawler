/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

public class DummyFurtherProcessing implements FurtherProcessing {
	
	@Override
	public void startFurtherProcessing(final OriginalInfo originalInfo, final Iterable<? extends FurtherProcessingItem> items) {
		try {
			System.out.println("Speichere " + originalInfo.getMetadata().getDownloadUrl());
		} finally {
			for (final FurtherProcessingItem item : items) {
				try {
					item.getPreParsingResult().dispose();
				} catch (final Exception e) {
					// ignore
				}
			}
		}
	}
	
}
