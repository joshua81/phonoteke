package org.phonoteke.loader;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.hash.Hashing;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class PhonotekeLoader 
{
	protected static final Logger LOGGER = LogManager.getLogger(PhonotekeLoader.class);

	protected static final String MONGO_HOST = "localhost";
	protected static final int MONGO_PORT = 27017;
	protected static final String MONGO_DB = "phonoteke";

	protected MongoCollection<org.bson.Document> pages;
	protected MongoCollection<org.bson.Document> docs;
	protected MongoCollection<org.bson.Document> tracks;
	protected MongoCollection<org.bson.Document> musicbrainz;

	protected enum TYPE {
		ARTIST,
		ALBUM,
		TRACK,
		CONCERT,
		INTERVIEW,
		UNKNOWN
	}

	public static void main(String[] args) 
	{
		new OndarockLoader().loadDocuments();
		new OndarockLoader().loadTracks();

		new MusicalboxLoader().loadDocuments();
		new MusicalboxLoader().loadTracks();
	}

	public PhonotekeLoader()
	{
		try
		{
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
			pages = db.getCollection("pages");
			docs = db.getCollection("docs");
			tracks = db.getCollection("tracks");
			musicbrainz = db.getCollection("musicbrainz");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage(), t);
			throw new RuntimeException(t);
		}
	}

	protected void loadTracks()
	{
		String source = getSource();
		MongoCursor<org.bson.Document> i = pages.find(Filters.eq("source", source)).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			String html = page.get("page", String.class);
			String id = getId(url);
			Document doc = Jsoup.parse(html);

			try
			{
				switch(getType(url))
				{
				case ALBUM:
				case CONCERT:
					if(!tracks.find(Filters.eq("id", id)).iterator().hasNext())
					{
						org.bson.Document json = new org.bson.Document("id", id).
								append("tracks", getTracks(url, doc));
						tracks.insertOne(json);
						LOGGER.info("Tracks " + url + " added");
					}
					break;
				default:
					break;
				}
			}
			catch (Throwable t) 
			{
				LOGGER.error("Error parsing page " + url + ": " + t.getMessage(), t);
			}
		}
	}

	protected void loadDocuments()
	{
		String source = getSource();
		MongoCursor<org.bson.Document> i = pages.find(Filters.eq("source", source)).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			String html = page.get("page", String.class);
			String id = getId(url);
			Document doc = Jsoup.parse(html);

			try
			{
				String artist = getArtist(url, doc);
				String title = getTitle(url, doc);
				TYPE type = getType(url);

				if(!docs.find(Filters.and(Filters.eq("source", source), 
						Filters.eq("url", url))).iterator().hasNext())
				{
					org.bson.Document json = null;
					switch(type)
					{
					case ALBUM:
						if(!docs.find(Filters.and(Filters.eq("source", source),
								Filters.eq("type", type.name().toLowerCase()),
								Filters.eq("artist", artist),
								Filters.eq("title", title))).iterator().hasNext())
						{
							json = new org.bson.Document("id", id).
									append("type", type.name().toLowerCase()).
									append("artist", artist).
									append("authors", getAuthors(url, doc)).
									append("cover", getCover(url, doc)).
									append("date", getDate(url, doc)).
									append("description", getDescription(url, doc)).
									append("genres", getGenres(url, doc)).
									append("label", getLabel(url, doc)).
									append("links", getLinks(url, doc)).
									append("review", getReview(url, doc)).
									append("source", getSource()).
									append("title", title).
									append("url", getUrl(url)).
									append("vote", getVote(url, doc)).
									append("year", getYear(url, doc));
						}
						break;
					case ARTIST:
						if(!docs.find(Filters.and(Filters.eq("source", source), 
								Filters.eq("type", type.name().toLowerCase()),
								Filters.eq("artist", artist))).iterator().hasNext())
						{
							json = new org.bson.Document("id", id).
									append("type", type.name().toLowerCase()).
									append("artist", artist).
									append("authors", getAuthors(url, doc)).
									append("cover", getCover(url, doc)).
									append("date", getDate(url, doc)).
									append("description", getDescription(url, doc)).
									append("links", getLinks(url, doc)).
									append("review", getReview(url, doc)).
									append("source", getSource()).
									append("title", title).
									append("url", getUrl(url));
						}
						break;
					case CONCERT:
						if(!docs.find(Filters.and(Filters.eq("source", source), 
								Filters.eq("type", type.name().toLowerCase()),
								Filters.eq("artist", artist),
								Filters.eq("title", title))).iterator().hasNext())
						{
							json = new org.bson.Document("id", id).
									append("type", type.name().toLowerCase()).
									append("artist", artist).
									append("authors", getAuthors(url, doc)).
									append("cover", getCover(url, doc)).
									append("date", getDate(url, doc)).
									append("description", getDescription(url, doc)).
									append("links", getLinks(url, doc)).
									append("review", getReview(url, doc)).
									append("source", getSource()).
									append("title", title).
									append("url", getUrl(url));
						}
						break;
					default:
						break;
					}
					if(json != null)
					{
						docs.insertOne(json);
						LOGGER.info(json.get("type", String.class).toUpperCase() + " " + url + " added");
					}
					else if(TYPE.ALBUM.equals(getType(url)))
					{
						page.put("vote", getVote(url, doc));
						docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
						LOGGER.info(page.get("type", String.class).toUpperCase() + " " + url + " updated");
					}
				}
			}
			catch (Throwable t) 
			{
				LOGGER.error("Error parsing page " + url + ": " + t.getMessage(), t);
			}
		}
	}

	protected String getId(String url) 
	{
		return Hashing.sha256().hashString(getUrl(url), StandardCharsets.UTF_8).toString();
	}

	protected String getUrl(String url) 
	{
		try 
		{
			if(url.startsWith(".") || url.startsWith("/"))
			{
				url = new URL(new URL(getBaseUrl()), url).toString();
				url = url.replaceAll("\\.\\./", "");
			}
			return url.trim();
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error getUrl() "+ url + ": " + t.getMessage(), t);
			return null;
		} 
	}

	//---------------------------------
	// Methods to be overridden
	//---------------------------------
	protected String getBaseUrl()
	{
		return null;
	}

	protected String getSource() 
	{
		return null;
	}

	protected TYPE getType(String url) 
	{
		return TYPE.UNKNOWN;
	}

	protected String getArtist(String url, Document doc) {
		return null;
	}

	protected List<String> getAuthors(String url, Document doc) {
		return null;
	}

	protected String getCover(String url, Document doc) {
		return null;
	}

	protected Date getDate(String url, Document doc) {
		return null;
	}

	protected String getDescription(String url, Document doc) {
		return null;
	}

	protected List<String> getGenres(String url, Document doc) {
		return null;
	}

	protected String getLabel(String url, Document doc) {
		return null;
	}

	protected String getReview(String url, Document doc) {
		return null;
	}

	protected List<String> getLinks(String url, Document doc) {
		return null;
	}

	protected String getTitle(String url, Document doc) {
		return null;
	}

	protected List<Map<String, String>> getTracks(String url, Document doc) {
		return null;
	}

	protected Float getVote(String url, Document doc) {
		return null;
	}

	protected Integer getYear(String url, Document doc) {
		return null;
	}
}
