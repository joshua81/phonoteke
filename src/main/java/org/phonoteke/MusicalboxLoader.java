package org.phonoteke;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Maps;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MusicalboxLoader 
{
	private static final Logger LOGGER = LogManager.getLogger(MusicalboxLoader.class);
	private static final List<String> ERRORS = Lists.newArrayList("An internal error occurred", "[an error occurred while processing this directive]", "PLAY");
	
	public static final String MONGO_HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String MONGO_DB = "phonoteke";

	private MongoCollection<org.bson.Document> pages;
	private MongoCollection<org.bson.Document> albums;


	public static void main(String[] args) 
	{
		MusicalboxLoader loader = new MusicalboxLoader();
		loader.loadAlbums();
	}

	public MusicalboxLoader()
	{
		try
		{
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
			pages = db.getCollection("pages");
			albums = db.getCollection("albums");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	private void loadAlbums()
	{
		//		http://musicbrainz.org/ws/2/recording/?query=artist:BROOKZILL%20AND%20recording:LET%E2%80%99S%20GO%20(E%20NOIZ)!
		//		MongoCursor<org.bson.Document> i = pages.find(Filters.eq("url", "https://www.raiplayradio.it/audio/2017/05/2Night-Musicalbox-del-18052017-958ca610-d269-45e7-a665-b60f21a18590.html")).iterator();
		MongoCursor<org.bson.Document> i = pages.find(Filters.eq("source", "musicalbox")).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			String html = page.get("page", String.class);

			// check if the article was already crawled
			MongoCursor<org.bson.Document> j = albums.find(Filters.and(
					Filters.eq("source", "musicalbox"), 
					Filters.eq("url", getUrl(url)))).iterator();
			if(!j.hasNext())
			{
				try
				{
					Document doc = Jsoup.parse(html);
					String id = getId(url);
					String cover = getCover(doc);
					Date date = getDate(doc);
					String desc = getDescription(doc);
					List<Map<String,String>> tracks = getTracks(doc);
					String title = getTitle(doc);
					String source = "musicalbox";

					if(CollectionUtils.isNotEmpty(tracks))
					{
						org.bson.Document json = new org.bson.Document("id", id).
								append("url", url).
								append("band", source).
								append("title", title).
								append("description", desc).
								append("tracks", tracks).
								append("date", date).
								append("cover", cover).
								append("source", source);
						albums.insertOne(json);
						LOGGER.info("Album " + url + " added");
					}
				}
				catch (Throwable t) 
				{
					LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
				}
			}
		}
	}

	private String getId(String url) 
	{
		url = getUrl(url);
		LOGGER.info("url: " + url);
		return Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
	}

	private String getUrl(String url) 
	{
		try 
		{
			if(url.startsWith(".") || url.startsWith("/"))
			{
				url = new URL(new URL(MusicalboxCrawler.MUSICALBOX_URL2), url).toString();
				url = url.replaceAll("\\.\\./", "");
			}
			return url.trim();
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error getUrl() "+ url + ": " + t.getMessage());
			return null;
		} 
	}

	private Date getDate(Document doc) 
	{
		Date date = null;
		try
		{
			Element content = doc.select("ul.menuDescriptionProgramma").first();
			if(content != null && content.children() != null)
			{
				date = new SimpleDateFormat("dd/MM/yyyy").parse(content.children().get(0).text());
			}
		}
		catch(ParseException e)
		{
			// nothing to do
		}
		LOGGER.info("date: " + date);
		return date;
	}

	private String getDescription(Document doc) 
	{
		String desc = null;
		Element content = doc.select("div.aodDescription").first();
		if(content != null)
		{
			desc = content.text();

		}
		LOGGER.info("description: " + desc);
		return desc;
	}

	private String getTitle(Document doc) 
	{
		String title = null;
		Element content = doc.select("h1").first();
		if(content != null)
		{
			title = content.text();

		}
		LOGGER.info("title: " + title);
		return title;
	}

	private List<Map<String, String>> getTracks(Document doc) 
	{
		List<Map<String, String>> playlist = Lists.newArrayList();
		Element content = doc.select("div.aodHtmlDescription").first();
		if(content != null && content.children() != null)
		{
			Iterator<Element> i = content.children().iterator();
			while(i.hasNext())
			{
				String track = i.next().text().trim();
				if(StringUtils.isNoneBlank(track) && !ERRORS.contains(track))
				{
					String youtube = getYoutube(track);
					LOGGER.info("playlist: " + track + ", youtube: " + youtube);
					Map<String, String> map = Maps.newHashMap();
					map.put("track", track);
					map.put("youtube", youtube);
					playlist.add(map);
				}
			}
		}
		if(content != null && content.textNodes() != null)
		{
			Iterator<TextNode> i = content.textNodes().iterator();
			while(i.hasNext())
			{
				String track = i.next().text().trim();
				if(StringUtils.isNoneBlank(track) && !ERRORS.contains(track))
				{
					String youtube = getYoutube(track);
					LOGGER.info("playlist: " + track + ", youtube: " + youtube);
					Map<String, String> map = Maps.newHashMap();
					map.put("track", track);
					map.put("youtube", youtube);
					playlist.add(map);
				}
			}
		}
		return playlist;
	}

	private String getCover(Document doc) 
	{
		String cover = null;
		Element content = doc.select("img.imgHomeProgramma[src]").first();
		if(content != null)
		{
			cover = content.attr("src");
			cover = getUrl(cover);
		}
		LOGGER.info("cover: " + cover);
		return cover;
	}

	private String getYoutube(String track) 
	{
		try
		{
			// This object is used to make YouTube Data API requests. The last
			// argument is required, but since we don't need anything
			// initialized when the HttpRequest is initialized, we override
			// the interface and provide a no-op function.
			YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {}
			}).setApplicationName("Phonoteke").build();

			// Define the API request for retrieving search results.
			YouTube.Search.List search = youtube.search().list("id,snippet");

			// Set your developer key from the {{ Google Cloud Console }} for
			// non-authenticated requests. See:
			// {{ https://cloud.google.com/console }}
			String apiKey = "AIzaSyDshyjPIgMCMIcwIG2JQfqZ7AR3kfrqHNI";
			search.setKey(apiKey);
			search.setQ(track);

			// Restrict the search results to only include videos. See:
			// https://developers.google.com/youtube/v3/docs/search/list#type
			search.setType("video");

			// To increase efficiency, only retrieve the fields that the
			// application uses.
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(1L);

			// Call the API and print results.
			String youtubeId = null;
			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResults = searchResponse.getItems();
			if(CollectionUtils.isNotEmpty(searchResults))
			{
				youtubeId = searchResults.get(0).getId().getVideoId();
			}
			LOGGER.info("youtube: " + youtubeId);
			return youtubeId;
		}
		catch(Exception e)
		{
			LOGGER.error("ERROR getYoutube(): " + e.getMessage());
			return null;
		}
	}
}
