package org.humanbeats.crawler;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NTSCrawler extends AbstractCrawler
{
	private static final Integer PAGE_SIZE = 20;
	private static final String NTS = "nts";
	private static final String URL = "https://www.nts.live/";
	private static final String URL_EPISODES = "api/v2/shows/$ARTIST/episodes?offset=$OFFSET&limit=$LIMIT";
	private static final String URL_EPISODE = "api/v2/shows/$ARTIST/episodes/$EPISODE";


	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", NTS))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", NTS), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			NTSCrawler.id = show.getString("id");
			NTSCrawler.artist = show.getString("title");
			NTSCrawler.source = show.getString("source");
			NTSCrawler.authors = show.get("authors", List.class);
			NTSCrawler.page = args.length == 2 ? Integer.parseInt(args[1]) : 1;

			log.info("Crawling " + artist + " (" + page + " page)");
			crawl(URL);
		}
	}

	@Override
	protected void crawl(String url)
	{
		try
		{
			CloseableHttpClient client = HttpClients.createDefault();
			Integer offset = (page-1)*PAGE_SIZE;
			String episodesUrl = url + URL_EPISODES.replace("$ARTIST", NTSCrawler.id)
			.replace("$OFFSET", offset.toString())
			.replace("$LIMIT", PAGE_SIZE.toString());
			HttpGet httpGet = new HttpGet(episodesUrl);

			CloseableHttpResponse response = client.execute(httpGet);
			JsonObject gson = new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);
			JsonArray results = gson.get("results").getAsJsonArray();

			results.forEach(item -> {
				try
				{
					JsonObject doc = (JsonObject)item;
					CloseableHttpClient client2 = HttpClients.createDefault();
					String episodeUrl = url + URL_EPISODE.replace("$ARTIST", NTSCrawler.id)
					.replace("$EPISODE", doc.get("episode_alias").getAsString());
					HttpGet httpGet2 = new HttpGet(episodeUrl);

					CloseableHttpResponse response2 = client2.execute(httpGet2);
					JsonObject gson2 = new Gson().fromJson(new InputStreamReader(response2.getEntity().getContent()), JsonObject.class);
					doc = gson2.getAsJsonObject();

					String pageUrl = episodeUrl.replace("api/v2/", "");
					TYPE type = TYPE.podcast;

					log.debug("Parsing page " + pageUrl);
					String id = getId(pageUrl);
					String title = doc.get("name").getAsString();

					org.bson.Document json = repo.getDocs().find(Filters.and(Filters.eq("source", source), 
							Filters.eq("url", pageUrl))).iterator().tryNext();
					if(json == null)
					{
						try {
							json = new org.bson.Document("id", id).
									append("url", getUrl(pageUrl)).
									append("type", type.name()).
									append("artist", artist).
									append("title", title).
									append("authors", authors).
									append("cover", doc.getAsJsonObject("media").get("picture_medium_large").getAsString()).
									append("date", getDate(doc.get("broadcast").getAsString())).
									append("description", doc.get("description").getAsString()).
									append("genres", null).
									append("label", null).
									append("links", null).
									append("review", null).
									append("source", source).
									append("vote", null).
									append("year", getYear(doc.get("broadcast").getAsString())).
									append("tracks", getTracks(doc.getAsJsonObject("embeds").getAsJsonObject("tracklist").getAsJsonArray("results"))).
									append("audio", getAudio(doc));
							insertDoc(json);
						}
						catch(Exception e) {
							log.debug("ERROR parsing page " + pageUrl + ": " + e.getMessage());
						}
					}
				}
				catch (Throwable t) 
				{
					log.debug("ERROR parsing page " + id + ": " + t.getMessage());
				}
			});
			client.close();
		}
		catch (Throwable t) 
		{
			log.debug("ERROR parsing page " + id + ": " + t.getMessage());
		}
	}

	private Date getDate(String date) 
	{
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(date.split("T")[0]);
		} catch (ParseException e) {
			return null;
		}
	}

	private Integer getYear(String date) 
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate(date));
		return cal.get(Calendar.YEAR);
	}

	private String getAudio(JsonObject doc) {
		return doc.getAsJsonArray("audio_sources").get(0).getAsJsonObject().get("url").getAsString();
	}

	private List<org.bson.Document> getTracks(JsonArray content) {
		List<org.bson.Document> tracks = Lists.newArrayList();
		content.forEach(c -> {
			String title = c.getAsJsonObject().get("artist") + " - " + c.getAsJsonObject().get("title");
			tracks.add(newTrack(title, null));
			log.debug("tracks: " + title);
		});
		return checkTracks(tracks);
	}
}
