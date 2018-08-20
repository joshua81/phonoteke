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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PhonotekeSpotify {

	protected static final int THREAD_SLEEP = 3000;
	protected static final Logger LOGGER = LogManager.getLogger(PhonotekeSpotify.class.getName());

	protected static final String SQL_FIND_ALBUMS = "SELECT * FROM musicdb.document WHERE albumIdSptf IS NULL AND type = 'REVIEW' ORDER BY creation_date DESC";
	protected static final String SQL_SET_SPOTIFY_ALBUMID = "UPDATE musicdb.document SET albumIdSptf = ? WHERE id = ?";

	protected static Connection db = null;
	protected static PreparedStatement queryFindAlbums = null;
	protected static PreparedStatement querySetSpotifyAlbumId = null;

	static 
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			db = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/musicdb", "musicdb", "musicdb");
			queryFindAlbums = db.prepareStatement(SQL_FIND_ALBUMS);
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
			ResultSet docs = queryFindAlbums.executeQuery();
			while(docs.next())
			{
				String id = docs.getString("id");
				String artist = docs.getString("band");
				String release = docs.getString("album");

				// Spotify
				String sptfAlbumId = docs.getString("albumIdSptf");
				if(sptfAlbumId == null)
				{
					String sptfid = getSpotifyAlbumId(artist, release);
					if(sptfid == null)
					{
						LOGGER.info(artist + " - " + release + ": SPTFid not found");
					}
					else
					{
						querySetSpotifyAlbumId.setString(1, sptfid);
						querySetSpotifyAlbumId.setString(2, id);
						querySetSpotifyAlbumId.executeUpdate();
						LOGGER.info(artist + " - " + release + ": SPTFid " + sptfid);
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

	private static String getSpotifyAlbumId(String artist, String release)
	{
		BufferedReader rd = null;
		try
		{
			String urlString = "https://api.spotify.com/v1/search?q=album:" + release.trim().replace(" ", "%20") + "%20artist:" + artist.trim().replace(" ", "%20") + "&type=album";
			String result = "";
			String line = null;
			HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
			rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			while ((line = rd.readLine()) != null) 
			{
				result += line;
			}

			List<String> ids = new ArrayList<String>();
			JsonNode json = new ObjectMapper().readTree(result);
			JsonNode items = json.get("albums").get("items");
			for(int i = 0; i < items.size(); i++)
			{
				if(release.replace(" ", "").toUpperCase().equals(items.get(i).get("name").textValue().replace(" ", "").toUpperCase()))
				{
					ids.add(items.get(i).get("id").textValue());
				}
			}

			return ids.size() > 0 ? ids.get(0) : null;
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
