package org.phonoteke.loader;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

public class PodcastLoader extends AbstractCrawler
{
	protected static final Logger LOGGER = LogManager.getLogger(PodcastLoader.class);

	protected static String url;
	protected static String artist;
	protected static String source;
	protected static List<String> authors;

	@Override
	public void load(String... args) {
		new BBCRadioLoader().load(args);
		new RadioRaiLoader().load(args);
		new SpreakerLoader().load(args);
	}

	protected void updateLastEpisodeDate(String source) {
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", source))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(1).iterator();
		Date date = i.next().get("date", Date.class);
		i = HumanBeats.authors.find(Filters.eq("source", source)).limit(1).iterator();
		Document doc = i.next();
		doc.append("lastEpisodeDate", date);
		HumanBeats.authors.updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
	}
}
