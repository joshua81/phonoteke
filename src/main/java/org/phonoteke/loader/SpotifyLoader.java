package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.common.collect.Lists;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SpotifyLoader
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyLoader.class);

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId("a6c3686d32cb48d4854d88915d3925be")
			.setClientSecret("46004c8b1a2b4c778cb9761ace300b6c")
			.setRedirectUri(SpotifyHttpManager.makeUri("https://phonoteke.org/spotify-redirect"))
			.build();
	private static final ClientCredentialsRequest SPOTIFY_LOGIN = SPOTIFY_API.clientCredentials().build();

	private static final List<String> SEPARATOR = Lists.newArrayList("-", "–");
	private static final int SLEEP_TIME = 2000;
	private static final int THRESHOLD = 90;

	private static ClientCredentials credentials;


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
				LOGGER.info("SPTF Expires in: " + credentials.getExpiresIn() + " secs");
			}
		} 
		catch (Exception e) 
		{
			LOGGER.error("Error connecting to Spotify: " + e.getMessage());
		}
	}

	public void loadAlbum(Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String album = page.getString("title");

		// check if the article was already crawled
		LOGGER.info("SPTF Loading album " + artist + " - " + album);
		Document spotify = getAlbum(artist, album);
		if(spotify != null)
		{
			String artistId = spotify.getString("spartistid");
			String albumId = spotify.getString("spalbumid");
			LOGGER.info("SPTF " + artist + " - " + album + ": " + artistId + " - " + albumId);

			page.append("spartistid", artistId).
			append("spalbumid", albumId).
			append("spcover-l", spotify.getString("coverL")).
			append("spcover-m", spotify.getString("coverM")).
			append("spcover-s", spotify.getString("coverS"));
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
					int score = FuzzySearch.tokenSortRatio(artist + " " + album, spartist + " " + spalbum);
					if(score > THRESHOLD)
					{
						return new Document("spartistid", artistid).
								append("spalbumid", albumId).
								append("spcover-l", a.getImages()[0].getUrl()).
								append("spcover-m", a.getImages()[1].getUrl()).
								append("spcover-s", a.getImages()[2].getUrl());
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

	public void loadArtist(Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");

		LOGGER.info("SPTF Loading artist " + artist);
		Document spotify = getArtist(artist);
		if(spotify != null)
		{
			String artistId = spotify.getString("spartistid");
			LOGGER.info("SPTF " + artist + ": " + artistId);

			page.append("spartistid", artistId).
			append("spcover-l", spotify.getString("coverL")).
			append("spcover-m", spotify.getString("coverM")).
			append("spcover-s", spotify.getString("coverS"));
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
				int score = FuzzySearch.tokenSortRatio(artist, spartist);
				if(score > THRESHOLD)
				{
					return new Document("spartistid", artistid).
							append("spcover-l", a.getImages()[0].getUrl()).
							append("spcover-m", a.getImages()[1].getUrl()).
							append("spcover-s", a.getImages()[2].getUrl());
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

	public void loadTracks(Document page)
	{
		String id = page.getString("id");

		List<org.bson.Document> tracks = page.get("tracks", List.class);
		if(CollectionUtils.isNotEmpty(tracks))
		{
			for(org.bson.Document track : tracks)
			{
				String title = track.getString("title");
				LOGGER.info("SPTF Loading track " + title);
				Document spotify = getTrack(title);
				if(spotify != null)
				{
					String trackId = spotify.getString("sptrackid");
					LOGGER.info("SPTF " + title + ": " + trackId);

					track.append("sptrackid", trackId).
					append("spcover-l", spotify.getString("coverL")).
					append("spcover-m", spotify.getString("coverM")).
					append("spcover-s", spotify.getString("coverS"));
				}
			}
		}
	}

	private Document getTrack(String title)
	{
		try
		{
			login();
			for(String s : SEPARATOR)
			{
				if(title.split(s).length == 2)
				{
					// artist - song
					String artist = title.split(s)[0];
					String song = title.split(s)[1];
					String q = "artist:" + artist + " track: " + song;
					SearchTracksRequest request = SPOTIFY_API.searchTracks(q).build();
					Paging<Track> tracks = request.execute();
					for(int i = 0; i < tracks.getItems().length; i++)
					{
						Track track = tracks.getItems()[i];
						String spartist = track.getArtists()[0].getName();
						String spsong = track.getName();
						String trackid = track.getId();
						int score = FuzzySearch.tokenSortRatio(artist + " " + song, spartist + " " + spsong);
						if(score > THRESHOLD)
						{
							LOGGER.info(title + ": " + spartist + " - " + spsong + " (" + trackid + ")");
							return new Document("sptrackid", trackid).
									append("spcover-l", track.getAlbum().getImages()[0].getUrl()).
									append("spcover-m", track.getAlbum().getImages()[1].getUrl()).
									append("spcover-s", track.getAlbum().getImages()[2].getUrl());
						}
					}

					// song - artist
					artist = title.split(s)[1];
					song = title.split(s)[0];
					q = "artist:" + artist + " track: " + song;
					request = SPOTIFY_API.searchTracks(q).build();
					tracks = request.execute();
					for(int i = 0; i < tracks.getItems().length; i++)
					{
						Track track = tracks.getItems()[i];
						String spartist = track.getArtists()[0].getName();
						String spsong = track.getName();
						String trackid = track.getId();
						int score = FuzzySearch.tokenSortRatio(artist + " " + song, spartist + " " + spsong);
						if(score > THRESHOLD)
						{
							LOGGER.info(title + ": " + spartist + " - " + spsong + " (" + trackid + ")");
							return new Document("sptrackid", trackid).
									append("spcover-l", track.getAlbum().getImages()[0].getUrl()).
									append("spcover-m", track.getAlbum().getImages()[1].getUrl()).
									append("spcover-s", track.getAlbum().getImages()[2].getUrl());
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error loading " + title + ": " + e.getMessage(), e);
			relogin();
		}
		return null;
	}
}
