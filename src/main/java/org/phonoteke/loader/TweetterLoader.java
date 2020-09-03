package org.phonoteke.loader;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

public class TweetterLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(TweetterLoader.class);

	public static void main(String[] args)
	{
		new TweetterLoader().tweet();
	}

	public TweetterLoader()
	{
		super();
	}

	private void tweet()
	{
		LOGGER.info("Tweetting podcasts...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(100).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{
			String links = "#spotifyplaylist";
			String tweet = "";
			Document page = i.next();
			String artist = page.getString("artist");
			String title = page.getString("title");
			String source = page.getString("source");
			Integer score = page.getInteger("score");
			if("casabertallot".equals(source)) {
				links = "@bertallot " + links;
			}
			else if("battiti".equals(source)) {
				links = "@Radio3tweet " + links;
			}
			else if("seigradi".equals(source)) {
				links = "@Radio3tweet " + links;
			}
			else if("musicalbox".equals(source)) {
				links = "@RaiRadio2 @raffacostantino @_musicalbox " + links;
			}
			else if("stereonotte".equals(source)) {
				links = "@Radio1Rai " + links;
			}
			String date = new SimpleDateFormat("yyyy.MM.dd").format(page.getDate("date"));
			String spotify = page.getString("spalbumid");
			if(spotify != null && score >= 70) {
				Set<String> artists = Sets.newHashSet();
				List<org.bson.Document> tracks = page.get("tracks", List.class);
				if(CollectionUtils.isNotEmpty(tracks))
				{
					for(org.bson.Document track : tracks)
					{
						if(score != null && track.getInteger("score") >= 70) {
							artists.add(track.getString("artist"));
						}
					}
				}

				tweet += artist + " (" + date  + ") - " + title + "\n";
				tweet += (artists.size() <= 5 ? artists : Lists.newArrayList(artists).subList(0, 5)) +"\n";
				tweet += links + "\n";
				tweet += "https://open.spotify.com/playlist/" + spotify;
				LOGGER.info(tweet);
			}
		}
	}
}
