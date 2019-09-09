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
	protected MongoCollection<org.bson.Document> albums;
	protected MongoCollection<org.bson.Document> artists;
	protected MongoCollection<org.bson.Document> links;
	protected MongoCollection<org.bson.Document> tracks;

	protected enum TYPE {
		ARTIST,
		ALBUM,
		UNKNOWN
	}

	public static void main(String[] args) 
	{
		new OndarockLoader().load();
		new MusicalboxLoader().load();
	}

	public PhonotekeLoader()
	{
		try
		{
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
			pages = db.getCollection("pages");
			albums = db.getCollection("albums");
			artists = db.getCollection("artists");
			links = db.getCollection("links");
			tracks = db.getCollection("tracks");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	protected void load()
	{
		//		http://musicbrainz.org/ws/2/recording/?query=artist:BROOKZILL%20AND%20recording:LET%E2%80%99S%20GO%20(E%20NOIZ)!
		String source = getSource();
		MongoCursor<org.bson.Document> i = pages.find(Filters.eq("source", source)).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			String html = page.get("page", String.class);
			String id = getId(url);
			Document doc = Jsoup.parse(html);

			// Links
			if(!links.find(Filters.eq("id", id)).iterator().hasNext())
			{
				try
				{
					org.bson.Document json = new org.bson.Document("id", id).
							append("links", getLinks(url, doc));
					links.insertOne(json);
					LOGGER.info("Links " + url + " added");
				}
				catch (Throwable t) 
				{
					LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
				}
			}

			TYPE type = getType(url);
			if(TYPE.ALBUM.equals(type))
			{
				// Tracks
				if(!tracks.find(Filters.eq("id", id)).iterator().hasNext())
				{
					try
					{
						org.bson.Document json = new org.bson.Document("id", id).
								append("tracks", getTracks(url, doc));
						tracks.insertOne(json);
						LOGGER.info("Tracks " + url + " added");
					}
					catch (Throwable t) 
					{
						LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
					}
				}

				// Albums
				if(!albums.find(Filters.and(Filters.eq("source", source), 
						Filters.eq("url", url))).iterator().hasNext())
				{
					try
					{
						org.bson.Document json = new org.bson.Document("id", id).
								append("artist", getArtist(url, doc)).
								append("authors", getAuthors(url, doc)).
								append("cover", getCover(url, doc)).
								append("date", getDate(url, doc)).
								append("description", getDescription(url, doc)).
								append("genres", getGenres(url, doc)).
								append("idmbrz", getIdmbrz(url, doc)).
								append("idsptf", getIdsptf(url, doc)).
								append("label", getLabel(url, doc)).
								append("milestone", getMilestone(url, doc)).
								append("review", getReview(url, doc)).
								append("source", getSource()).
								append("title", getTitle(url, doc)).
								append("url", getUrl(url)).
								append("vote", getVote(url, doc)).
								append("year", getYear(url, doc));
						albums.insertOne(json);
						LOGGER.info("Album " + url + " added");
					}
					catch (Throwable t) 
					{
						LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
					}
				}
			}
			else if(TYPE.ARTIST.equals(type))
			{
				// Artist
				if(!artists.find(Filters.and(Filters.eq("source", source), 
						Filters.eq("url", url))).iterator().hasNext())
				{
					try
					{
						org.bson.Document json = new org.bson.Document("id", id).
								append("artist", getArtist(url, doc)).
								append("authors", getAuthors(url, doc)).
								append("cover", getCover(url, doc)).
								append("date", getDate(url, doc)).
								append("description", getDescription(url, doc)).
								append("idmbrz", getIdmbrz(url, doc)).
								append("idsptf", getIdsptf(url, doc)).
								append("review", getReview(url, doc)).
								append("source", getSource()).
								append("title", getTitle(url, doc)).
								append("url", getUrl(url));
						artists.insertOne(json);
						LOGGER.info("Artist " + url + " added");
					}
					catch (Throwable t) 
					{
						LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
					}
				}
			}
		}
	}

	//	private void delete()
	//	{
	//		MongoCursor<org.bson.Document> i = albums.find(Filters.eq("content", "")).iterator();
	//		while(i.hasNext())
	//		{
	//			org.bson.Document page = i.next();
	//			String url = page.get("url", String.class);
	//			albums.findOneAndDelete(Filters.eq("url", url));
	//			pages.findOneAndDelete(Filters.eq("url", url));
	//			LOGGER.info("Deleted page " + url);
	//		}
	//	}

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
			LOGGER.error("Error getUrl() "+ url + ": " + t.getMessage());
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

	protected String getIdmbrz(String url, Document doc) {
		return null;
	}

	protected String getIdsptf(String url, Document doc) {
		return null;
	}

	protected String getLabel(String url, Document doc) {
		return null;
	}

	protected Boolean getMilestone(String url, Document doc) {
		return null;
	}

	protected String getReview(String url, Document doc) {
		return null;
	}

	protected List<Map<String, String>> getLinks(String url, Document doc) {
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
