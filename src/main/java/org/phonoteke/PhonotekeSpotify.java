package org.phonoteke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;

public class PhonotekeSpotify {

	protected static final int THREAD_SLEEP = 3000;
	private static final Logger LOGGER = LogManager.getLogger(PhonotekeLoader.class);

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "phonoteke";

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId("a6c3686d32cb48d4854d88915d3925be")
			.setClientSecret("3294564c84e54285adeee3e05caf4b29")
			.setRedirectUri(SpotifyHttpManager.makeUri("https://phonoteke.org/spotify-redirect"))
			.build();

	private DBCollection articles;


	public static void main(String[] args) {
		new PhonotekeSpotify().getSpotifyIds();
	}

	public PhonotekeSpotify()
	{
		try 
		{
			DB db = new MongoClient(MONGO_HOST, MONGO_PORT).getDB(MONGO_DB);
			articles = db.getCollection("articles");
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error connecting to Mongo db: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void getSpotifyIds()
	{
		try 
		{
			ClientCredentialsRequest credentialsRequest = SPOTIFY_API.clientCredentials().build();
			ClientCredentials credentials = credentialsRequest.execute();
			SPOTIFY_API.setAccessToken(credentials.getAccessToken());
			LOGGER.info("Expires in: " + credentials.getExpiresIn());

			DBCursor i = articles.find(BasicDBObjectBuilder.start().add("spotify", null).add("type", "REVIEW").get());
			while(i.hasNext())
			{
				DBObject page = i.next();
				String band = (String)page.get("band");
				String album = (String)page.get("album");

				if(credentials.getExpiresIn() < 5)
				{
					credentials = credentialsRequest.execute();
					SPOTIFY_API.setAccessToken(credentials.getAccessToken());
					LOGGER.info("Expires in: " + credentials.getExpiresIn() + " secs");
				}
				String id = getAlbumId(band, album);
				LOGGER.info(band + " - " + album + ": " + id);
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error closing BufferedReader: " + e.getMessage());
		}
	}

	private String getAlbumId(String band, String album)
	{
		try
		{
			SearchAlbumsRequest request = SPOTIFY_API.searchAlbums(album.trim().replace(" ", "+")).build();
			Paging<AlbumSimplified> albums = request.execute();
			for(int j = 0; j < albums.getItems().length; j++)
			{
				AlbumSimplified a = albums.getItems()[j];
				ArtistSimplified[] artists = a.getArtists();
				for(int k = 0; k < artists.length; k++)
				{
					ArtistSimplified artist = artists[k];
					if(artist.getName().toLowerCase().replace(" ", "").trim().equals(band.toLowerCase().replace(" ", "").trim()))
					{
						return a.getId();
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error getting " + band + " - " + album + "  id: " + e.getMessage());
		}
		return null;
	}
}
