package org.phonoteke.loader;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Sets;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

public class SocialNetworkLoader implements HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(SocialNetworkLoader.class);
	private static final String CREDENTIALS = "/Users/riccia/twitter.json";

	private MongoCollection<org.bson.Document> docs = new MongoDB().getDocs();

	private static TwitterClient twitterClient;


	public static void main(String args[]) {
		new SocialNetworkLoader().load(null);
	}


	public SocialNetworkLoader() {
		try {
			twitterClient = new TwitterClient(TwitterClient.OBJECT_MAPPER.readValue(new File(CREDENTIALS), TwitterCredentials.class));
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Override
	public void load(String task)
	{
		LOGGER.info("Tweetting podcasts...");
		Date start = new GregorianCalendar(2020,9,11).getTime();
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.gt("date", start), Filters.eq("tweet", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(100).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			Integer score = page.getInteger("score");
			String spotify = page.getString("spalbumid");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(spotify != null && score >= 70 && CollectionUtils.isNotEmpty(tracks) && tracks.size() >= 5) {
				sendTweet(page);
				sendTelegram(page);
			}
		}
	}

	private void sendTweet(Document page) {
		String id = page.getString("id");
		String artist = page.getString("artist");
		String source = page.getString("source");
		String spotify = page.getString("spalbumid");
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		String date = new SimpleDateFormat("dd-MM-yyyy").format(page.getDate("date"));

		Set<String> artists = Sets.newHashSet();
		for(org.bson.Document track : tracks) {
			if(track.getInteger("score") >= 70) {
				artists.add(track.getString("artist"));
			}
		}

		String links = "";
		if("casabertallot".equals(source)) {
			links = "@bertallot #casabertallot " + links;
		}
		else if("cassabertallot".equals(source)) {
			links = "@bertallot @albiscotti #cassabertallot " + links;
		}
		else if("rolloverhangover".equals(source)) {
			links = "@bertallot #rolloverhangover " + links;
		}
		else if("blackalot".equals(source)) {
			links = "@bertallot #blackalot " + links;
		}
		else if("resetrefresh".equals(source)) {
			links = "@bertallot @flikkarina #resetrefresh " + links;
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
		else if("jazztracks".equals(source)) {
			links = "@daniloddt #jazztracks " + links;
		}
		else if("thetuesdaytapes".equals(source)) {
			links = "@bertallot @thetuesdaytapes #thetuesdaytapes " + links;
		}

		String msg = "La playlist #Spotify di " + artist + " (" + date  + ")\n";
		msg += (artists.size() <= 5 ? artists : Lists.newArrayList(artists).subList(0, 5)) +"\n";
		msg += links + "\n";
		msg += "https://open.spotify.com/playlist/" + spotify;

		Tweet tweet = twitterClient.postTweet(msg);
		page.append("tweet", tweet.getId());
		docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
		LOGGER.info("Podcast " + id + " sent to Twitter");
	}

	private void sendTelegram(Document page) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;

		try {
			String id = page.getString("id");
			String artist = page.getString("artist");
			String title = page.getString("title");
			String spotify = page.getString("spalbumid");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			String date = new SimpleDateFormat("dd-MM-yyyy").format(page.getDate("date"));

			Set<String> artists = Sets.newHashSet();
			for(org.bson.Document track : tracks) {
				if(track.getInteger("score") >= 70) {
					artists.add(track.getString("artist"));
				}
			}

			String msg = "La playlist Spotify di *" + artist + "* (" + date  + ") - *" + title + "*\n";
			msg += "con: " + artists +"\n";
			msg += "https://open.spotify.com/playlist/" + spotify;

			String url = "https://api.telegram.org/bot" + System.getenv("TELEGRAM_KEY") + "/sendMessage?chat_id=@beatzhuman&parse_mode=markdown&text=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
			response = httpClient.execute(new HttpGet(url));
			int status = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == status) {
				LOGGER.info("Podcast " + id + " sent to Telegram");
			}
			else {
				LOGGER.error("Error while sending message to Telegram channel @beatzhuman. HTTP status " + status);
			}
		}
		catch(Exception e) {
			LOGGER.error(e.getMessage(),  e);
		}
		finally {
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					// do nothing
				}
			}
			if(httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}
}
