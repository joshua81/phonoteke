package org.phonoteke.loader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;

public class SpeakerLoader extends PhonotekeLoader
{
	private static final String URL1 = "https://api.spreaker.com/show/896299/episodes";
	private static final String URL2 = "https://api.spreaker.com/show/1977676/episodes";
	private static final String URL3 = "https://api.spreaker.com/show/2071330/episodes";
	private static final String URL4 = "https://api.spreaker.com/show/1501820/episodes";
	private static final String URL5 = "https://api.spreaker.com/show/2013495/episodes";


	public SpeakerLoader() {
		super();
	}

	protected void load(String task) 
	{
		if(task == null) {
			load("casabertallot");
			load("rolloverhangover");
			load("blackalot");
			load("cassabertallot");
			load("refreshrefresh");	
		}

		if("casabertallot".equals(task)) {
			crawl(URL1, "casabertallot", "Casa Bertallot");
		}
		else if("casabertallot".equals(task)) {
			crawl(URL2, "rolloverhangover", "Rollover Hangover");
		}
		else if("casabertallot".equals(task)) {
			crawl(URL3, "blackalot", "Black A Lot");
		}
		else if("casabertallot".equals(task)) {
			crawl(URL4, "cassabertallot", "Cassa Bertallot");
		}
		else if("casabertallot".equals(task)) {
			crawl(URL5, "refreshrefresh", "Refresh Refresh");
		}
	}

	protected void crawl(String baseurl, String source, String artist)
	{
		try
		{
			HttpURLConnection con = (HttpURLConnection)new URL(baseurl).openConnection();
			JsonObject gson = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
			int pages = gson.get("response").getAsJsonObject().get("pager").getAsJsonObject().get("last_page").getAsInt();
			for(int page = 1; page <= pages; page++)
			{
				con = (HttpURLConnection)new URL(baseurl + "?page=" + page).openConnection();
				gson = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
				JsonArray results = gson.get("response").getAsJsonObject().get("pager").getAsJsonObject().get("results").getAsJsonArray();
				results.forEach(item -> {
					JsonObject doc = (JsonObject)item;
					String url = doc.get("site_url").getAsString();
					TYPE type = TYPE.podcast;

					LOGGER.debug("Parsing page " + url);
					String id = getId(url);
					String title = doc.get("title").getAsString();

					org.bson.Document json = docs.find(Filters.and(Filters.eq("source", source), 
							Filters.eq("url", url))).iterator().tryNext();
					if(json == null)
					{
						try {
							json = new org.bson.Document("id", id).
									append("url", getUrl(url)).
									append("type", type.name()).
									append("artist", artist).
									append("title", title).
									append("authors", Lists.newArrayList("Alessio Bertallot")).
									append("cover", doc.get("image_original_url").getAsString()).
									append("date", getDate(doc.get("published_at").getAsString())).
									append("description", title).
									append("genres", null).
									append("label", null).
									append("links", null).
									append("review", null).
									append("source", source).
									append("vote", null).
									append("year", getYear(doc.get("published_at").getAsString())).
									append("tracks", getTracks(doc.get("description").getAsString())).
									append("audio", doc.get("download_url").getAsString());

							docs.insertOne(json);
							LOGGER.info(json.getString("type") + " " + url + " added");
						}
						catch(Exception e) {
							LOGGER.error("ERROR parsing page " + url + ": " + e.getMessage());
						}
					}
				});
			}
		}
		catch (Throwable t) 
		{
			LOGGER.error("ERROR parsing page " + baseurl + ": " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	private Date getDate(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	private Integer getYear(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(date));
			return cal.get(Calendar.YEAR);
		} catch (ParseException e) {
			return null;
		}
	}

	private List<org.bson.Document> getTracks(String content) {
		List<org.bson.Document> tracks = Lists.newArrayList();

		String[] chunks = content.split("\n");
		for(int i = 0; i < chunks.length; i++)
		{
			String title = chunks[i].trim();
			if(StringUtils.isNotBlank(title) && isTrack(title))
			{
				String youtube = null;
				tracks.add(newTrack(title, youtube));
				LOGGER.debug("tracks: " + title + ", youtube: " + youtube);
			}
		}
		if(CollectionUtils.isEmpty(tracks))
		{
			throw new IllegalArgumentException("Empty tracks!");
		}
		return tracks;
	}
}
