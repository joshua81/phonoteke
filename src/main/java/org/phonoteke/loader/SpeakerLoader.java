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
	private static final String URL = "https://api.spreaker.com/show/896299/episodes";

	public static void main(String[] args) 
	{
		new SpeakerLoader().crawl(URL);
	}

	protected void crawl(String baseurl)
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
					String source = "casabertallot";
					TYPE type = TYPE.podcast;

					LOGGER.debug("Parsing page " + url);
					String id = getId(url);
					String artist = "Alessio Bertallot";
					String title = doc.get("title").getAsString();

					org.bson.Document json = docs.find(Filters.and(Filters.eq("source", source), 
							Filters.eq("url", url))).iterator().tryNext();
					if(json == null)
					{
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
				});
			}
		}
		catch (Throwable t) 
		{
			LOGGER.error("Error parsing page " + baseurl + ": " + t.getMessage(), t);
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
