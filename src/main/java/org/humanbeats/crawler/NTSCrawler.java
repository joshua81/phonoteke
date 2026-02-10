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
import org.humanbeats.model.HBDocument;
import org.humanbeats.model.HBTrack;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

	public NTSCrawler() {
		this.type = NTS;
	}

	@Override
	protected void crawl(String url)
	{
		try
		{
			CloseableHttpClient client = HttpClients.createDefault();
			Integer offset = (page-1)*PAGE_SIZE;
			String episodesUrl = URL + URL_EPISODES.replace("$ARTIST", this.id)
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
					String episodeUrl = URL + URL_EPISODE.replace("$ARTIST", this.id)
					.replace("$EPISODE", doc.get("episode_alias").getAsString());
					HttpGet httpGet2 = new HttpGet(episodeUrl);

					CloseableHttpResponse response2 = client2.execute(httpGet2);
					JsonObject gson2 = new Gson().fromJson(new InputStreamReader(response2.getEntity().getContent()), JsonObject.class);
					doc = gson2.getAsJsonObject();

					String pageUrl = episodeUrl.replace("api/v2/", "");
					String id = getId(pageUrl);
					log.debug("Parsing page " + pageUrl);

					org.bson.Document json = repo.getDocs().find(Filters.and(Filters.eq("source", source), 
							Filters.eq("id", id))).iterator().tryNext();
					if(json == null)
					{
						HBDocument episode = crawlDocument(pageUrl, doc);
						insertDoc(episode);
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

	@Override
	protected String getBaseUrl() {
		return URL;
	}

	@Override
	public HBDocument crawlDocument(String url, Document doc) {
		throw new RuntimeException("Not implemented!!");
	}

	@Override
	public HBDocument crawlDocument(String url, JsonObject doc) {
		HBDocument episode = HBDocument.builder()
				.id(getId(url))
				.url(url)
				.source(source)
				.type(TYPE.podcast)
				.artist(artist)
				.authors(authors)
				.date(getDate(doc))
				.year(getYear(doc))
				.description(getDescription(doc))
				.title(getTitle(doc))
				.cover(getCover(doc))
				.audio(getAudio(doc))
				.tracks(getTracks(doc)).build();
		return episode;
	}

	private String getTitle(JsonObject doc) {
		return doc.get("name").getAsString();
	}

	private String getDescription(JsonObject doc) {
		return doc.get("description").getAsString();
	}

	private String getCover(JsonObject doc) {
		return doc.getAsJsonObject("media").get("picture_medium_large").getAsString();
	}

	private Date getDate(JsonObject doc) 
	{
		try {
			String date = doc.get("broadcast").getAsString();
			return new SimpleDateFormat("yyyy-MM-dd").parse(date.split("T")[0]);
		} catch (ParseException e) {
			return null;
		}
	}

	private Integer getYear(JsonObject doc) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate(doc));
		return cal.get(Calendar.YEAR);
	}

	private String getAudio(JsonObject doc) {
		return doc.getAsJsonArray("audio_sources").get(0).getAsJsonObject().get("url").getAsString();
	}

	private List<HBTrack> getTracks(JsonObject doc) {
		JsonArray content = doc.getAsJsonObject("embeds").getAsJsonObject("tracklist").getAsJsonArray("results");
		List<HBTrack> tracks = Lists.newArrayList();
		content.forEach(c -> {
			String title = c.getAsJsonObject().get("artist") + " - " + c.getAsJsonObject().get("title");
			tracks.add(HBTrack.builder().titleOrig(title).build());
			log.debug("tracks: " + title);
		});
		return tracks;
	}
}
