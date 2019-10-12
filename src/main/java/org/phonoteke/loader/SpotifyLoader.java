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

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SpotifyLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyLoader.class);

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId("a6c3686d32cb48d4854d88915d3925be")
			.setClientSecret("46004c8b1a2b4c778cb9761ace300b6c")
			.setRedirectUri(SpotifyHttpManager.makeUri("https://phonoteke.org/spotify-redirect"))
			.build();
	private static final ClientCredentialsRequest SPOTIFY_LOGIN = SPOTIFY_API.clientCredentials().build();

	private static final int SLEEP_TIME = 2000;

	private static ClientCredentials credentials;


	public static void main(String[] args) 
	{
		new SpotifyLoader().loadAlbums();
		new SpotifyLoader().loadArtists();
	}

	public SpotifyLoader()
	{
		super();
		beforeStart();
	}

	private void beforeStart()
	{
		// does nothing
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
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.ALBUM.name().toLowerCase()), Filters.eq("spalbumid", null))).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			Document page = i.next();
			String id = page.getString("id");
			String artist = page.getString("artist");
			String album = page.getString("title");

			// check if the article was already crawled
			Document spotify = getAlbum(artist, album);
			if(spotify != null)
			{
				String artistId = spotify.getString("spartistid");
				String albumId = spotify.getString("spalbumid");
				LOGGER.info("SPTF " + artist + " - " + album + ": " + artistId + " - " + albumId);

				page.append("spartistid", artistId).
				append("spalbumid", albumId).
				append("coverL", spotify.getString("coverL")).
				append("coverM", spotify.getString("coverM")).
				append("coverS", spotify.getString("coverS"));
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
	}

	private Document getAlbum(String artist, String album)
	{
		try
		{
			login();
			String q = "artist:"+ artist + " album:" + album;
			SearchAlbumsRequest request = SPOTIFY_API.searchAlbums(q).build();
			Paging<AlbumSimplified> albums = request.execute();
			for(int j = 0; j < albums.getItems().length; j++)
			{
				AlbumSimplified a = albums.getItems()[j];
				ArtistSimplified[] artists = a.getArtists();
				for(int k = 0; k < artists.length; k++)
				{
					String spartist = artists[k].getName();
					String artistid = artists[k].getId();
					String spalbum = a.getName();
					String albumId = a.getId();
					int score = FuzzySearch.tokenSetRatio(artist + " " + album, spartist + " " + spalbum);
					if(score > 90)
					{
						return new Document("spartistid", artistid).
								append("spalbumid", albumId).
								append("coverL", a.getImages()[0].getUrl()).
								append("coverM", a.getImages()[1].getUrl()).
								append("coverS", a.getImages()[2].getUrl());
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error loading " + artist + " - " + album + ": " + e.getMessage(), e);
		}
		return null;
	}

	private void loadArtists()
	{
		MongoCursor<Document> i = docs.find(Filters.and(Filters.ne("type", TYPE.ALBUM.name().toLowerCase()), Filters.eq("spalbumid", null))).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			Document page = i.next();
			String id = page.getString("id");
			String artist = page.getString("artist");

			Document spotify = getArtist(artist);
			if(spotify != null)
			{
				String artistId = spotify.getString("spartistid");
				LOGGER.info("SPTF " + artist + ": " + artistId);

				page.append("spartistid", artistId).
				append("coverL", spotify.getString("coverL")).
				append("coverM", spotify.getString("coverM")).
				append("coverS", spotify.getString("coverS"));
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
	}

	private Document getArtist(String artist)
	{
		try
		{
			login();
			String q = "artist:" + artist;
			SearchArtistsRequest request = SPOTIFY_API.searchArtists(q).build();
			Paging<Artist> artists = request.execute();
			for(int j = 0; j < artists.getItems().length; j++)
			{
				Artist a = artists.getItems()[j];
				String spartist = a.getName();
				String artistid = a.getId();
				int score = FuzzySearch.tokenSetRatio(artist, spartist);
				if(score > 90)
				{
					return new Document("spartistid", artistid).
							append("coverL", a.getImages()[0].getUrl()).
							append("coverM", a.getImages()[1].getUrl()).
							append("coverS", a.getImages()[2].getUrl());
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error loading " + artist + ": " + e.getMessage(), e);
		}
		return null;
	}
}
