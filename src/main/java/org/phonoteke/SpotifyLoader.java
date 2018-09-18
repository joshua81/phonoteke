package org.phonoteke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;

public class SpotifyLoader 
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyLoader.class);

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId("a6c3686d32cb48d4854d88915d3925be")
			.setClientSecret("3294564c84e54285adeee3e05caf4b29")
			.setRedirectUri(SpotifyHttpManager.makeUri("https://phonoteke.org/spotify-redirect"))
			.build();
	private static final ClientCredentialsRequest SPOTIFY_LOGIN = SPOTIFY_API.clientCredentials().build();

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "phonoteke";

	private DBCollection spotify;
	private DBCollection articles;

	private ClientCredentials credentials;

	public static void main(String[] args) 
	{
		SpotifyLoader loader = new SpotifyLoader();
		loader.loadAlbums();
		loader.loadArtists();
	}

	public SpotifyLoader()
	{
		try 
		{
			DB db = new MongoClient(MONGO_HOST, MONGO_PORT).getDB(MONGO_DB);
			spotify = db.getCollection("spotify");
			articles = db.getCollection("articles");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	private void login()
	{
		try 
		{
			if(credentials == null || credentials.getExpiresIn() < 5)
			{
				credentials = SPOTIFY_LOGIN.execute();
				SPOTIFY_API.setAccessToken(credentials.getAccessToken());
				LOGGER.info("Expires in: " + credentials.getExpiresIn() + " secs");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error connecting to Spotify: " + e.getMessage());
		}
	}

	private void loadAlbums()
	{
		DBCursor i = articles.find(BasicDBObjectBuilder.start().add("type", PhonotekeLoader.TYPE.REVIEW.name()).get());
		i.setOptions(Bytes.QUERYOPTION_NOTIMEOUT);
		int total = i.count();
		int j = 1;
		while(i.hasNext())
		{
			DBObject page = i.next();
			String band = (String)page.get("band");
			String album = (String)page.get("album");
			String id = (String)page.get("id");
			String spotifyId = null;

			// check if the article was already crawled
			DBObject spotifyDB = spotify.findOne(BasicDBObjectBuilder.start().add("id", id).get());
			if(spotifyDB == null)
			{
				DBObject json = getAlbum(band, album);
				if(json != null)
				{
					spotifyId = (String)json.get("album");
					
					// insert SPOTIFY
					json.put("id", id);
					spotify.insert(json);
				}
			}
			LOGGER.info(j++ + "/" + total + " Album " + band + " - " + album + ": " + spotifyId);
		}
	}
	
	private void loadArtists()
	{
		DBCursor i = articles.find(BasicDBObjectBuilder.start().add("type", PhonotekeLoader.TYPE.MONOGRAPH.name()).get());
		i.setOptions(Bytes.QUERYOPTION_NOTIMEOUT);
		int total = i.count();
		int j = 1;
		while(i.hasNext())
		{
			DBObject page = i.next();
			String band = (String)page.get("band");
			String id = (String)page.get("id");
			String spotifyId = null;

			// check if the article was already crawled
			DBObject spotifyDB = spotify.findOne(BasicDBObjectBuilder.start().add("id", id).get());
			if(spotifyDB == null)
			{
				DBObject json = getArtist(band);
				if(json != null)
				{
					spotifyId = (String)json.get("artist");
					
					// insert SPOTIFY
					json.put("id", id);
					spotify.insert(json);
				}
			}
			LOGGER.info(j++ + "/" + total + " Artist " + band + " : " + spotifyId);
		}
	}

	private DBObject getAlbum(String band, String album)
	{
		try
		{
			login();

			Thread.sleep(500);
			String q =   "album:" + album + " artist:"+band;
			SearchAlbumsRequest request = SPOTIFY_API.searchAlbums(q).build();
			Paging<AlbumSimplified> albums = request.execute();
			for(int j = 0; j < albums.getItems().length; j++)
			{
				AlbumSimplified a = albums.getItems()[j];
				ArtistSimplified[] artists = a.getArtists();
				for(int k = 0; k < artists.length; k++)
				{
					ArtistSimplified artist = artists[k];
					LOGGER.info("Spotify: " + artist.getName() + " - " + a.getName());
					if(artist.getName().toLowerCase().replace(" ", "").trim().equals(band.toLowerCase().replace(" ", "").trim()))
					{
						return BasicDBObjectBuilder.start().
								add("artist", artist.getId()).
								add("album", a.getId()).
								add("imageL", a.getImages()[0].getUrl()).
								add("imageM", a.getImages()[1].getUrl()).
								add("imageS", a.getImages()[2].getUrl()).
								add("type", "album").get();
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
	
	private DBObject getArtist(String band)
	{
		try
		{
			login();

			Thread.sleep(500);
			String q = "artist:" + band;
			SearchArtistsRequest request = SPOTIFY_API.searchArtists(q).build();
			Paging<Artist> artists = request.execute();
			for(int j = 0; j < artists.getItems().length; j++)
			{
				Artist artist = artists.getItems()[j];
				LOGGER.info("Spotify: " + artist.getName());
				if(artist.getName().toLowerCase().replace(" ", "").trim().equals(band.toLowerCase().replace(" ", "").trim()))
				{
					return BasicDBObjectBuilder.start().
							add("artist", artist.getId()).
							add("imageL", artist.getImages()[0].getUrl()).
							add("imageM", artist.getImages()[1].getUrl()).
							add("imageS", artist.getImages()[2].getUrl()).
							add("type", "artist").get();
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error getting " + band + "  id: " + e.getMessage());
		}
		return null;
	}
	
	public DBObject getId(String id)
	{
		return spotify.findOne(BasicDBObjectBuilder.start().add("id", id).get());
	}
}
