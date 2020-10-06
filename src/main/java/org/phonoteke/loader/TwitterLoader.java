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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

public class TwitterLoader implements HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(TwitterLoader.class);

	private MongoCollection<org.bson.Document> docs = new MongoDB().getDocs();


	@Override
	public void load(String task)
	{
		LOGGER.info("Tweetting podcasts...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(100).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{
			String links = "";
			String tweet = "";
			Document page = i.next();
			String artist = page.getString("artist");
			String title = page.getString("title");
			String source = page.getString("source");
			Integer score = page.getInteger("score");
			String spotify = page.getString("spalbumid");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			
			if("casabertallot".equals(source)) {
				links = "@bertallot #casabertallot " + links;
			}
			else if("cassabertallot".equals(source)) {
				links = "@bertallot #cassabertallot " + links;
			}
			else if("rolloverhangover".equals(source)) {
				links = "@bertallot #rolloverhangover " + links;
			}
			else if("blackalot".equals(source)) {
				links = "@bertallot #blackalot " + links;
			}
			else if("resetrefresh".equals(source)) {
				links = "@bertallot #resetrefresh " + links;
			}
			else if("battiti".equals(source)) {
				links = "@radio3tweet #battitiradio3 " + links;
			}
			else if("seigradi".equals(source)) {
				links = "@radio3tweet #seigradiradio3 " + links;
			}
			else if("musicalbox".equals(source)) {
				links = "@rairadio2 @raffacostantino @_musicalbox #musicalboxradio2 " + links;
			}
			else if("stereonotte".equals(source)) {
				links = "@radio1rai @stereonotte #stereonotteradio1 " + links;
			}
			else if("inthemix".equals(source)) {
				links = "@rairadio2 @djlelesacchi #inthemixradio2 " + links;
			}
			else if("babylon".equals(source)) {
				links = "@rairadio2 @carlopastore #babylonradio2 " + links;
			}
			String date = new SimpleDateFormat("yyyy.MM.dd").format(page.getDate("date"));
			
			if(spotify != null && score >= 70 && CollectionUtils.isNotEmpty(tracks) && tracks.size() >= 5) {
				Set<String> artists = Sets.newHashSet();
				for(org.bson.Document track : tracks)
				{
					if(track.getInteger("score") >= 70) {
						artists.add(track.getString("artist"));
					}
				}

				tweet += "\n\n\nLa playlist #Spotify di " + artist + " (" + date  + ") - " + title + "\n";
				tweet += (artists.size() <= 5 ? artists : Lists.newArrayList(artists).subList(0, 5)) +"\n";
				tweet += links + "\n";
				tweet += "https://open.spotify.com/playlist/" + spotify;
				LOGGER.info(tweet);
			}
		}
	}
}
