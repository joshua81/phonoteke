package org.phonoteke.loader;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.google.api.client.util.Sets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

public class SocialNetworkLoader implements HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(SocialNetworkLoader.class);
	private static final String CREDENTIALS = "/Users/riccia/twitter.json";
	private static final int SCORE = 80;
	private static final int WEEKS = 1;

	private MongoCollection<org.bson.Document> docs = new MongoDB().getDocs();
	private MongoCollection<org.bson.Document> shows = new MongoDB().getShows();

	private static TwitterClient twitterClient;


	public static void main(String args[]) {
		new SocialNetworkLoader().load();
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
	public void load(String... args)
	{
		LOGGER.info("Tweetting podcasts...");
		LocalDateTime start = LocalDateTime.now().minusWeeks(WEEKS).withHour(0).withMinute(0).withSecond(0);
		MongoCursor<Document> i = docs.find(Filters.and(
				Filters.eq("type", "podcast"),
				Filters.gt("date", start), 
				Filters.eq("tweet", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(100).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			Integer score = page.getInteger("score");
			String spotify = page.getString("spalbumid");
			if(spotify != null && score >= SCORE) {
				try {
					notify(page);
				}
				catch(Exception e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
	}

	private void notify(Document page) {
		String id = page.getString("id");
		String podcast = page.getString("artist");
		String title = page.getString("title");
		String source = page.getString("source");
		String spotify = page.getString("spalbumid");
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		String date = new SimpleDateFormat("dd-MM-yyyy").format(page.getDate("date"));

		Set<String> artists = Sets.newHashSet();
		for(org.bson.Document track : tracks) {
			if(track.getInteger("score") >= SCORE) {
				artists.add(track.getString("artist"));
			}
		}

		org.bson.Document show = shows.find(Filters.and(Filters.eq("source", source))).iterator().next();
		List<String> twitter = show.get("twitter", List.class);

		String tweet = sendTweet(podcast, title, date, Lists.newArrayList(artists), twitter, spotify);
		page.append("tweet", tweet);
		docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
		LOGGER.info("Podcast " + id + " sent to Twitter");

		sendTelegram(podcast, title, date, Lists.newArrayList(artists), spotify);
		LOGGER.info("Podcast " + id + " sent to Telegram");
	}

	private String  sendTweet(String show, String title, String date, List<String> artists, List<String> twitter, String spotify) {
		artists = artists.size() <= TRACKS_SIZE ? artists : Lists.newArrayList(artists).subList(0, TRACKS_SIZE);
		String artistsStr = artists.toString().substring(1, artists.toString().length()-1);

		String twitterStr = "";
		if(CollectionUtils.isNotEmpty(twitter)) {
			for(String t : twitter) {
				twitterStr += t + " ";
			}
		}

		String msg = "The #spotify playlist of the new episode of " + show + " (" + date  + ")\n";
		msg += "with " + artistsStr.trim() +"\n";
		msg += StringUtils.isBlank(twitterStr) ? "" : (twitterStr.trim() + "\n");
		msg += "https://open.spotify.com/playlist/" + spotify;
		msg = StringEscapeUtils.escapeHtml(msg);

		Tweet tweet = twitterClient.postTweet(msg);
		Preconditions.checkNotNull(tweet.getId(), "Error while sending message to Twitter");
		return tweet.getId();
	}

	private void sendTelegram(String show, String title, String date, List<String> artists, String spotify) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;

		try {
			String artistsStr = artists.toString().substring(1, artists.toString().length()-1);
			String msg = "The spotify playlist of the new episode of *" + show + " - " + title + "* (" + date  + ")\n";
			msg += "with " + artistsStr.trim() +"\n";
			msg += "https://open.spotify.com/playlist/" + spotify;
			msg = StringEscapeUtils.escapeHtml(msg);

			String url = "https://api.telegram.org/bot" + System.getenv("TELEGRAM_KEY") + "/sendMessage?chat_id=@beatzhuman&parse_mode=markdown&text=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
			response = httpClient.execute(new HttpGet(url));
			int status = response.getStatusLine().getStatusCode();
			Preconditions.checkArgument(HttpStatus.SC_OK == status, "Error while sending message to Telegram");
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
