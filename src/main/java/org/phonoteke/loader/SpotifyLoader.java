package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SpotifyLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyLoader.class);

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId("a6c3686d32cb48d4854d88915d3925be")
			.setClientSecret("46004c8b1a2b4c778cb9761ace300b6c")
			.setRedirectUri(SpotifyHttpManager.makeUri("https://humanbeats.appspot.com/"))
			.build();
	private static final ClientCredentialsRequest SPOTIFY_LOGIN = SPOTIFY_API.clientCredentials().build();

	private static ClientCredentials credentials;


	public static void main(String[] args)
	{
		new SpotifyLoader().load();
	}

	private void load()
	{
		LOGGER.info("Loading Spotify...");
		for(int j = 0; j < 20; j++)
		{
			MongoCursor<Document> i = docs.find(Filters.or(
					Filters.and(Filters.ne("type", "podcast"), Filters.or(Filters.exists("spartistid", false),Filters.eq("spartistid", null))), 
					Filters.and(Filters.eq("type", "podcast"), Filters.or(Filters.exists("tracks.spotify", false), Filters.eq("tracks.spotify", null))))).
					sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).skip(j*1000).limit(1000).noCursorTimeout(true).iterator(); 
			while(i.hasNext()) 
			{ 
				Document page = i.next();
				String id = page.getString("id"); 
				String type = page.getString("type");
				if("album".equals(type))
				{
					loadAlbum(page);
				}
				else if("podcast".equals(type))
				{
					loadTracks(page);
				}
				else
				{
					loadArtist(page);
				}
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
			}
		}
	}

	private void relogin()
	{
		credentials = null;
		login();
	}

	private void login()
	{
		try 
		{
			Thread.sleep(SLEEP_TIME);
			if(credentials == null)
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

	private void loadAlbum(Document page)
	{
		String artist = page.getString("artist");
		String album = page.getString("title");

		// check if the article was already crawled
		LOGGER.debug("Loading album " + artist + " - " + album);
		Document spotify = loadAlbum(artist, album);
		if(spotify != null)
		{
			String artistId = spotify.getString("spartistid");
			String albumId = spotify.getString("spalbumid");
			page.append("spartistid", artistId).
			append("spalbumid", albumId).
			append("coverL", spotify.getString("coverL")).
			append("coverM", spotify.getString("coverM")).
			append("coverS", spotify.getString("coverS"));
		}
		else
		{
			page.append("spartistid", NA).
			append("spalbumid", NA);
		}
	}

	private Document loadAlbum(String artist, String album)
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
					int score = FuzzySearch.tokenSortRatio(artist + " " + album, spartist + " " + spalbum);
					LOGGER.info("album: " + artist + " " + album + ", spotify: " + spartist + " " + spalbum + " score " + score);
					if(score >= THRESHOLD)
					{
						LOGGER.info(artist + " " + album + ": " + spartist + " " + spalbum + " (" + albumId + ")");
						Document page = new Document("spartistid", artistid).append("spalbumid", albumId);
						getImages(page, a.getImages());
						return page;
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error loading " + artist + " - " + album + ": " + e.getMessage(), e);
			relogin();
		}
		return null;
	}

	private void loadArtist(Document page)
	{
		String artist = page.getString("artist");

		LOGGER.debug("Loading artist " + artist);
		Document spotify = loadArtist(artist);
		if(spotify != null)
		{
			String artistId = spotify.getString("spartistid");
			page.append("spartistid", artistId).
			append("coverL", spotify.getString("coverL")).
			append("coverM", spotify.getString("coverM")).
			append("coverS", spotify.getString("coverS"));
		}
		else
		{
			page.append("spartistid", NA);
		}
	}

	private Document loadArtist(String artist)
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
				int score = FuzzySearch.tokenSortRatio(artist, spartist);
				LOGGER.info("artist: " + artist + ", spotify: " + spartist + " score; " + score);
				if(score >= THRESHOLD)
				{
					LOGGER.info(artist + ": " + spartist + " (" + artistid + ")");
					Document page = new Document("spartistid", artistid);
					getImages(page, a.getImages());
					return page;
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error loading " + artist + ": " + e.getMessage(), e);
			relogin();
		}
		return null;
	}

	private void loadTracks(Document page)
	{
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		if(CollectionUtils.isNotEmpty(tracks))
		{
			for(org.bson.Document track : tracks)
			{
				String title = track.getString("title");
				String spotifyId = track.getString("spotify");
				if(spotifyId == null)
				{
					LOGGER.debug("Loading track " + title);
					try
					{
						login();
						Document spotify = getTrack(title);
						if(spotify != null)
						{
							track.append("spotify", spotify.getString("spotify")).
							append("artist", spotify.getString("artist")).
							append("album", spotify.getString("album")).
							append("track", spotify.getString("track")).
							append("spartistid", spotify.getString("spartistid")).
							append("spalbumid", spotify.getString("spalbumid")).
							append("coverL", spotify.getString("coverL")).
							append("coverM", spotify.getString("coverM")).
							append("coverS", spotify.getString("coverS"));
						}
						else
						{
							track.append("spotify", NA);
						}
					}
					catch (Exception e) 
					{
						LOGGER.error("Error loading " + title + ": " + e.getMessage(), e);
						relogin();
					}
				}
			}
		}
	}

	protected Document getTrack(String artist, String song) throws Exception
	{
		if(StringUtils.isNotBlank(artist) && StringUtils.isNotBlank(song))
		{
			String q = "artist:" + artist + " track: " + song;
			SearchTracksRequest request = SPOTIFY_API.searchTracks(q).build();
			Paging<Track> tracks = request.execute();
			for(int i = 0; i < tracks.getItems().length; i++)
			{
				Track track = tracks.getItems()[i];
				String spartist = track.getArtists()[0].getName();
				String spartistid = track.getArtists()[0].getId();
				String spalbum = track.getAlbum().getName();
				String spalbumid = track.getAlbum().getId();
				String spsong = track.getName();
				String trackid = track.getId();
				int score = FuzzySearch.tokenSortRatio(artist + " " + song, spartist + " " + spsong);
				LOGGER.info(artist + " - " + song + ": " + spartist + " - " + spsong + " (score: " + score + ")");
				if(score >= THRESHOLD)
				{
					LOGGER.info(artist + " - " + song + ": " + spartist + " - " + spsong + " (" + trackid + ")");
					Document page = new Document("spotify", trackid);
					page.append("artist", spartist);
					page.append("spartistid", spartistid);
					page.append("album", spalbum);
					page.append("spalbumid", spalbumid);
					page.append("track", spsong);
					getImages(page, track.getAlbum().getImages());
					return page;
				}
			}
		}
		return null;
	}

	private void getImages(Document page, Image[] images)
	{
		if(images != null)
		{
			for(int i = 0; i < images.length; i++)
			{
				Image image = images[i];
				String size = i == 0 ? "L" : i == 1 ? "M" : "S";
				page.append("cover" + size, image.getUrl());
			}
		}
	}
}
