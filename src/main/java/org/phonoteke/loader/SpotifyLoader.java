package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCursor;
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

public class SpotifyLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyLoader.class);

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId("a6c3686d32cb48d4854d88915d3925be")
			.setClientSecret("3294564c84e54285adeee3e05caf4b29")
			.setRedirectUri(SpotifyHttpManager.makeUri("https://phonoteke.org/spotify-redirect"))
			.build();
	private static final ClientCredentialsRequest SPOTIFY_LOGIN = SPOTIFY_API.clientCredentials().build();
	private static final int SLEEP_TIME = 2000;

	private static ClientCredentials credentials;

	public static void main(String[] args) 
	{
		new SpotifyLoader().loadAlbums();
		new SpotifyLoader().loadAlbums();
	}

	public SpotifyLoader()
	{
		super();
		beforeStart();
	}
	
	private void beforeStart()
	{
		
	}

	private void login()
	{
		try 
		{
			if(credentials == null || credentials.getExpiresIn() < 5)
			{
				credentials = SPOTIFY_LOGIN.execute();
				SPOTIFY_API.setAccessToken(credentials.getAccessToken());
				LOGGER.info("SPTF Expires in: " + credentials.getExpiresIn() + " secs");
			}
			else
			{
				Thread.sleep(SLEEP_TIME);
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error connecting to Spotify: " + e.getMessage());
		}
	}

	private void loadAlbums()
	{
		MongoCursor<Document> i = docs.find(Filters.eq("type", TYPE.ALBUM.name().toLowerCase())).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			Document page = i.next();
			String id = page.getString("id");
			String artist = page.getString("artist");
			String album = page.getString("title");
			String spotifyId = null;

			// check if the article was already crawled
			//			MongoCursor<Document> j = musicbrainz.find(Filters.eq("id", id)).noCursorTimeout(true).iterator();
			//			if(!j.hasNext())
			//			{
			Document json = getAlbum(artist, album);
			if(json != null)
			{
				spotifyId = json.getString("album");
				// insert SPOTIFY
				json.put("id", id);
				//					musicbrainz.insertOne(json);
			}
			//			}
			LOGGER.info("SPTF Album " + artist + " - " + album + ": " + spotifyId);
		}
	}

	private void loadArtists()
	{
		MongoCursor<Document> i = docs.find(Filters.eq("type", TYPE.ARTIST.name().toLowerCase())).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			Document page = i.next();
			String id = page.getString("id");
			String artist = page.getString("artist");
			String spotifyId = null;

			// check if the article was already crawled
			//			MongoCursor<Document> j = musicbrainz.find(Filters.eq("id", id)).iterator();
			//			if(!j.hasNext())
			//			{
			Document json = getArtist(artist);
			if(json != null)
			{
				spotifyId = json.getString("artist");
				// insert SPOTIFY
				json.put("id", id);
				//					musicbrainz.insertOne(json);
			}
			//			}
			LOGGER.info("SPTF Artist " + artist + " : " + spotifyId);
		}
	}

	private Document getAlbum(String band, String album)
	{
		try
		{
			login();
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
}
