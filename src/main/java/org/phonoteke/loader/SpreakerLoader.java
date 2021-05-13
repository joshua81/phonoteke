package org.phonoteke.loader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class SpreakerLoader extends PodcastLoader
{
	private static final String SPREAKER = "spreaker";

	public static final String CASABERTALLOT = "casabertallot";
	public static final String ROLLOVERHANGOVER = "rolloverhangover";
	public static final String BLACKALOT = "blackalot";
	public static final String CASSABERTALLOT = "cassabertallot";
	public static final String RESETREFRESH = "resetrefresh";
	public static final String THETUESDAYTAPES = "thetuesdaytapes";
	public static final String JAZZTRACKS = "jazztracks";


	@Override
	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? shows.find(Filters.and(Filters.eq("type", SPREAKER))).iterator() : 
			shows.find(Filters.and(Filters.eq("type", SPREAKER), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			url = show.getString("url");
			artist = show.getString("title");
			source = show.getString("source");
			authors = show.get("authors", List.class);
			LOGGER.info("Crawling " + artist);
			crawl(url);
			updateLastEpisodeDate(source);
		}
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
					TYPE type = TYPE.podcast;

					LOGGER.debug("Parsing page " + pageUrl);
					String id = getId(pageUrl);
					String title = doc.get("title").getAsString();

					org.bson.Document json = docs.find(Filters.and(Filters.eq("source", source), 
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
							LOGGER.info(json.getString("type") + " " + pageUrl + " added");
						}
						catch(Exception e) {
							LOGGER.error("ERROR parsing page " + pageUrl + ": " + e.getMessage());
						}
					}
				});
			}
		}
		catch (Throwable t) 
		{
			LOGGER.error("ERROR parsing page " + url + ": " + t.getMessage());
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
		return checkTracks(tracks);
	}
}
