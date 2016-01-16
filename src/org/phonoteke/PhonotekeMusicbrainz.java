package org.phonoteke;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PhonotekeMusicbrainz {

	protected static final int THREAD_SLEEP = 2000;
	protected static final Logger LOGGER = LogManager.getLogger(AbstractCrawler.class.getName());

	protected static final String SQL_FIND_DOCUMENTS = "SELECT * FROM musicdb.document WHERE bandId IS NULL OR albumId IS NULL ORDER BY creation_date DESC";
	protected static final String SQL_SET_MUSICBRAINZ_BANDID = "UPDATE musicdb.document SET bandId = ? WHERE id = ? ORDER BY creation_date DESC";
	protected static final String SQL_SET_MUSICBRAINZ_ALBUMID = "UPDATE musicdb.document SET albumId = ? WHERE id = ? ORDER BY creation_date DESC";

	protected static Connection db = null;
	protected static PreparedStatement queryFindDocuments = null;
	protected static PreparedStatement querySetMusicbrainzBandId = null;
	protected static PreparedStatement querySetMusicbrainzAlbumId = null;

	static 
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			db = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/musicdb", "musicdb", "musicdb");
			queryFindDocuments = db.prepareStatement(SQL_FIND_DOCUMENTS);
			querySetMusicbrainzBandId = db.prepareStatement(SQL_SET_MUSICBRAINZ_BANDID);
			querySetMusicbrainzAlbumId = db.prepareStatement(SQL_SET_MUSICBRAINZ_ALBUMID);
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to MySQL db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	public static void main(String[] args) {
		try
		{
			ResultSet docs = queryFindDocuments.executeQuery();
			while(docs.next())
			{
				AbstractCrawler.TYPE type = AbstractCrawler.TYPE.valueOf(docs.getString("type"));
				String id = docs.getString("id");
				String artist = docs.getString("band");
				String release = docs.getString("album");

				// MusicBrainz
				switch (type) {
				case REVIEW:
					String mbBandId = docs.getString("bandId");
					if(mbBandId == null)
					{
						String mbid = getMusicBrainzBandId(artist);
						if(mbid == null)
						{
							LOGGER.info(artist + ": MBid not found");
						}
						else
						{
							querySetMusicbrainzBandId.setString(1, mbid);
							querySetMusicbrainzBandId.setString(2, id);
							querySetMusicbrainzBandId.executeUpdate();
							LOGGER.info(artist + ": MBid " + mbid);
						}
					}

					String mbAlbumId = docs.getString("albumId");
					if(mbAlbumId == null)
					{
						String mbid = getMusicBrainzAlbumId(artist, release);
						if(mbid == null)
						{
							LOGGER.info(artist + " - " + release + ": MBid not found");
						}
						else
						{
							querySetMusicbrainzAlbumId.setString(1, mbid);
							querySetMusicbrainzAlbumId.setString(2, id);
							querySetMusicbrainzAlbumId.executeUpdate();
							LOGGER.info(artist + " - " + release + ": MBid " + mbid);
						}
					}
					break;
				case MONOGRAPH:
					mbBandId = docs.getString("bandId");
					if(mbBandId == null)
					{
						String mbid = getMusicBrainzBandId(artist);
						if(mbid == null)
						{
							LOGGER.info(artist + ": MBid not found");
						}
						else
						{
							querySetMusicbrainzBandId.setString(1, mbid);
							querySetMusicbrainzBandId.setString(2, id);
							querySetMusicbrainzBandId.executeUpdate();
							LOGGER.info(artist + ": MBid " + mbid);
						}
					}
					break;
				default:

				}

				// To prevent MusicBrainz '503 Service Unavailable error'
				Thread.sleep(THREAD_SLEEP);
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
		}
	}


	private static String getMusicBrainzBandId(String artist)
	{
		BufferedReader rd = null;
		try
		{
			String urlString = "http://musicbrainz.org/ws/2/artist/?query=artist:" + artist.trim().replace(" ", "%20");
			String result = "";
			String line = null;
			HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
			rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = rd.readLine()) != null) 
			{
				result += line;
			}
			Document doc = Jsoup.parse(result);

			List<String> ids = new ArrayList<String>();
			Elements artistElements = doc.select("artist");
			for(int i = 0; i < artistElements.size(); i++)
			{
				Element artistElement = artistElements.get(i);
				int score = new Integer(artistElement.attr("ext:score"));
				if(score == 100)
				{
					ids.add(artistElement.attr("id"));
				}
			}

			return ids.size() == 1 ? ids.get(0) : null;
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}
		finally
		{
			if(rd != null)
			{
				try 
				{
					rd.close();
				} 
				catch (Throwable t) 
				{
					// do nothing
				}
			}
		}
	}

	private static String getMusicBrainzAlbumId(String artist, String release)
	{
		BufferedReader rd = null;
		try
		{
			String urlString = "http://musicbrainz.org/ws/2/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20" + "release:" + release.trim().replace(" ", "%20");
			String result = "";
			String line = null;
			HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
			rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = rd.readLine()) != null) 
			{
				result += line;
			}
			Document doc = Jsoup.parse(result);

			List<String> ids = new ArrayList<String>();
			Elements releaseElements = doc.select("release");
			for(int i = 0; i < releaseElements.size(); i++)
			{
				Element releaseElement = releaseElements.get(i);
				int score = new Integer(releaseElement.attr("ext:score"));
				if(score == 100)
				{
					ids.add(releaseElement.attr("id"));
				}
			}

			return ids.size() == 1 ? ids.get(0) : null;
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
			return null;
		}
		finally
		{
			if(rd != null)
			{
				try 
				{
					rd.close();
				} 
				catch (Throwable t) 
				{
					// do nothing
				}
			}
		}
	}
}
