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

public class TwitterLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(TwitterLoader.class);

	public static void main(String[] args)
	{
		new TwitterLoader().tweet();
	}

	public TwitterLoader()
	{
		super();
	}

	private void tweet()
	{
		LOGGER.info("Tweetting podcasts...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(100).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{
//			#BattitiRadio3
//			#cassabertallot
//			#casabertallot
//			#rolloverhangover
//			#blackalot
//			refreshrefresh
//			#seigradiradio3 @DeaPaola
//			#musicalboxradio2
//			@Stereonotte @MaxDeTomassi @djlelesacchi
//			inthemix
//			babylon
			
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
				links = "@radio3tweet #battitiradio3 " + links;
			}
			else if("seigradi".equals(source)) {
				links = "@radio3tweet @deapaola #seigradiradio3 " + links;
			}
			else if("musicalbox".equals(source)) {
				links = "@rairadio2 @raffacostantino @_musicalbox #musicalboxradio2 " + links;
			}
			else if("stereonotte".equals(source)) {
				links = "@radio1rai @stereonotte @maxdetomassi @djlelesacchi #stereonotteradio1 " + links;
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
