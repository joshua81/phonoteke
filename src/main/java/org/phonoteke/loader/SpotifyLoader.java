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
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.enums.AlbumType;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Image;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.ReplacePlaylistsItemsRequest;
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
	private static final String VA = "Artisti Vari";

	private static ClientCredentials credentials;


	public static void main(String[] args)
	{
		new SpotifyLoader().load("d67a665575ca0d012dc45d2dada0edd52d2c241af797a916c57bc7e9529567a5");
		new SpotifyLoader().load();
		new SpotifyLoader().loadPlaylists(true);
	}

	private void loadPlaylists(boolean replace)
	{
		LOGGER.info("Loading Spotify Playlists...");
		MongoCursor<Document> i = replace ? docs.find(Filters.and(Filters.eq("type", "podcast"))).noCursorTimeout(true).iterator() : 
			docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("spalbumid", null))).noCursorTimeout(true).iterator(); 
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			createPlaylist(page);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
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
				String id = page.getString("spalbumid");
				// create new playlist
				if(id == null) {
					String date = new SimpleDateFormat("dd-MM-yyyy").format(page.getDate("date"));
					String title = page.getString("artist") + " del " + date;
					String description = page.getString("title");
					try
					{
						CreatePlaylistRequest playlistRequest = PLAYLIST_API.createPlaylist(SPOTIFY_USER, title).description(description).public_(true).build();
						Playlist playlist = playlistRequest.execute();
						AddItemsToPlaylistRequest itemsRequest = PLAYLIST_API.addItemsToPlaylist(playlist.getId(), Arrays.copyOf(uris.toArray(), uris.size(), String[].class)).build();
						itemsRequest.execute();
						page.append("spalbumid", playlist.getId());
						LOGGER.info("Playlist: " + playlist.getName() + " spotify: " + playlist.getId() + " created");
					}
					catch (Exception e) 
					{
						LOGGER.error("ERROR creating playlist " + title + ": " + e.getMessage());
					}
				}
				// replace existing playlist
				else {
					try {
						ReplacePlaylistsItemsRequest req = PLAYLIST_API.replacePlaylistsItems(id, Arrays.copyOf(uris.toArray(), uris.size(), String[].class)).build();
						req.execute();
						LOGGER.info("Playlist " + id + " recreated");
					}
					catch (Exception e) 
					{
						LOGGER.error("ERROR r playlist " + id + ": " + e.getMessage());
					}
				}
			}
		}
	}

	private void load()
	{
		load(null);
	}

	private void load(String id)
	{
		LOGGER.info("Loading Spotify...");
		MongoCursor<Document> i = id != null ? docs.find(Filters.eq("id", id)).noCursorTimeout(true).iterator() :
			docs.find(Filters.or(Filters.and(Filters.ne("type", "podcast"), Filters.eq("spartistid", null)), 
					Filters.and(Filters.eq("type", "podcast"), Filters.eq("tracks.spotify", null)))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			id = page.getString("id"); 
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
			append("coverS", spotify.getString("coverS")).
			append("score", spotify.getInteger("score"));
		}
		else
		{
			page.append("spartistid", NA).
			append("spalbumid", NA).
			append("score", 0);
		}
	}

	private Document loadAlbum(String artist, String album)
	{
		TreeMap<Integer, Document> albumsMap = Maps.newTreeMap();
		try
		{
			login();
			//			String q = "artist:"+ artist + " album:" + album;
			String q = artist + " " + album;
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
					String albumid = a.getId();
					int score = FuzzySearch.tokenSortRatio(artist + " " + album, spartist + " " + spalbum);
					Document page = new Document("spartistid", artistid).append("spalbumid", albumid);
					page.append("score", score);
					getImages(page, a.getImages());

					if(!VA.equalsIgnoreCase(a.getArtists()[0].getName()) && !AlbumType.COMPILATION.equals(a.getAlbumType()) && !albumsMap.containsKey(score)) {
						LOGGER.info(artist + " - " + album + " | " + spartist + " - " + spalbum + ": " + score);
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
			append("coverS", spotify.getString("coverS")).
			append("score", spotify.getInteger("score"));
		}
		else
		{
			page.append("spartistid", NA).
			append("score", 0);
		}
	}

	private Document loadArtist(String artist)
	{
		TreeMap<Integer, Document> artistsMap = Maps.newTreeMap();
		try
		{
			login();
			//			String q = "artist:" + artist;
			String q = artist;
			SearchArtistsRequest request = SPOTIFY_API.searchArtists(q).build();
			Paging<Artist> artists = request.execute();
			for(int j = 0; j < artists.getItems().length; j++)
			{
				Artist a = artists.getItems()[j];
				String spartist = a.getName();
				String artistid = a.getId();
				int score = FuzzySearch.tokenSortRatio(artist, spartist);
				Document page = new Document("spartistid", artistid);
				page.append("score", score);
				getImages(page, a.getImages());

				if(!artistsMap.containsKey(score)) {
					LOGGER.info(artist + " | " + spartist + ": " + score);
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
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		if(CollectionUtils.isNotEmpty(tracks))
		{
			int score = 0;
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
							append("coverS", spotify.getString("coverS")).
							append("title", spotify.getString("artist") + " - " + spotify.getString("track")).
							append("score", spotify.getInteger("score"));
						}
						else
						{
							track.append("spotify", NA).
							append("score", 0);
						}
					}
					catch (Exception e) 
					{
						LOGGER.error("ERROR loading " + title + ": " + e.getMessage());
						relogin();
					}
				}
				score += track.getInteger("score", 0);
			}
			score = score/tracks.size();
			page.append("score", score);
		}
	}

	private org.bson.Document getTrack(String title) throws Exception
	{
		List<String> chunks = Lists.newArrayList();
		for(String match : TRACKS_MATCH) {
			Matcher m = Pattern.compile(match).matcher(title);
			if(m.matches()) {
				for(int j=1; j<= m.groupCount(); j++){
					chunks.add(m.group(j));
				}
				break;
			}
		}

		if(chunks.size() >= 2) {
			String artist = chunks.get(0);
			artist = artist.replaceAll("/", " ");
			artist = artist.replaceAll("&", " ");
			artist = artist.replaceAll("\\+", " ");
			artist = artist.replaceAll(",", " ");
			artist = artist.replaceAll("=", " ");
			artist = artist.replaceAll(";", " ");
			for(String match : FEAT_MATCH) {
				Matcher m = Pattern.compile(match).matcher(artist);
				if(m.matches()) {
					artist = m.group(1);
					break;
				}
			}
			for(String match : YEAR_MATCH) {
				Matcher m = Pattern.compile(match).matcher(artist);
				if(m.matches()) {
					artist = m.group(1);
					break;
				}
			}

			String song = chunks.get(1);
			song = song.replaceAll("/", " ");
			song = song.replaceAll("&", " ");
			song = song.replaceAll("\\+", " ");
			song = song.replaceAll(",", " ");
			song = song.replaceAll("=", " ");
			song = song.replaceAll(";", " ");
			for(String match : FEAT_MATCH) {
				Matcher m = Pattern.compile(match).matcher(song);
				if(m.matches()) {
					song = m.group(1);
					break;
				}
			}
			for(String match : YEAR_MATCH) {
				Matcher m = Pattern.compile(match).matcher(song);
				if(m.matches()) {
					song = m.group(1);
					break;
				}
			}

			LOGGER.info(artist + " - " + song);
			TreeMap<Integer, Document> tracksMap = loadTrack(artist, song);
			return tracksMap.isEmpty() ?  null : tracksMap.descendingMap().firstEntry().getValue();
		}
		LOGGER.info(title + " not found");
		return null;
	}

	private TreeMap<Integer, Document> loadTrack(String artist, String song) throws Exception
	{
		TreeMap<Integer, Document> tracksMap = Maps.newTreeMap();
		if(StringUtils.isNotBlank(artist) && StringUtils.isNotBlank(song))
		{
			artist = artist.trim();
			song = song.trim();
			//			String q = "artist:" + artist + " track: " + song;
			String q = artist + " " + song;
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
				Document page = new Document("spotify", trackid);
				page.append("artist", spartist);
				page.append("spartistid", spartistid);
				page.append("album", spalbum);
				page.append("spalbumid", spalbumid);
				page.append("track", spsong);
				page.append("score", score);
				getImages(page, track.getAlbum().getImages());

				if(!VA.equalsIgnoreCase(track.getAlbum().getArtists()[0].getName()) && !AlbumType.COMPILATION.equals(track.getAlbum().getAlbumType()) && !tracksMap.containsKey(score)) {
					LOGGER.info(artist + " - " + song + " | " + spartist + " - " + spsong + ": " + score);
					tracksMap.put(score, page);
				}
			}
		}
		return tracksMap;
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
