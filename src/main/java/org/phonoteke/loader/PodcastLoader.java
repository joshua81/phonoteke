package org.phonoteke.loader;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class PodcastLoader extends AbstractCrawler
{
	protected static final Logger LOGGER = LogManager.getLogger(PodcastLoader.class);

	protected static String url;
	protected static String title;
	protected static String artist;
	protected static String source;
	protected static List<String> authors;
	
	protected MongoCollection<org.bson.Document> shows = new MongoDB().getShows();

	public static void main(String[] args) {
		new PodcastLoader().load(BBCRadioLoader.GILLES_PETERSON);
	}

	public void initShows() {
//		initShow("spreaker", "https://api.spreaker.com/show/4380252/episodes", 
//				"Jazz Tracks", "jazztracks", Lists.newArrayList("Danilo Di Termini"), 
//				Lists.newArrayList("@daniloddt", "#jazztracks"));
	}

	private void initShow(String type, String url, String title, String source, List<String> authors, List<String> twitter) {
		Document show = new Document();
		show.append("type", type);
		show.append("url", url);
		show.append("title", title);
		show.append("source", source);
		show.append("authors", authors);
		show.append("twitter", twitter);
		shows.insertOne(show);
	}

	@Override
	public void load(String... args) {
		new BBCRadioLoader().load(args);
		new RadioRaiLoader().load(args);
		new SpreakerLoader().load(args);
	}
}
