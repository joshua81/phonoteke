package org.humanbeats.crawler;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.humanbeats.util.HumanBeatsUtils.TYPE;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RadioRaheemCrawler extends AbstractCrawler
{
	private static final String RADIO_RAHEEM = "radioraheem";

	private static final String URL = "https://www.radioraheem.it/wp-json/wp/v2/episodes/?types=show:$ID&per_page=24&offset=0&orderby=date&lang=default";


	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", RADIO_RAHEEM))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", RADIO_RAHEEM), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			RadioRaheemCrawler.id = show.getString("id");
			RadioRaheemCrawler.artist = show.getString("title");
			RadioRaheemCrawler.source = show.getString("source");
			RadioRaheemCrawler.authors = show.get("authors", List.class);

			log.info("Crawling " + artist);
			crawl(url);
		}
	}

	@Override
	protected void crawl(String url)
	{
		try
		{
			HttpURLConnection con = (HttpURLConnection)new URL(URL.replace("$ID", RadioRaheemCrawler.id)).openConnection();
			JsonArray results = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonArray.class);
			results.forEach(item -> {
				JsonObject doc = (JsonObject)item;
				String pageUrl = doc.get("link").getAsString();
				TYPE type = TYPE.podcast;

				log.debug("Parsing page " + pageUrl);
				String id = getId(pageUrl);
				String title = doc.get("title").getAsJsonObject().get("rendered").getAsString();

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
								append("cover", doc.get("gds_featured_image").getAsJsonObject().get("url").getAsString()).
								append("date", getDate(doc.get("date").getAsString())).
								append("description", title).
								append("genres", null).
								append("label", null).
								append("links", null).
								append("review", null).
								append("source", source).
								append("vote", null).
								append("year", getYear(doc.get("date").getAsString())).
								append("tracks", getTracks(doc.get("acf").getAsJsonObject().get("tracklist").getAsJsonArray())).
								append("audio", getAudio(doc.get("acf").getAsJsonObject().get("mixcloud_iframe").getAsString()));
						insertDoc(json);
					}
					catch(Exception e) {
						log.debug("ERROR parsing page " + pageUrl + ": " + e.getMessage());
					}
				}
			});
		}
		catch (Throwable t) 
		{
			log.debug("ERROR parsing page " + url + ": " + t.getMessage());
		}
	}

	private Date getDate(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	private Integer getYear(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(date));
			return cal.get(Calendar.YEAR);
		} catch (ParseException e) {
			return null;
		}
	}

	private String getAudio(String doc) {
		String feed = doc.split("feed=")[1].split("\"")[0].replace("%2F", "/");
		return "https://www.mixcloud.com" + feed;
	}

	private List<org.bson.Document> getTracks(JsonArray content) {
		List<org.bson.Document> tracks = Lists.newArrayList();

		content.forEach(item -> {
			JsonObject doc = (JsonObject)item;
			String youtube = null;
			String title = doc.get("titolo").getAsString() + " - " + doc.get("sottotitolo").getAsString();
			tracks.add(newTrack(title, youtube));
			log.debug("tracks: " + title + ", youtube: " + youtube);
		});
		return checkTracks(tracks);
	}
}
