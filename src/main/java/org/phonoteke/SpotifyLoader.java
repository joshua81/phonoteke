package org.phonoteke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.phonoteke.loader.OndarockLoader;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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

	private MongoCollection<Document> spotify;
	private MongoCollection<Document> articles;

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
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
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
		MongoCursor<Document> i = null;//articles.find(Filters.eq("type", OndarockLoader.TYPE.REVIEW.name())).iterator();
		//		i.setOptions(Bytes.QUERYOPTION_NOTIMEOUT);
		//		int total = i.count();
		int n = 1;
		while(i.hasNext())
		{
			Document page = i.next();
			String band = page.get("band", String.class);
			String album = page.get("album", String.class);
			String id = page.get("id", String.class);
			String spotifyId = null;

			// check if the article was already crawled
			MongoCursor<Document> j = spotify.find(Filters.eq("id", id)).iterator();
			if(!j.hasNext())
			{
				Document json = getAlbum(band, album);
				if(json != null)
				{
					spotifyId = json.get("album", String.class);
					// insert SPOTIFY
					json.put("id", id);
					spotify.insertOne(json);
				}
			}
			LOGGER.info(n++ + " Album " + band + " - " + album + ": " + spotifyId);
		}
	}

	private void loadArtists()
	{
		MongoCursor<Document> i = null;//articles.find(Filters.eq("type", OndarockLoader.TYPE.MONOGRAPH.name())).iterator();
		//		i.setOptions(Bytes.QUERYOPTION_NOTIMEOUT);
		//		int total = i.count();
		int n = 1;
		while(i.hasNext())
		{
			Document page = i.next();
			String band = page.get("band", String.class);
			String id = page.get("id", String.class);
			String spotifyId = null;

			// check if the article was already crawled
			MongoCursor<Document> j = spotify.find(Filters.eq("id", id)).iterator();
			if(!j.hasNext())
			{
				Document json = getArtist(band);
				if(json != null)
				{
					spotifyId = json.get("artist", String.class);
					// insert SPOTIFY
					json.put("id", id);
					spotify.insertOne(json);
				}
			}
			LOGGER.info(n++ + " Artist " + band + " : " + spotifyId);
		}
	}

	private Document getAlbum(String band, String album)
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
						return new Document("artist", artist.getId()).
								append("album", a.getId()).
								append("imageL", a.getImages()[0].getUrl()).
								append("imageM", a.getImages()[1].getUrl()).
								append("imageS", a.getImages()[2].getUrl()).
								append("type", "album");
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

	private Document getArtist(String band)
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
					return new Document("artist", artist.getId()).
							append("imageL", artist.getImages()[0].getUrl()).
							append("imageM", artist.getImages()[1].getUrl()).
							append("imageS", artist.getImages()[2].getUrl()).
							append("type", "artist");
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error getting " + band + "  id: " + e.getMessage());
		}
		return null;
	}

	public Document getId(String id)
	{
		MongoCursor<Document> i = spotify.find(Filters.eq("id", id)).iterator();
		return i.hasNext() ? i.next() : null;
	}
}
