package org.humanbeats.crawler;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.humanbeats.model.HBDocument;
import org.humanbeats.model.HBTrack;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.nodes.Document;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RadioRaheemCrawler extends AbstractCrawler
{
	private static final String RADIO_RAHEEM = "radioraheem";
	private static final String URL = "https://www.radioraheem.it/";
	private static final String URL_EPISODE = "https://www.radioraheem.it/wp-json/wp/v2/episodes/?types=show:$ID&per_page=24&offset=0&orderby=date&lang=default";

	public RadioRaheemCrawler() {
		this.type = RADIO_RAHEEM;
	}

	@Override
	protected void crawl(String url)
	{
		try
		{
			HttpURLConnection con = (HttpURLConnection)new URL(URL_EPISODE.replace("$ID", id)).openConnection();
			JsonArray results = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonArray.class);
			results.forEach(item -> {
				JsonObject doc = (JsonObject)item;
				String pageUrl = doc.get("link").getAsString();
				String id = getId(pageUrl);
				log.debug("Parsing page " + pageUrl);

				org.bson.Document json = repo.getDocs().find(Filters.eq("id", id)).iterator().tryNext();
				if(json == null)
				{
					HBDocument episode = crawlDocument(pageUrl, doc);
					insertDoc(episode);
				}
			});
		}
		catch (Throwable t) 
		{
			log.debug("ERROR parsing page " + url + ": " + t.getMessage());
		}
	}

	private String getTitle(JsonObject doc) {
		return doc.get("title").getAsJsonObject().get("rendered").getAsString();
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
				.title(getTitle(doc))
				// same as title
				.description(getTitle(doc))
				.cover(getCover(doc))
				.audio(getAudio(doc))
				.tracks(getTracks(doc)).build();
		return episode;
	}

	@Override
	public HBDocument crawlDocument(String url, Document doc) {
		throw new RuntimeException("Not implemented!!");
	}

	@Override
	protected String getBaseUrl() {
		return URL;
	}

	private Date getDate(JsonObject doc) {
		try {
			String date = doc.get("date").getAsString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	private Integer getYear(JsonObject doc) {
		Date date = getDate(doc);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	private String getAudio(JsonObject doc) {
		String iframe = doc.get("acf").getAsJsonObject().get("mixcloud_iframe").getAsString();
		String feed = iframe.split("feed=")[1].split("\"")[0].replace("%2F", "/");
		return "https://www.mixcloud.com" + feed;
	}

	private List<HBTrack> getTracks(JsonObject doc) {
		List<HBTrack> tracks = Lists.newArrayList();
		JsonArray content = doc.get("acf").getAsJsonObject().get("tracklist").getAsJsonArray();
		content.forEach(item -> {
			JsonObject track = (JsonObject)item;
			String title = track.get("titolo").getAsString() + " - " + track.get("sottotitolo").getAsString();
			tracks.add(HBTrack.builder().titleOrig(title).build());
			log.debug("tracks: " + title);
		});
		return tracks;
	}

	private String getCover(JsonObject doc) {
		return doc.get("gds_featured_image").getAsJsonObject().get("url").getAsString();
	}
}
