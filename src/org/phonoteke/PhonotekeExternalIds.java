package org.phonoteke;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PhonotekeExternalIds {

	protected static final int THREAD_SLEEP = 2000;
	protected static final Logger LOGGER = LogManager.getLogger(AbstractCrawler.class.getName());

	protected static final String SQL_FIND_DOCUMENTS = "SELECT * FROM musicdb.document WHERE bandId IS NULL OR albumId IS NULL OR bandIdSptf IS NULL OR albumIdSptf IS NULL ORDER BY creation_date DESC";
	protected static final String SQL_SET_MUSICBRAINZ_BANDID = "UPDATE musicdb.document SET bandId = ? WHERE id = ? ORDER BY creation_date DESC";
	protected static final String SQL_SET_MUSICBRAINZ_ALBUMID = "UPDATE musicdb.document SET albumId = ? WHERE id = ? ORDER BY creation_date DESC";
	protected static final String SQL_SET_SPOTIFY_BANDID = "UPDATE musicdb.document SET bandIdSptf = ? WHERE id = ? ORDER BY creation_date DESC";
	protected static final String SQL_SET_SPOTIFY_ALBUMID = "UPDATE musicdb.document SET albumIdSptf = ? WHERE id = ? ORDER BY creation_date DESC";

	protected static Connection db = null;
	protected static PreparedStatement queryFindDocuments = null;
	protected static PreparedStatement querySetMusicbrainzBandId = null;
	protected static PreparedStatement querySetMusicbrainzAlbumId = null;
	protected static PreparedStatement querySetSpotifyBandId = null;
	protected static PreparedStatement querySetSpotifyAlbumId = null;

	static 
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			db = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/musicdb", "musicdb", "musicdb");
			queryFindDocuments = db.prepareStatement(SQL_FIND_DOCUMENTS);
			querySetMusicbrainzBandId = db.prepareStatement(SQL_SET_MUSICBRAINZ_BANDID);
			querySetMusicbrainzAlbumId = db.prepareStatement(SQL_SET_MUSICBRAINZ_ALBUMID);
			querySetSpotifyBandId = db.prepareStatement(SQL_SET_SPOTIFY_BANDID);
			querySetSpotifyAlbumId = db.prepareStatement(SQL_SET_SPOTIFY_ALBUMID);
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
					String mbAlbumId = docs.getString("albumId");
					if(mbAlbumId == null)
					{
						String mbid = getMBId(type, artist, release);
						if(mbid == null)
						{
							LOGGER.info(artist + "(" + release + ") MusicBrainz id: not found");
						}
						else
						{
							querySetMusicbrainzAlbumId.setString(1, mbid);
							querySetMusicbrainzAlbumId.setString(2, id);
							querySetMusicbrainzAlbumId.executeUpdate();
							LOGGER.info(artist + "(" + release + ") MusicBrainz id: " + mbid);
						}
					}
					break;
				case MONOGRAPH:
					String mbBandId = docs.getString("bandId");
					if(mbBandId == null)
					{
						String mbid = getMBId(type, artist, release);
						if(mbid == null)
						{
							LOGGER.info(artist + " MusicBrainz id: not found");
						}
						else
						{
							querySetMusicbrainzBandId.setString(1, mbid);
							querySetMusicbrainzBandId.setString(2, id);
							querySetMusicbrainzBandId.executeUpdate();
							LOGGER.info(artist + " MusicBrainz id: " + mbid);
						}
					}
					break;
				}

				// Spotify
				switch (type) {
				case REVIEW:
					String sptfAlbumId = docs.getString("albumIdSptf");
					if(sptfAlbumId == null)
					{
						String sptfid = getSPTFId(type, artist, release);
						if(sptfid == null)
						{
							LOGGER.info(artist + "(" + release + ") Spotify id: not found");
						}
						else
						{
							querySetSpotifyAlbumId.setString(1, sptfid);
							querySetSpotifyAlbumId.setString(2, id);
							querySetSpotifyAlbumId.executeUpdate();
							LOGGER.info(artist + "(" + release + ") Spotify id: " + sptfid);
						}
					}
					break;
				case MONOGRAPH:
					String sptfBandId = docs.getString("bandIdSptf");
					if(sptfBandId == null)
					{
						// TODO: to be implemented
					}
					break;
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


	private static String getMBId(AbstractCrawler.TYPE type, String artist, String release)
	{
		BufferedReader rd = null;
		try
		{
			String urlString = null;
			switch (type) {
			case REVIEW:
				urlString = "http://musicbrainz.org/ws/2/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20" + "release:" + release.trim().replace(" ", "%20");
				break;
			case MONOGRAPH:
				urlString = "http://musicbrainz.org/ws/2/artist/?query=artist:" + artist.trim().replace(" ", "%20");
				break;
			}

			String result = "";
			String line = null;
			HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
			rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = rd.readLine()) != null) 
			{
				result += line;
			}
			Document doc = Jsoup.parse(result);

			String mbid = null;
			switch (type) {
			case REVIEW:
				Elements releaseElements = doc.select("release");
				for(int i = 0; i < releaseElements.size(); i++)
				{
					Element releaseElement = releaseElements.get(i);
					int score = new Integer(releaseElement.attr("ext:score"));
					if(score == 100)
					{
						mbid = releaseElement.attr("id");
						break;
					}
				}
				break;
			case MONOGRAPH:
				Elements artistElements = doc.select("artist");
				for(int i = 0; i < artistElements.size(); i++)
				{
					Element artistElement = artistElements.get(i);
					int score = new Integer(artistElement.attr("ext:score"));
					if(score == 100)
					{
						mbid = artistElement.attr("id");
						break;
					}
				}
				break;
			default:
				break;
			}

			return mbid;
		}
		catch(Throwable t)
		{
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

	private static String getSPTFId(AbstractCrawler.TYPE type, String artist, String release)
	{
		BufferedReader rd = null;
		try
		{
			String urlString = null;
			switch (type) {
			case REVIEW:
				urlString = "https://api.spotify.com/v1/search?q=album:" + release.trim().replace(" ", "%20") + "%20artist:" + artist.trim().replace(" ", "%20") + "&type=album";
				break;
			case MONOGRAPH:
				break;
			}

			String result = "";
			String line = null;
			HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
			rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = rd.readLine()) != null) 
			{
				result += line;
			}

			String sptfid = null;
			JsonNode json = new ObjectMapper().readTree(result);
			JsonNode items = json.get("albums").get("items");
			if(items.size() > 0)
			{
				sptfid = items.get(0).get("id").textValue();
			}

			return sptfid;
		}
		catch(Throwable t)
		{
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
