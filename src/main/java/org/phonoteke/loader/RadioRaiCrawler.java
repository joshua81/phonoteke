package org.phonoteke.loader;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.phonoteke.loader.HumanBeatsUtils.TYPE;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RadioRaiCrawler extends AbstractCrawler
{
	private static final String URL = "https://www.raiplaysound.it/";
	private static final String URL2 = "https://www.raiplaysound.it";
	private static final String RAI = "rai";

	private static final String MUSICALBOX = "musicalbox";

	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", RAI))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", RAI), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			RadioRaiCrawler.url = show.getString("url");
			RadioRaiCrawler.artist = show.getString("title");
			RadioRaiCrawler.source = show.getString("source");
			RadioRaiCrawler.authors = show.get("authors", List.class);

			log.info("Crawling " + artist);
			crawl(url);
		}
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
				TYPE type = TYPE.podcast;
				try {
					log.debug("Parsing page " + pageUrl);
					String id = getId(pageUrl);

					org.bson.Document json = repo.getDocs().find(Filters.and(Filters.eq("source", source), 
							Filters.eq("url", pageUrl))).iterator().tryNext();
					if(json == null)
					{
						HttpURLConnection con2 = (HttpURLConnection)new URL(pageUrl.replace(".html", ".json")).openConnection();
						JsonObject doc = new Gson().fromJson(new InputStreamReader(con2.getInputStream()), JsonObject.class);

						json = new org.bson.Document("id", id).
								append("url", getUrl(pageUrl)).
								append("type", type.name()).
								append("artist", artist).
								append("title", doc.get("episode_title").getAsString()).
								append("authors", authors).
								append("cover", URL + doc.get("image").getAsString()).
								append("date", getDate(doc.get("literal_publication_date").getAsString())).
								append("description", doc.get("description").getAsString()).
								append("genres", null).
								append("label", null).
								append("links", null).
								append("review", null).
								append("source", source).
								append("vote", null).
								append("year", getYear(doc.get("literal_publication_date").getAsString())).
								append("tracks", getTracks(doc.get("description").getAsString())).
								append("audio", getAudio(doc.get("audio").getAsJsonObject().get("url").getAsString()));
						insertDoc(json);
					}
				}
				catch(Exception e) {
					log.debug("ERROR parsing page " + pageUrl + ": " + e.getMessage());
				}
			});
		}
		catch (Throwable t) {
			log.debug("ERROR parsing page " + url + ": " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	@Override
	protected String getBaseUrl()
	{
		return URL;
	}

	@Override
	protected String getSource() 
	{
		return source;
	}

	@Override
	protected String getArtist(String url, Document doc) 
	{
		return artist;
	}

	@Override
	protected List<String> getAuthors(String url, Document doc) 
	{
		return authors;
	}

	protected Date getDate(String date) 
	{
		try {
			return new SimpleDateFormat("dd MMM yyyy", Locale.ITALY).parse(date);
		} 
		catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	protected Integer getYear(String date)
	{
		Calendar year = Calendar.getInstance();
		year.setTime(getDate(date));
		return year.get(Calendar.YEAR);
	}

	protected List<org.bson.Document> getTracks(String content) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		if(MUSICALBOX.equals(RadioRaiCrawler.source)) {
			return tracks;
		}

		if(content != null)
		{
			String[] chunks = content.replace("//", HumanBeatsUtils.TRACKS_NEW_LINE).split(HumanBeatsUtils.TRACKS_NEW_LINE);
			for(int i = 0; i < chunks.length; i++)
			{
				String title = chunks[i].trim();
				if(StringUtils.isNotBlank(title) && HumanBeatsUtils.isTrack(title))
				{
					tracks.add(newTrack(title, null));
					log.debug("tracks: " + title);
				}
			}
		}
		return checkTracks(tracks);
	}

	protected String getAudio(String url) 
	{
		try {
			Document doc = Jsoup.connect(url).ignoreContentType(true).get();
			return doc.location();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
