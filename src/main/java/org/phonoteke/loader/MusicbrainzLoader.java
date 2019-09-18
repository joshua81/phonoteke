package org.phonoteke.loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MusicbrainzLoader 
{
	private static final Logger LOGGER = LogManager.getLogger(MusicbrainzLoader.class);

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "phonoteke";

	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";

	private MongoCollection<org.bson.Document> docs;
	private MongoCollection<org.bson.Document> tracks;
	private MongoCollection<org.bson.Document> musicbrainz;


	public static void main(String[] args) 
	{
		//		new MusicbrainzLoader().loadAlbums();
		//		new MusicbrainzLoader().loadArtists();
		new MusicbrainzLoader().loadTracks();
	}

	public MusicbrainzLoader()
	{
		try
		{
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
			docs = db.getCollection("docs");
			tracks = db.getCollection("tracks");
			musicbrainz = db.getCollection("musicbrainz");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	private void loadAlbums()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "album"))).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String artist = page.get("artist", String.class);
			String album = page.get("title", String.class);
			if(!musicbrainz.find(Filters.eq("id", id)).iterator().hasNext())
			{
				org.bson.Document json = getAlbum(artist, album);
				if(json != null)
				{
					json.append("id", id);
					musicbrainz.insertOne(json);
					LOGGER.info("Musicbrainz " + id + " Album added");
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}

	private void loadArtists()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "artist"))).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String artist = page.get("artist", String.class);
			if(!musicbrainz.find(Filters.eq("id", id)).iterator().hasNext())
			{
				org.bson.Document json = getArtist(artist);
				if(json != null)
				{
					json.append("id", id);
					musicbrainz.insertOne(json);
					LOGGER.info("Musicbrainz " + id + " Artist added");
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}

	private void loadTracks()
	{
		MongoCursor<org.bson.Document> i = tracks.find().iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			List<org.bson.Document> recordings = page.get("tracks", List.class);
			for(org.bson.Document recording : recordings)
			{
				String title = recording.get("title", String.class);
				org.bson.Document json = recording.get("musicbrainz", org.bson.Document.class);
				if(json == null && StringUtils.isNoneBlank(title))
				{
					json = getRecording(title);
					if(json != null)
					{
						recording.append("musicbrainz", json);
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
			tracks.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Musicbrainz " + id + " Tracks updated");
		}
	}

	private org.bson.Document getRecording(String recording)
	{
		String urlString = MUSICBRAINZ + "/recording/?query=recording:" + recording.trim().replace(" ", "%20") + "&fmt=json";
//		String urlString = MUSICBRAINZ + "/recording/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20" + "recording:" + album.trim().replace(" ", "%20") + "&fmt=json";
		String json = "";
		String line = null;
		HttpURLConnection con;
		try 
		{
			con = (HttpURLConnection)new URL(urlString).openConnection();
		} 
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}

		try(BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));)
		{
			while ((line = rd.readLine()) != null) 
			{
				json += line;
			}
			return org.bson.Document.parse(json);
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}
	}

	private org.bson.Document getArtist(String artist)
	{
		String urlString = MUSICBRAINZ + "/artist/?query=artist:" + artist.trim().replace(" ", "%20") + "&fmt=json";
		String json = "";
		String line = null;
		HttpURLConnection con;
		try 
		{
			con = (HttpURLConnection) new URL(urlString).openConnection();
		} 
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}

		try(BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));)
		{
			while ((line = rd.readLine()) != null) 
			{
				json += line;
			}
			return org.bson.Document.parse(json);
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}
	}

	private org.bson.Document getAlbum(String artist, String album)
	{
		String urlString = MUSICBRAINZ + "/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20" + "release:" + album.trim().replace(" ", "%20") + "&fmt=json";
		String json = "";
		String line = null;
		HttpURLConnection con;
		try 
		{
			con = (HttpURLConnection) new URL(urlString).openConnection();
		} 
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}

		try(BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));)
		{
			while ((line = rd.readLine()) != null) 
			{
				json += line;
			}
			return org.bson.Document.parse(json);
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}
	}

	//	private String[] getAlbumIds(String artist, String album)
	//	{
	//		BufferedReader rd = null;
	//		try
	//		{
	//			String urlString = MUSICBRAINZ + "/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20" + "release:" + album.trim().replace(" ", "%20") + "&fmt=json";
	//			String json = "";
	//			String line = null;
	//			HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
	//			rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
	//			while ((line = rd.readLine()) != null) 
	//			{
	//				json += line;
	//			}
	//			BasicDBObject doc = BasicDBObject.parse(json);
	//			List<BasicDBObject> releases = (List<BasicDBObject>)doc.get("releases");
	//
	//			for(BasicDBObject release : releases)
	//			{
	//				int score = (Integer)release.get("score");
	//				if(score == 100)
	//				{
	//					for(BasicDBObject ac : (List<BasicDBObject>)release.get("artist-credit"))
	//					{
	//						String artistId = (String)((BasicDBObject)((List<BasicDBObject>)release.get("artist-credit")).get(0).get("artist")).get("id");
	//						String albumId = (String)((BasicDBObject)release.get("release-group")).get("id");
	//						//String amazonId = (String)release.get("asin");
	//						return new String[]{artistId, albumId};
	//					}
	//				}
	//			}
	//			return null;
	//		}
	//		catch(Throwable t)
	//		{
	//			LOGGER.error("ERROR: " + t.getMessage());
	//			return null;
	//		}
	//		finally
	//		{
	//			if(rd != null)
	//			{
	//				try 
	//				{
	//					rd.close();
	//				} 
	//				catch (Throwable t) 
	//				{
	//					// do nothing
	//				}
	//			}
	//		}
	//	}
}
