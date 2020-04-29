package org.phonoteke.loader;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.api.client.util.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.data.playlists.AddTracksToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;

public class SpotifyPlaylistLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(SpotifyPlaylistLoader.class);

	private static final SpotifyApi PLAYLIST_API = new SpotifyApi.Builder().setAccessToken(System.getenv("SPOTIFY_TOKEN")).build();
	private static final String SPOTIFY_USER = "andrea.ricci81";


	public static void main(String[] args)
	{
		new SpotifyPlaylistLoader().load();
	}

	private void load()
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
				String title = page.getString("artist") + " - " + page.getString("title");
				try
				{
					CreatePlaylistRequest playlistRequest = PLAYLIST_API.createPlaylist(SPOTIFY_USER, title).public_(true).build();
					Playlist playlist = playlistRequest.execute();
					System.out.println("Playlist: " + playlist.getName() + " spotify: " + playlist.getId() + " created");
					AddTracksToPlaylistRequest itemsRequest = PLAYLIST_API.addTracksToPlaylist(SPOTIFY_USER, playlist.getId(), Arrays.copyOf(uris.toArray(), uris.size(), String[].class)).build();
					itemsRequest.execute();
					page.append("spalbumid", playlist.getId());
				}
				catch (Exception e) 
				{
					LOGGER.error("Error creating playlist " + title + ": " + e.getMessage(), e);
				}
			}
		}
	}
}
