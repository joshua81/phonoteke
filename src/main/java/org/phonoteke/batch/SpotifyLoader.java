package org.phonoteke.batch;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.phonoteke.batch.model.Doc;
import org.phonoteke.batch.model.Stat;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neovisionaries.i18n.CountryCode;
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
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.follow.FollowPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.ReplacePlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchAlbumsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchArtistsRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;

import me.xdrop.fuzzywuzzy.FuzzySearch;

@Component
public class SpotifyLoader extends HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyLoader.class);
	private static final String SPOTIFY_USER = System.getenv("SPOTIFY_USER");

	private ClientCredentials credentials;
	private SpotifyApi spotify = new SpotifyApi.Builder()
			.setClientId(System.getenv("SPOTIFY_CLIENT_ID"))
			.setClientSecret(System.getenv("SPOTIFY_CLIENT_SECRET"))
			.setRedirectUri(SpotifyHttpManager.makeUri(System.getenv("SPOTIFY_REDIRECT"))).build();

	public static void main(String[] args) {
		new SpotifyLoader().load();
	}

	@Override
	public void load(String... args)
	{
		if(args.length == 0) {
			LOGGER.info("Loading Spotify...");
			List<Doc> i = docs.findSpotify();
			i.stream().forEach(page -> {
				String id = page.getId(); 
				String type = page.getType();
				if("album".equals(type)) {
					loadAlbum(page);
				}
				else if("podcast".equals(type)) {
					loadTracks(page);
				}
				else {
					loadArtist(page);
				}
				docs.save(page);
			});
		}
		else if("follow".equals(args[0])) {
			LOGGER.info("Following Spotify playlists...");
			spotify = new SpotifyApi.Builder().setAccessToken(args[2]).build();
			followPlaylist(args[1]);
		}		
		else if("rename".equals(args[0])) {
			LOGGER.info("Renaming Spotify playlists...");
			spotify = new SpotifyApi.Builder().setAccessToken(args[1]).build();
			renamePlaylists();
		}
		else {
			LOGGER.info("Creating Spotify playlist...");
			spotify = new SpotifyApi.Builder().setAccessToken(args[0]).build();
			createPlaylists();
		}
	}

	private void followPlaylist(String id)
	{
		Preconditions.checkNotNull(id);

		try {
			FollowPlaylistRequest req0 = spotify.followPlaylist(id, true).build();
			req0.execute();
			LOGGER.info("Playlist " + id + " followed");
		}
		catch (Exception e) {
			LOGGER.error("ERROR following playlist " + id + ": " + e.getMessage());
		}
	}

	//	private void resetPlaylists()
	//	{
	//		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("spalbumid", null))).iterator();
	//		while(i.hasNext()) 
	//		{ 
	//			Document page = i.next();
	//			String id = page.getString("id");
	//			try {
	//				AtomicInteger tracksnum = new AtomicInteger(0);
	//				List<org.bson.Document> tracks = page.get("tracks", List.class);
	//				tracks.forEach(t -> {
	//					if(t.getString("spotify") != null && !NA.equals(t.getString("spotify"))) {
	//						tracksnum.incrementAndGet();
	//					}
	//				});
	//				if(tracksnum.get() > 0) {
	//					page.append("dirty", true);
	//					docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
	//					LOGGER.info("Playlist " + id + " reset: " + tracksnum.get());
	//				}
	//			} 
	//			catch (Exception e) {
	//				LOGGER.error("ERROR resetting playlist " + id + ": " + e.getMessage());
	//			}
	//		}
	//	}

	//	private void unfollowPlaylists()
	//	{
	//		try {
	//			for(int i = 0; i < 10; i++) {
	//				GetListOfCurrentUsersPlaylistsRequest req = spotify.getListOfCurrentUsersPlaylists().offset(0).limit(50).build();
	//				Paging<PlaylistSimplified> playlists = req.execute();
	//				for(PlaylistSimplified p : playlists.getItems()) {
	//					try {
	//						UnfollowPlaylistRequest req2 = spotify.unfollowPlaylist(p.getId()).build();
	//						req2.execute();
	//						LOGGER.info("Playlist " + p.getId() + " unfollowed");
	//					}
	//					catch (Exception e) {
	//						LOGGER.error("ERROR unfollowing playlist " + p.getId() + ": " + e.getMessage());
	//					}
	//				}
	//			}
	//		}
	//		catch (Exception e) {
	//			LOGGER.error("ERROR getting the list of playlists: " + e.getMessage());
	//		}
	//	}

	private void renamePlaylists()
	{
		List<Doc> i = docs.findByType("podcast");
		i.stream().forEach(page ->  {
			String id = page.getId();
			String spalbumid = page.getSpalbumid();
			if(spalbumid != null) {
				try {
					//					String name = "";
					//					ChangePlaylistsDetailsRequest req = spotify.changePlaylistsDetails(spalbumid).name(name).build();
					//					req.execute();
					LOGGER.info("Playlist " + id + " renamed");
				} 
				catch (Exception e) {
					LOGGER.error("ERROR renaming playlist " + id + ": " + e.getMessage());
				}
			}
		});
	}

	private void createPlaylists()
	{
		List<Doc> i = docs.findDirtyPodcast();
		i.stream().forEach(page -> {
			String id = page.getId();
			String title = page.getArtist();
			String description = page.getTitle();
			Date date = page.getDate();
			title = format(title, date);
			createPlaylist(page, title, description);
			docs.save(page);
		});

		List<Stat> s = stats.findAll();
		s.stream().forEach(page -> {
			String name = page.getName();
			createPlaylist(page, name, null);
			stats.save(page); 
		});
	}

	private void createPlaylist(Doc page, String title, String description)
	{
		List<org.bson.Document> tracks = page.getTracks();
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
				String id = page.getSpalbumid();
				try
				{
					if(id == null) {
						// create new playlist
						CreatePlaylistRequest req = spotify.createPlaylist(SPOTIFY_USER, title).description(description).public_(true).build();
						Playlist playlist = req.execute();
						id = playlist.getId();
						AddItemsToPlaylistRequest req2 = spotify.addItemsToPlaylist(playlist.getId(), Arrays.copyOf(uris.toArray(), uris.size(), String[].class)).build();
						req2.execute();
						page.setSpalbumid(id);
						page.setDirty(false);
						LOGGER.info("Playlist: " + playlist.getName() + " spotify: " + id + " created");
					}
					// update existing playlist
					else {
						if(uris.size() > 50) {
							ReplacePlaylistsItemsRequest req = spotify.replacePlaylistsItems(id, 
									Arrays.copyOf(uris.subList(0, 50).toArray(), 50, String[].class)).build();
							req.execute();
							AddItemsToPlaylistRequest req2 = spotify.addItemsToPlaylist(id, 
									Arrays.copyOf(uris.subList(50, uris.size()).toArray(), uris.size()-50, String[].class)).build();
							req2.execute();
						}
						else {
							ReplacePlaylistsItemsRequest req = spotify.replacePlaylistsItems(id, 
									Arrays.copyOf(uris.toArray(), uris.size(), String[].class)).build();
							req.execute();
						}
						page.setDirty(false);
						LOGGER.info("Playlist " + title + " updated");
					}
				}
				catch (Exception e) 
				{
					LOGGER.error("ERROR creating/updating playlist " + id + ": " + e.getMessage());
				}
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
		if(credentials == null)
		{
			try 
			{
				credentials = spotify.clientCredentials().build().execute();
				spotify.setAccessToken(credentials.getAccessToken());
				LOGGER.info("Expires in: " + credentials.getExpiresIn() + " secs");
			} 
			catch (Exception e) 
			{
				LOGGER.error("ERROR connecting to Spotify: " + e.getMessage());
			}
		}
	}

	private void loadAlbum(Doc page)
	{
		String artist = page.getArtist();
		String album = page.getTitle();

		// check if the article was already crawled
		LOGGER.debug("Loading album " + artist + " - " + album);
		Document spotify = loadAlbum(artist, album);
		if(spotify != null)
		{
			String artistId = spotify.getString("spartistid");
			String albumId = spotify.getString("spalbumid");
			page.setSpartistid(artistId);
			page.setSpalbumid(albumId);
			page.setCoverL(spotify.getString("coverL"));
			page.setCoverM(spotify.getString("coverM"));
			page.setCoverS(spotify.getString("coverS"));
			page.setScore(spotify.getInteger("score"));
			page.setArtistid(null);
		}
		else
		{
			page.setSpartistid(NA);
			page.setSpalbumid(NA);
			page.setScore(0);
		}
	}

	private Document loadAlbum(String artist, String album)
	{
		TreeMap<Integer, Document> albumsMap = Maps.newTreeMap();
		try
		{
			login();
			String q = artist + " " + album;
			SearchAlbumsRequest request = spotify.searchAlbums(q).market(CountryCode.IT).build();
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

					if(!albumsMap.containsKey(score)) {
						LOGGER.info(artist + " - " + album + " | " + spartist + " - " + spalbum + ": " + score);
						albumsMap.put(score, page);
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("ERROR loading " + artist + " - " + album + ": " + e.getMessage(), e);
			relogin();
		}
		return albumsMap.isEmpty() ?  null : albumsMap.descendingMap().firstEntry().getValue();
	}

	private void loadArtist(Doc page)
	{
		String artist = page.getArtist();

		LOGGER.debug("Loading artist " + artist);
		Doc spotify = loadArtist(artist);
		if(spotify != null)
		{
			page.setSpartistid(spotify.getSpartistid());
			page.setCoverL(spotify.getCoverL());
			page.setCoverM(spotify.getCoverM());
			page.setCoverS(spotify.getCoverS());
			page.setScore(spotify.getScore());
			page.setArtistid(null);
		}
		else
		{
			page.setSpartistid(NA);
			page.setScore(0);
		}
	}

	private Doc loadArtist(String artist)
	{
		TreeMap<Integer, Document> artistsMap = Maps.newTreeMap();
		if(artist != null) {
			try
			{
				login();
				SearchArtistsRequest request = spotify.searchArtists(artist).market(CountryCode.IT).build();
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
		}
		return artistsMap.isEmpty() ?  null : artistsMap.descendingMap().firstEntry().getValue();
	}

	private void loadArtistDetails(Document page)
	{
		String artistId = page.getString("spartistid");
		if(artistId != null) {
			try
			{
				login();
				GetArtistRequest request = spotify.getArtist(artistId).build();
				Artist artist = request.execute();
				if(artist != null) {
					String artistid = artist.getId();
					Document detail = new Document("spartistid", artistid);
					getImages(detail, artist.getImages());
				}
			}
			catch (Exception e) 
			{
				LOGGER.error("ERROR loading " + artistId + ": " + e.getMessage());
				relogin();
			}
		}
	}

	private void loadTracks(Doc page)
	{
		List<org.bson.Document> tracks = page.getTracks();
		if(CollectionUtils.isNotEmpty(tracks))
		{
			int score = 0;
			for(org.bson.Document track : tracks)
			{
				String title = track.getString("titleOrig");
				String spotifyId = track.getString("spotify");
				if(spotifyId == null)
				{
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
							append("score", spotify.getInteger("score")).
							append("artistid", null).
							append("youtube", null);
						}
						else
						{
							track.append("spotify", NA).
							append("artist", null).
							append("album", null).
							append("track", null).
							append("spartistid", null).
							append("spalbumid", null).
							append("coverL", null).
							append("coverM", null).
							append("coverS", null).
							append("title", track.getString("titleOrig")).
							append("score", 0).
							append("artistid", NA).
							append("youtube", NA);
						}
					}
					catch (Exception e) 
					{
						LOGGER.error("ERROR loading " + title + ": " + e.getMessage(), e);
						relogin();
					}
				}
				score += track.getInteger("score", 0);
			}
			score = score/tracks.size();
			page.setScore(score);
			page.setDirty(true);
		}
	}

	private org.bson.Document getTrack(String title) throws Exception
	{
		Set<String> chunks = parseTrack(title);
		TreeMap<Integer, Document> tracksMap = loadTrack(chunks);
		return tracksMap.isEmpty() ? null : tracksMap.descendingMap().firstEntry().getValue();
	}

	private TreeMap<Integer, Document> loadTrack(Set<String> titles) throws Exception
	{
		TreeMap<Integer, Document> tracksMap = Maps.newTreeMap();
		if(CollectionUtils.isNotEmpty(titles)) {
			for(String title : titles) {
				if(StringUtils.isNotBlank(title)) {
					SearchTracksRequest request = spotify.searchTracks(title).market(CountryCode.IT).build();
					Paging<Track> tracks = request.execute();
					if(tracks.getItems().length == 0) {
						LOGGER.info("Not found: " + title);
					}
					for(int i = 0; i < tracks.getItems().length; i++)
					{
						Track track = tracks.getItems()[i];
						if(track != null) {
							String spartist = track.getArtists()[0].getName();
							String spartistid = track.getArtists()[0].getId();
							String spalbum = track.getAlbum().getName();
							String spalbumid = track.getAlbum().getId();
							String spsong = track.getName();
							for(String match : HumanBeats.FEAT) {
								Matcher m = Pattern.compile(match).matcher(spsong);
								if(m.matches()) {
									spsong = m.group(1);
									break;
								}
							}
							String trackid = track.getId();
							int score = FuzzySearch.tokenSortRatio(title, spartist + " " + spsong);
							if(score >= SCORE && !tracksMap.containsKey(score)) {
								LOGGER.info("Found: " + title + " | " + spartist + " - " + spsong + " | score: " + score);
								Document page = new Document("spotify", trackid);
								page.append("artist", spartist);
								page.append("spartistid", spartistid);
								page.append("album", spalbum);
								page.append("spalbumid", spalbumid);
								page.append("track", spsong);
								page.append("score", score);
								getImages(page, track.getAlbum().getImages());
								tracksMap.put(score, page);
							}
						}
					}
					Thread.sleep(100);
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
