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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PhonotekeMusicbrainz {

	protected static final int THREAD_SLEEP = 3000;
	protected static final Logger LOGGER = LogManager.getLogger(AbstractCrawler.class.getName());

	protected static final String SQL_FIND_ALBUMS = "SELECT * FROM musicdb.document WHERE (bandId IS NULL OR albumId IS NULL) AND type = 'REVIEW' ORDER BY creation_date DESC";
	protected static final String SQL_FIND_BANDS = "SELECT * FROM musicdb.document WHERE (bandId IS NULL) AND type = 'MONOGRAPH' ORDER BY creation_date DESC";
	protected static final String SQL_SET_BANDID = "UPDATE musicdb.document SET bandId = ? WHERE id = ?";
	protected static final String SQL_SET_ALBUMID = "UPDATE musicdb.document SET albumId = ? WHERE id = ?";
	protected static final String SQL_SET_AMAZONID = "UPDATE musicdb.document SET amazonId = ? WHERE id = ?";

	protected static Connection db = null;
	protected static PreparedStatement queryFindAlbums = null;
	protected static PreparedStatement queryFindBands = null;
	protected static PreparedStatement querySetMusicbrainzBandId = null;
	protected static PreparedStatement querySetMusicbrainzAlbumId = null;
	protected static PreparedStatement querySetMusicbrainzAmazonId = null;

	static 
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			db = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/musicdb", "musicdb", "musicdb");
			queryFindAlbums = db.prepareStatement(SQL_FIND_ALBUMS);
			queryFindBands = db.prepareStatement(SQL_FIND_BANDS);
			querySetMusicbrainzBandId = db.prepareStatement(SQL_SET_BANDID);
			querySetMusicbrainzAlbumId = db.prepareStatement(SQL_SET_ALBUMID);
			querySetMusicbrainzAmazonId = db.prepareStatement(SQL_SET_AMAZONID);
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
			// Album
			ResultSet albums = queryFindAlbums.executeQuery();
			while(albums.next())
			{
				String id = albums.getString("id");
				String artist = albums.getString("band");
				String release = albums.getString("album");

				// MusicBrainz
				String mbAlbumId = albums.getString("albumId");
				if(mbAlbumId == null)
				{
					String[] mbid = getMusicBrainzAlbumId(artist, release);
					if(mbid == null)
					{
						LOGGER.info(artist + " - " + release + ": MBid not found");
					}
					else
					{
						querySetMusicbrainzBandId.setString(1, mbid[1]);
						querySetMusicbrainzBandId.setString(2, id);
						querySetMusicbrainzBandId.executeUpdate();
						LOGGER.info(artist + ": MBid " + mbid[1]);
						
						querySetMusicbrainzAlbumId.setString(1, mbid[0]);
						querySetMusicbrainzAlbumId.setString(2, id);
						querySetMusicbrainzAlbumId.executeUpdate();
						LOGGER.info(artist + " - " + release + ": MBid " + mbid[0]);
					}
				}

				// Sleep a while to prevent 'Found robot'
				Thread.sleep(THREAD_SLEEP);
			}

			// Band
			ResultSet bands = queryFindBands.executeQuery();
			while(bands.next())
			{
				String id = albums.getString("id");
				String artist = albums.getString("band");
				
				// MusicBrainz
				String mbBandId = bands.getString("bandId");
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
				
				// Sleep a while to prevent 'Found robot'
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

	private static String[] getMusicBrainzAlbumId(String artist, String release)
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

			Map<String, List<Element>> ids = new HashMap<String, List<Element>>();
			Elements releaseElements = doc.select("release");
			for(int i = 0; i < releaseElements.size(); i++)
			{
				Element releaseElement = releaseElements.get(i);
				int score = new Integer(releaseElement.attr("ext:score"));
				if(score == 100)
				{
					String bandId = releaseElement.getElementsByTag("artist-credit").get(0).getElementsByTag("name-credit").get(0).getElementsByTag("artist").get(0).attr("id");
					if(!ids.containsKey(bandId))
					{
						ids.put(bandId, new ArrayList<Element>());
					}
					ids.get(bandId).add(releaseElement);
				}
			}

			String bandId = ids.keySet().iterator().next();
			String albumId = ids.values().iterator().next().get(0).attr("id");
			String amazonId = ids.values().iterator().next().get(0).getElementsByTag("asin").get(0).data();
			return ids.keySet().size() == 1 ? new String[]{bandId, albumId, amazonId} : null;
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
