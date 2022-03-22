package org.phonoteke.loader;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
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
		new StatsLoader().load("2022", "3", null);
	}

	@Override
	public void load(String... args)
	{
		int year = Integer.parseInt(args[0]);
		int month = Integer.parseInt(args[1]);
		String source = args[2];
		LOGGER.info("Calculating stats...");
		LocalDateTime start = LocalDateTime.now().withYear(year).withMonth(month).with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
		LocalDateTime end = LocalDateTime.now().withYear(year).withMonth(month).with(TemporalAdjusters.lastDayOfMonth()).withHour(0).withMinute(0).withSecond(0);

		MongoCursor<Document> i = docs.find(Filters.and(
				Filters.eq("type", "podcast"),
//				Filters.eq("source", source), 
				Filters.gt("date", start),
				Filters.lt("date", end))).iterator();
		Map<String, BigDecimal> weights = Maps.newHashMap();
		if(source != null) {
			weights.put(source, BigDecimal.ONE);
		}
		else {
			while(i.hasNext()) 
			{
				Document page = i.next();
				source = page.getString("source");
				BigDecimal weight = weights.get(source);
				if(weight == null) {
					weight = BigDecimal.ZERO;
				}
				weights.put(source, weight.add(BigDecimal.ONE));
			}
		}

		i = docs.find(Filters.and(
				Filters.eq("type", "podcast"),
//				Filters.eq("source", source), 
				Filters.gt("date", start),
				Filters.lt("date", end))).iterator();
		TreeMap<BigDecimal, List<String>> topArtists = Maps.newTreeMap();
		TreeMap<String, BigDecimal> artists = Maps.newTreeMap();
		TreeMap<BigDecimal, List<String>> topAlbums = Maps.newTreeMap();
		TreeMap<String, BigDecimal> albums = Maps.newTreeMap();
		TreeMap<BigDecimal, List<String>> topSongs = Maps.newTreeMap();
		TreeMap<String, BigDecimal> songs = Maps.newTreeMap();
		TreeMap<BigDecimal, List<String>> topVideos = Maps.newTreeMap();
		TreeMap<String, BigDecimal> videos = Maps.newTreeMap();
		while(i.hasNext()) 
		{
			Document page = i.next();
			source = page.getString("source");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document t : tracks)
				{
					String artist = t.getString("artist");
					if(artist != null && !HumanBeats.NA.equals(artist)) {
						BigDecimal artistNum = artists.get(artist);
						if(artistNum == null) {
							artistNum = BigDecimal.ZERO;
						}
						artists.put(artist, artistNum.add(BigDecimal.ONE.divide(weights.get(source), MathContext.DECIMAL32)));
					}

					String album = t.getString("album");
					if(album != null && !HumanBeats.NA.equals(album)) {
						BigDecimal albumNum = albums.get(artist + " - " + album);
						if(albumNum == null) {
							albumNum = BigDecimal.ZERO;
						}
						albums.put(artist + " - " + album, albumNum.add(BigDecimal.ONE.divide(weights.get(source), MathContext.DECIMAL32)));
					}

					String song = t.getString("spotify");
					if(song != null && !HumanBeats.NA.equals(song)) {
						BigDecimal songNum = songs.get(song);
						if(songNum == null) {
							songNum = BigDecimal.ZERO;
						}
						songs.put(song, songNum.add(BigDecimal.ONE.divide(weights.get(source), MathContext.DECIMAL32)));
					}

					String video = t.getString("youtube");
					if(video != null && !HumanBeats.NA.equals(video)) {
						BigDecimal videoNum = videos.get(video);
						if(videoNum == null) {
							videoNum = BigDecimal.ZERO;
						}
						videos.put(video, videoNum.add(BigDecimal.ONE.divide(weights.get(source), MathContext.DECIMAL32)));
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
		BigDecimal top = topArtists.descendingKeySet().iterator().next();
		for(BigDecimal rank : topArtists.descendingKeySet()) {
			LOGGER.info("Artists: " + rank.divide(top, MathContext.DECIMAL32) + " -> " + topArtists.get(rank));
		}
		LOGGER.info("--------------------");

		albums.keySet().forEach(a -> {
			if(!topAlbums.containsKey(albums.get(a))) {
				topAlbums.put(albums.get(a), Lists.newArrayList());
			}
			topAlbums.get(albums.get(a)).add(a);
		});
		top = topAlbums.descendingKeySet().iterator().next();
		for(BigDecimal rank : topAlbums.descendingKeySet()) {
			LOGGER.info("Albums: " + rank.divide(top, MathContext.DECIMAL32) + " -> " + topAlbums.get(rank));
		}
		LOGGER.info("--------------------");

		songs.keySet().forEach(s -> {
			if(!topSongs.containsKey(songs.get(s))) {
				topSongs.put(songs.get(s), Lists.newArrayList());
			}
			topSongs.get(songs.get(s)).add(s);
		});
		top = topSongs.descendingKeySet().iterator().next();
		for(BigDecimal rank : topSongs.descendingKeySet()) {
			LOGGER.info("Songs: " + rank.divide(top, MathContext.DECIMAL32) + " -> " + topSongs.get(rank));
		}
		LOGGER.info("--------------------");

		videos.keySet().forEach(a -> {
			if(!topVideos.containsKey(videos.get(a))) {
				topVideos.put(videos.get(a), Lists.newArrayList());
			}
			topVideos.get(videos.get(a)).add(a);
		});
		top = topVideos.descendingKeySet().iterator().next();
		for(BigDecimal rank : topVideos.descendingKeySet()) {
			LOGGER.info("Videos: " + rank.divide(top, MathContext.DECIMAL32) + " -> " + topVideos.get(rank));
		}
		LOGGER.info("--------------------");
	}
}
