/* This Source Code Form is subject to the terms of the hermACrawler
 * Licence. If a copy of the licence was not distributed with this
 * file, You have received this Source Code Form in a manner that does
 * not comply with the terms of the licence.
 */
 package herma.crawler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;

import herma.crawler.db.MongoDbContext;
import herma.crawler.meta.AutoSavingStringSet;
import herma.crawler.meta.MetaInformationStringCollection;
import herma.crawler.meta.MongoDbStringSet;
import herma.crawler.meta.StringSaveQueue;
import herma.crawler.util.IOUtil;
import herma.crawler.util.PathUtil;

public class MetaCollector {
	
	private final MongoDbContext pDb;
	private final Path pMetaDirectory;
	
	private final ArrayList<Path> pDirectories;
	private final HashSet<String> pNames;
	private final ArrayList<AutoSavingStringSet> pAutoSavingStringSets;
	private final ArrayList<StringSaveQueue> pStringSaveQueues;
	
	public MetaCollector(final MongoDbContext db, final Path metaInformationDirectory) {
		pDb = db;
		pMetaDirectory = metaInformationDirectory;
		
		pDirectories = new ArrayList<>();
		pNames = new HashSet<>();
		pAutoSavingStringSets = new ArrayList<>();
		pStringSaveQueues = new ArrayList<>();
	}
	
	public MetaInformationStringCollection createMetaInformationStringCollection(final String name, final boolean unique) {
		if (pDb == null)
			return null;
		
		registerName(name);
		
		final Path directory = PathUtil.combine(pMetaDirectory, name);
		pDirectories.add(directory);
		
		if (unique)
			return createAutoSavingStringSet(name, directory);
		else
			return createStringSaveQueue(name, directory);
	}
	
	private StringSaveQueue createStringSaveQueue(final String name, final Path directory) {
		final StringSaveQueue result = new StringSaveQueue(PathUtil.combine(directory, name + ".txt"));
		pStringSaveQueues.add(result);
		return result;
	}
	
	private AutoSavingStringSet createAutoSavingStringSet(final String name, final Path directory) {
		final AutoSavingStringSet result = new AutoSavingStringSet(new MongoDbStringSet(pDb, name), PathUtil.combine(directory, name + ".txt"));
		pAutoSavingStringSets.add(result);
		return result;
	}
	
	private void registerName(final String name) {
		if (pNames.add(name))
			return;
		throw new IllegalStateException("meta-information collection name \"" + name + "\" already registered");
	}
	
	public void createDirectories() throws IOException {
		IOUtil.createDirectoryIfNotExists(pMetaDirectory);
		for (final Path directory : pDirectories)
			IOUtil.createDirectoryIfNotExists(directory);
	}
	
	public void stop() {
		for (final AutoSavingStringSet autoSavingStringSet : pAutoSavingStringSets)
			autoSavingStringSet.stop();
		for (final StringSaveQueue stringSaveQueue : pStringSaveQueues)
			stringSaveQueue.stop();
	}
	
}
