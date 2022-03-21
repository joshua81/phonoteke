package org.phonoteke.loader;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class StatsLoader extends HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(StatsLoader.class);

	public static void main(String[] args) {
		new StatsLoader().load();
	}

	@Override
	public void load(String... args)
	{
		int year = 2022;
		int month = 2;
		String source = "theblessedmadonna";
		LOGGER.info("Calculating stats...");
		LocalDateTime start = LocalDateTime.now().withYear(year).withMonth(month).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
		LocalDateTime end = LocalDateTime.now().withYear(year).withMonth(month).with(TemporalAdjusters.lastDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
		MongoCursor<Document> i = docs.find(Filters.and(
				Filters.eq("type", "podcast"),
				Filters.eq("source", source), 
				Filters.gt("date", start),
				Filters.lt("date", end))).iterator();

		TreeMap<Integer, List<String>> topArtists = Maps.newTreeMap();
		TreeMap<String, Integer> artists = Maps.newTreeMap();
		TreeMap<Integer, List<String>> topAlbums = Maps.newTreeMap();
		TreeMap<String, Integer> albums = Maps.newTreeMap();
		TreeMap<Integer, List<String>> topSongs = Maps.newTreeMap();
		TreeMap<String, Integer> songs = Maps.newTreeMap();
		TreeMap<Integer, List<String>> topVideos = Maps.newTreeMap();
		TreeMap<String, Integer> videos = Maps.newTreeMap();
		while(i.hasNext()) 
		{
			Document page = i.next();
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document t : tracks)
				{
					String artist = t.getString("artist");
					if(artist != null) {
						Integer artistNum = artists.get(artist);
						if(artistNum == null) {
							artistNum = 0;
						}
						artists.put(artist, artistNum+1);
					}

					String album = t.getString("album");
					if(album != null) {
						Integer albumNum = albums.get(artist + " - " + album);
						if(albumNum == null) {
							albumNum = 0;
						}
						albums.put(artist + " - " + album, albumNum+1);
					}
					
					String song = t.getString("spotify");
					if(song != null) {
						Integer songNum = songs.get(song);
						if(songNum == null) {
							songNum = 0;
						}
						songs.put(song, songNum+1);
					}
					
					String video = t.getString("youtube");
					if(video != null) {
						Integer videoNum = videos.get(video);
						if(videoNum == null) {
							videoNum = 0;
						}
						videos.put(video, videoNum+1);
					}
				}
			}
		}

		artists.keySet().forEach(a -> {
			if(!topArtists.containsKey(artists.get(a))) {
				topArtists.put(artists.get(a), Lists.newArrayList());
			}
			topArtists.get(artists.get(a)).add(a);
		});
		for(Integer rank : topArtists.descendingKeySet()) {
			LOGGER.info("Artists: " + rank + " -> " + topArtists.get(rank));
		}
		LOGGER.info("--------------------");
		
		albums.keySet().forEach(a -> {
			if(!topAlbums.containsKey(albums.get(a))) {
				topAlbums.put(albums.get(a), Lists.newArrayList());
			}
			topAlbums.get(albums.get(a)).add(a);
		});
		for(Integer rank : topAlbums.descendingKeySet()) {
			LOGGER.info("Albums: " + rank + " -> " + topAlbums.get(rank));
		}
		LOGGER.info("--------------------");
		
		songs.keySet().forEach(s -> {
			if(!topSongs.containsKey(songs.get(s))) {
				topSongs.put(songs.get(s), Lists.newArrayList());
			}
			topSongs.get(songs.get(s)).add(s);
		});
		for(Integer rank : topSongs.descendingKeySet()) {
			LOGGER.info("Songs: " + rank + " -> " + topSongs.get(rank));
		}
		LOGGER.info("--------------------");
		
		videos.keySet().forEach(a -> {
			if(!topVideos.containsKey(videos.get(a))) {
				topVideos.put(videos.get(a), Lists.newArrayList());
			}
			topVideos.get(videos.get(a)).add(a);
		});
		for(Integer rank : topVideos.descendingKeySet()) {
			LOGGER.info("Videos: " + rank + " -> " + topVideos.get(rank));
		}
		LOGGER.info("--------------------");
	}
}
