package org.humanbeats.crawler;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
public class SpreakerCrawler extends AbstractCrawler
{
	private static final String SPREAKER = "spreaker";
	private static final String URL = "https://www.spreaker.com/";

	public SpreakerCrawler() {
		SpreakerCrawler.type = SPREAKER;
	}

	@Override
	protected void crawl(String url)
	{
		try
		{
			HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
			JsonObject gson = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
			int pages = gson.get("response").getAsJsonObject().get("pager").getAsJsonObject().get("last_page").getAsInt();
			for(int page = 1; page <= pages; page++)
			{
				con = (HttpURLConnection)new URL(url + "?page=" + page).openConnection();
				gson = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
				JsonArray results = gson.get("response").getAsJsonObject().get("pager").getAsJsonObject().get("results").getAsJsonArray();
				results.forEach(item -> {
					JsonObject doc = (JsonObject)item;
					String pageUrl = doc.get("site_url").getAsString();
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
		}
		catch (Throwable t) 
		{
			log.debug("ERROR parsing page " + url + ": " + t.getMessage());
		}
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

	private String getCover(JsonObject doc) {
		return doc.get("image_original_url").getAsString();
	}

	private String getTitle(JsonObject doc) {
		return doc.get("title").getAsString();
	}

	private Date getDate(JsonObject doc) {
		try {
			String date = doc.get("published_at").getAsString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

	private List<HBTrack> getTracks(JsonObject doc) {
		List<HBTrack> tracks = Lists.newArrayList();

		String content = doc.get("description").getAsString();
		String[] chunks = content.split("\n");
		for(int i = 0; i < chunks.length; i++) {
			String title = chunks[i].trim();
			if(StringUtils.isNotBlank(title)) {
				tracks.add(HBTrack.builder().titleOrig(title).build());
				log.debug("tracks: " + title);
			}
		}
		return tracks;
	}

	private String getAudio(JsonObject doc) {
		return doc.get("download_url").getAsString();
	}
}
