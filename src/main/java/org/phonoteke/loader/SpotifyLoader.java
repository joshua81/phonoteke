package org.phonoteke.loader;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.gson.JsonArray;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.playlists.AddTracksToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.ReplacePlaylistsTracksRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SpotifyLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyLoader.class);

	private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder()
			.setClientId(System.getenv("SPOTIFY_CLIENT_ID"))
			.setClientSecret(System.getenv("SPOTIFY_CLIENT_SECRET"))
			.setRedirectUri(SpotifyHttpManager.makeUri(System.getenv("SPOTIFY_REDIRECT"))).build();
	private static final ClientCredentialsRequest SPOTIFY_LOGIN = SPOTIFY_API.clientCredentials().build();
	private static final SpotifyApi PLAYLIST_API = new SpotifyApi.Builder().setAccessToken(System.getenv("SPOTIFY_TOKEN")).build();
	private static final String SPOTIFY_USER = System.getenv("SPOTIFY_USER");

	private static ClientCredentials credentials;


	public static void main(String[] args)
	{
		//		new SpotifyLoader().load("b771b4dc8081520f7b43f3788b63dfc5a6f6587d7e68c38e26c9ae02ca8397bb");
		new SpotifyLoader().load();
		new SpotifyLoader().loadPlaylists();
		//		new SpotifyLoader().renamePlaylists();
	}

	private void loadPlaylists()
	{
		LOGGER.info("Loading Spotify Playlists...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("spalbumid", null))).noCursorTimeout(true).iterator(); 
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id"); 
			createPlaylist(page);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
		}
	}

	private void recreatePlaylists()
	{
		LOGGER.info("Recreating Spotify Playlists...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).noCursorTimeout(true).iterator(); 
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("spalbumid");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(id != null && CollectionUtils.isNotEmpty(tracks)) {
				try {
					JsonArray uris = new JsonArray();
					for(org.bson.Document track : tracks)
					{
						String spotify = track.getString("spotify");
						if(spotify != null && !NA.equals(spotify))
						{
							uris.add("spotify:track:" + spotify);
						}
					}
					ReplacePlaylistsTracksRequest req = PLAYLIST_API.replacePlaylistsTracks(SPOTIFY_USER, id, uris).build();
					String res = req.execute();
					LOGGER.info("Playlist " + id + " recreated");
				}
				catch (Exception e) 
				{
					LOGGER.error("ERROR r playlist " + id + ": " + e.getMessage());
				}
			}
		}
	}

	private void createPlaylist(Document page)
	{
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		if(CollectionUtils.isNotEmpty(tracks))
		{
			List<String> uris = Lists.newArrayList();
			for(org.bson.Document track : tracks)
			{
				String spotify = track.getString("spotify");
				if(spotify != null && !NA.equals(spotify))
				{
					uris.add("spotify:track:" + spotify);
				}
			}

			if(CollectionUtils.isNotEmpty(uris))
			{
				String date = new SimpleDateFormat("dd-MM-yyyy").format(page.getDate("date"));
				String title = page.getString("artist") + " del " + date;
				String description = page.getString("title");
				try
				{
					CreatePlaylistRequest playlistRequest = PLAYLIST_API.createPlaylist(SPOTIFY_USER, title).description(description).public_(true).build();
					Playlist playlist = playlistRequest.execute();
					System.out.println("Playlist: " + playlist.getName() + " spotify: " + playlist.getId() + " created");
					AddTracksToPlaylistRequest itemsRequest = PLAYLIST_API.addTracksToPlaylist(SPOTIFY_USER, playlist.getId(), Arrays.copyOf(uris.toArray(), uris.size(), String[].class)).build();
					itemsRequest.execute();
					page.append("spalbumid", playlist.getId());
				}
				catch (Exception e) 
				{
					LOGGER.error("ERROR creating playlist " + title + ": " + e.getMessage());
				}
			}
		}
	}

	private void load(String id)
	{
		LOGGER.info("Loading Spotify...");
		MongoCursor<Document> i = docs.find(Filters.eq("id", id)).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
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
			//docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
		}
	}

	private void load()
	{
		LOGGER.info("Loading Spotify...");
		MongoCursor<Document> i = docs.find(Filters.or(
				Filters.and(Filters.ne("type", "podcast"), Filters.eq("spartistid", null)), 
				Filters.and(Filters.eq("type", "podcast"), Filters.eq("tracks.spotify", null)))).noCursorTimeout(true).iterator();
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
			LOGGER.error("ERROR connecting to Spotify: " + e.getMessage());
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
		TreeMap<Integer, Document> albumsMap = Maps.newTreeMap();
		try
		{
			login();
			String q = "artist:"+ artist + " album:" + album;
			SearchAlbumsRequest request = SPOTIFY_API.searchAlbums(q).limit(5).build();
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
					String albumid = a.getId();
					int score = FuzzySearch.tokenSortRatio(artist + " " + album, spartist + " " + spalbum);
					LOGGER.info(artist + " - " + album + " | " + spartist + " - " + spalbum + ": " + score);
					Document page = new Document("spartistid", artistid).append("spalbumid", albumid);
					getImages(page, a.getImages());
					if(!albumsMap.containsKey(score)) {
						albumsMap.put(score, page);
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("ERROR loading " + artist + " - " + album + ": " + e.getMessage());
			relogin();
		}
		return albumsMap.isEmpty() ?  null : albumsMap.descendingMap().firstEntry().getValue();
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
		TreeMap<Integer, Document> artistsMap = Maps.newTreeMap();
		try
		{
			login();
			String q = "artist:" + artist;
			SearchArtistsRequest request = SPOTIFY_API.searchArtists(q).limit(5).build();
			Paging<Artist> artists = request.execute();
			for(int j = 0; j < artists.getItems().length; j++)
			{
				Artist a = artists.getItems()[j];
				String spartist = a.getName();
				String artistid = a.getId();
				int score = FuzzySearch.tokenSortRatio(artist, spartist);
				LOGGER.info(artist + " | " + spartist + ": " + score);
				Document page = new Document("spartistid", artistid);
				getImages(page, a.getImages());
				if(!artistsMap.containsKey(score)) {
					artistsMap.put(score, page);
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("ERROR loading " + artist + ": " + e.getMessage());
			relogin();
		}
		return artistsMap.isEmpty() ?  null : artistsMap.descendingMap().firstEntry().getValue();
	}

	private void loadTracks(Document page)
	{
		String source = page.getString("source");
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		if(CollectionUtils.isNotEmpty(tracks))
		{
			for(org.bson.Document track : tracks)
			{
				String title = track.getString("titleOrig");
				String spotifyId = track.getString("spotify");
				if(spotifyId == null)
				{
					LOGGER.debug("Loading track " + title);
					try
					{
						login();
						Document spotify = getTrack(title, source);
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
						LOGGER.error("ERROR loading " + title + ": " + e.getMessage());
						relogin();
					}
				}
			}
		}
	}

	private org.bson.Document getTrack(String title, String source) throws Exception
	{
		List<String> chunks = Lists.newArrayList();
		for(String match : TRACKS_MATCH)
		{
			Pattern p = Pattern.compile(match);
			Matcher m = p.matcher(title);
			if(m.matches()) {
				for(int j=1; j<= m.groupCount(); j++){
					chunks.add(m.group(j));
				}
				break;
			}
		}

		// artist - song
		if(chunks.size() >= 2) {
			LOGGER.info(chunks.get(0) + " - " + chunks.get(1));
			org.bson.Document track = loadTrack(chunks.get(0), chunks.get(1));
			if(track != null)
			{
				return track;
			}
			// song - artist
			track = loadTrack(chunks.get(1), chunks.get(0));
			if(track != null)
			{
				return track;
			}
			LOGGER.info(chunks.get(0) + " - " + chunks.get(1) + " not found");
		}
		return null;
	}

	private Document loadTrack(String artist, String song) throws Exception
	{
		TreeMap<Integer, Document> tracksMap = Maps.newTreeMap();
		if(StringUtils.isNotBlank(artist) && StringUtils.isNotBlank(song))
		{
			artist = artist.trim();
			song = song.trim();
			String q = "artist:" + artist + " track: " + song;
			SearchTracksRequest request = SPOTIFY_API.searchTracks(q).limit(5).build();
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
				LOGGER.info(artist + " - " + song + " | " + spartist + " - " + spsong + ": " + score);
				Document page = new Document("spotify", trackid);
				page.append("artist", spartist);
				page.append("spartistid", spartistid);
				page.append("album", spalbum);
				page.append("spalbumid", spalbumid);
				page.append("track", spsong);
				getImages(page, track.getAlbum().getImages());
				if(!tracksMap.containsKey(score)) {
					tracksMap.put(score, page);
				}
			}
		}
		return tracksMap.isEmpty() ?  null : tracksMap.descendingMap().firstEntry().getValue();
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
