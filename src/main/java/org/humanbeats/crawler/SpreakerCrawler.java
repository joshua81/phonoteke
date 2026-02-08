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
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.util.HumanBeatsUtils.TYPE;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpreakerCrawler extends AbstractCrawler
{
	private static final String SPREAKER = "spreaker";

	public SpreakerCrawler(MongoRepository repo) {
		super(repo);
	}

	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", SPREAKER))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", SPREAKER), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			this.url = show.getString("url");
			this.artist = show.getString("title");
			this.source = show.getString("source");
			this.authors = show.get("authors", List.class);

			log.info("Crawling " + artist);
			crawl(url);
		}
	}

	@Override
	public HBDocument crawlDocument(String url, org.jsoup.nodes.Document doc) {
		// TODO Auto-generated method stub
		return null;
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

					log.debug("Parsing page " + pageUrl);
					String id = getId(pageUrl);
					String title = doc.get("title").getAsString();

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
							insertDoc(json);
						}
						catch(Exception e) {
							log.debug("ERROR parsing page " + pageUrl + ": " + e.getMessage());
						}
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
	protected String getBaseUrl() {
		return "https://www.spreaker.com/";
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

	private List<HBTrack> getTracks(String content) {
		List<HBTrack> tracks = Lists.newArrayList();
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
}
