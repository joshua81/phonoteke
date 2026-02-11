package org.humanbeats.crawler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.humanbeats.model.HBDocument;
import org.humanbeats.model.HBTrack;
import org.humanbeats.util.HumanBeatsUtils;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RadioRaiCrawler extends AbstractCrawler
{
	private static final String URL = "https://www.raiplaysound.it/";
	private static final String URL2 = "https://www.raiplaysound.it";
	private static final String RAI = "rai";
	private static final String MUSICALBOX = "musicalbox";


	public RadioRaiCrawler() {
		this.type = RAI;
	}

	@Override
	protected void crawl(String url)
	{
		try
		{
			HttpURLConnection con = (HttpURLConnection)new URL(url + ".json").openConnection();
			JsonObject episodes = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
			episodes.get("block").getAsJsonObject().get("cards").getAsJsonArray().forEach(c -> {
				String pageUrl = URL2 + ((JsonObject)c).get("weblink").getAsString();
				try {
					log.debug("Parsing page " + pageUrl);
					String id = getId(pageUrl);

					org.bson.Document json = repo.getDocs().find(Filters.eq("id", id)).iterator().tryNext();
					if(json == null)
					{
						HttpURLConnection con2 = (HttpURLConnection)new URL(pageUrl.replace(".html", ".json")).openConnection();
						JsonObject doc = new Gson().fromJson(new InputStreamReader(con2.getInputStream()), JsonObject.class);
						HBDocument episode = crawlDocument(pageUrl, doc);
						insertDoc(episode);
					}
				}
				catch(Exception e) {
					log.error("ERROR parsing page " + pageUrl + ": " + e.getMessage());
				}
			});
		}
		catch (Throwable t) {
			log.error("ERROR parsing page " + url + ": " + t.getMessage());
			throw new RuntimeException("ERROR parsing page " + url + ": " + t.getMessage());
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
				.description(getDescription(doc))
				.title(getTitle(doc))
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
		return URL2 + doc.get("image").getAsString();
	}

	private String getDescription(JsonObject doc) {
		return doc.get("description").getAsString();
	}

	private String getTitle(JsonObject doc) {
		return doc.get("episode_title").getAsString();
	}

	private Date getDate(JsonObject doc) 
	{
		try {
			String date = doc.get("literal_publication_date").getAsString();
			return new SimpleDateFormat("dd MMM yyyy", Locale.ITALY).parse(date);
		} 
		catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private Integer getYear(JsonObject doc)
	{
		Calendar year = Calendar.getInstance();
		year.setTime(getDate(doc));
		return year.get(Calendar.YEAR);
	}

	private List<HBTrack> getTracks(JsonObject doc) {
		List<HBTrack> tracks = Lists.newArrayList();
		if(MUSICALBOX.equals(source)) {
			return tracks;
		}

		String content = doc.get("description").getAsString();
		if(content != null) {
			String[] chunks = content.replace("//", HumanBeatsUtils.TRACKS_NEW_LINE).split(HumanBeatsUtils.TRACKS_NEW_LINE);
			for(int i = 0; i < chunks.length; i++) {
				String title = chunks[i].trim();
				if(StringUtils.isNotBlank(title)) {
					tracks.add(HBTrack.builder().titleOrig(title).build());
					log.debug("tracks: " + title);
				}
			}
		}
		return tracks;
	}

	private String getAudio(JsonObject doc) {
		try {
			String url = doc.get("audio").getAsJsonObject().get("url").getAsString();
			Document audio = Jsoup.connect(url).ignoreContentType(true).get();
			return audio.location();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
