package org.phonoteke.loader;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PodcastLoader extends AbstractCrawler
{
	protected static final Logger LOGGER = LogManager.getLogger(PodcastLoader.class);

	protected static String url;
	protected static String id;
	protected static String artist;
	protected static String source;
	protected static List<String> authors;

	@Override
	public void load(String... args) {
		new BBCRadioLoader().load(args);
		new RadioRaiLoader().load(args);
		new SpreakerLoader().load(args);
		new WorldWideFMLoader().load(args);
	}
}
