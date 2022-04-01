package org.phonoteke.loader;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
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
		if(args.length == 0) {
			load(null, "Human Beats");

			MongoCursor<Document> i = authors.find().iterator();
			while(i.hasNext()) {
				Document page = i.next();
				String source = page.getString("source");
				String name = page.getString("name");
				load(source, name);
			}
		}
	}

	private void load(String source, String name)
	{
		Map<String, BigDecimal> weights = Maps.newHashMap();
		if(source != null) {
			weights.put(source, BigDecimal.ONE);
		}
		else {
			MongoCursor<Document> i = getDocs(source);
			i.forEachRemaining(page -> {
				String s = page.getString("source");
				BigDecimal weight = weights.get(s);
				if(weight == null) {
					weight = BigDecimal.ZERO;
				}
				weights.put(s, weight.add(BigDecimal.ONE));
			});
		}

		MongoCursor<Document> i = getDocs(source);
		if(!i.hasNext()) {
			return;
		}

		TreeMap<BigDecimal, List<String>> topArtists = Maps.newTreeMap();
		TreeMap<String, BigDecimal> artists = Maps.newTreeMap();
		Map<String, Document> trackArtists = Maps.newHashMap();
		TreeMap<BigDecimal, List<String>> topAlbums = Maps.newTreeMap();
		TreeMap<String, BigDecimal> albums = Maps.newTreeMap();
		Map<String, Document> trackAlbums = Maps.newHashMap();
		TreeMap<BigDecimal, List<String>> topSongs = Maps.newTreeMap();
		TreeMap<String, BigDecimal> songs = Maps.newTreeMap();
		Map<String, Document> trackSongs = Maps.newHashMap();
		TreeMap<BigDecimal, List<String>> topVideos = Maps.newTreeMap();
		TreeMap<String, BigDecimal> videos = Maps.newTreeMap();
		Map<String, Document> trackVideos = Maps.newHashMap();

		i.forEachRemaining(page -> {
			String s = page.getString("source");
			List<Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isEmpty(tracks)) {
				return;
			}

			if(!i.hasNext()) {
				return;
			}
			if(CollectionUtils.isNotEmpty(tracks)) {
				tracks.forEach(t -> {
					// Artist
					String artist = t.getString("spartistid");
					if(artist != null && !HumanBeats.NA.equals(artist)) {
						BigDecimal artistNum = artists.get(artist);
						if(artistNum == null) {
							artistNum = BigDecimal.ZERO;
						}
						BigDecimal score = artistNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
						artists.put(artist, score);
						trackArtists.put(artist, t);
					}

					// Album
					String album = t.getString("spalbumid");
					if(album != null && !HumanBeats.NA.equals(album)) {
						BigDecimal albumNum = albums.get(album);
						if(albumNum == null) {
							albumNum = BigDecimal.ZERO;
						}
						BigDecimal score = albumNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
						albums.put(album, score);
						trackAlbums.put(album, t);
					}

					// Song
					String song = t.getString("spotify");
					if(song != null && !HumanBeats.NA.equals(song)) {
						BigDecimal songNum = songs.get(song);
						if(songNum == null) {
							songNum = BigDecimal.ZERO;
						}
						BigDecimal score = songNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
						songs.put(song, score);
						trackSongs.put(song, t);
					}

					// Video
					String video = t.getString("youtube");
					if(video != null && !HumanBeats.NA.equals(video)) {
						BigDecimal videoNum = videos.get(video);
						if(videoNum == null) {
							videoNum = BigDecimal.ZERO;
						}
						BigDecimal score = videoNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
						videos.put(video, score);
						trackVideos.put(video, t);
					}
				});
			}
		});

		Document doc = new Document("source", source)
				.append("name", name);

		artists.keySet().forEach(a -> {
			if(!topArtists.containsKey(artists.get(a))) {
				topArtists.put(artists.get(a), Lists.newArrayList());
			}
			topArtists.get(artists.get(a)).add(a);
		});

		List<Document> jsonArtists = Lists.newArrayList();
		BigDecimal topScore = topArtists.descendingKeySet().iterator().next();
		for(BigDecimal score : topArtists.descendingKeySet()) {
			final Float score10 = calculateScore(score, topScore);
			topArtists.get(score).forEach(a -> {
				jsonArtists.add(getArtist(trackArtists.get(a), score10));
			});
		}
		doc.append("artists", subList(jsonArtists, 100));

		albums.keySet().forEach(a -> {
			if(!topAlbums.containsKey(albums.get(a))) {
				topAlbums.put(albums.get(a), Lists.newArrayList());
			}
			topAlbums.get(albums.get(a)).add(a);
		});

		List<Document> jsonAlbums = Lists.newArrayList();
		topScore = topAlbums.descendingKeySet().iterator().next();
		for(BigDecimal score : topAlbums.descendingKeySet()) {
			final Float score10 = calculateScore(score, topScore);
			topAlbums.get(score).forEach(a -> {
				jsonAlbums.add(getAlbum(trackAlbums.get(a), score10));
			});
		}
		doc.append("albums", subList(jsonAlbums, 100));

		songs.keySet().forEach(s -> {
			if(!topSongs.containsKey(songs.get(s))) {
				topSongs.put(songs.get(s), Lists.newArrayList());
			}
			topSongs.get(songs.get(s)).add(s);
		});

		List<Document> jsonSongs = Lists.newArrayList();
		topScore = topSongs.descendingKeySet().iterator().next();
		for(BigDecimal score : topSongs.descendingKeySet()) {
			final Float score10 = calculateScore(score, topScore);
			topSongs.get(score).forEach(s -> {
				jsonSongs.add(getTrack(trackSongs.get(s), score10));
			});
		}
		doc.append("tracks", subList(jsonSongs, 100));

		if(MapUtils.isNotEmpty(videos)) {
			videos.keySet().forEach(a -> {
				if(!topVideos.containsKey(videos.get(a))) {
					topVideos.put(videos.get(a), Lists.newArrayList());
				}
				topVideos.get(videos.get(a)).add(a);
			});

			List<Document> jsonVideos = Lists.newArrayList();
			topScore = topVideos.descendingKeySet().iterator().next();
			for(BigDecimal score : topVideos.descendingKeySet()) {
				final Float score10 = calculateScore(score, topScore);
				topVideos.get(score).forEach(v -> {
					jsonVideos.add(getVideo(trackVideos.get(v), score10));
				});
			}
			doc.append("videos", subList(jsonVideos, 100));
		}

		stats.deleteOne(Filters.and(Filters.eq("source", source)));
		stats.insertOne(doc);
		LOGGER.info(source + " added");
	}

	private Float calculateScore(BigDecimal score, BigDecimal topScore) {
		return score.divide(topScore, MathContext.DECIMAL32).multiply(BigDecimal.TEN).floatValue();
	}

	private MongoCursor<Document> getDocs(String source) {
		LocalDateTime start = LocalDateTime.now().minusDays(30);
		LocalDateTime end = LocalDateTime.now();

		return source == null ? docs.find(Filters.and(
				Filters.eq("type", "podcast"),
				Filters.gt("date", start),
				Filters.lt("date", end))).iterator() :

					docs.find(Filters.and(
							Filters.eq("type", "podcast"),
							Filters.eq("source", source), 
							Filters.gt("date", start),
							Filters.lt("date", end))).iterator();
	}

	private Document getArtist(Document track, Float score) {
		return new Document("artist", track.getString("artist"))
				.append("spartistid", track.getString("spartistid"))
				.append("artistid", replaceNA(track.getString("artistid")));
	}

	private Document getAlbum(Document track, Float score) {
		return new Document("artist", track.getString("artist"))
				.append("album", track.getString("album"))
				.append("spartistid", track.getString("spartistid"))
				.append("spalbumid", track.getString("spalbumid"))
				.append("cover", track.getString("coverM"))
				.append("artistid", replaceNA(track.getString("artistid")));
	}

	private Document getTrack(Document track, Float score) {
		return new Document("artist", track.getString("artist"))
				.append("album", track.getString("album"))
				.append("track", track.getString("track"))
				.append("spartistid", track.getString("spartistid"))
				.append("spalbumid", track.getString("spalbumid"))
				.append("spotify", track.getString("spotify"))
				.append("cover", track.getString("coverS"))
				.append("artistid", replaceNA(track.getString("artistid")));
	}

	private Document getVideo(Document track, Float score) {
		return new Document("artist", track.getString("artist"))
				.append("album", track.getString("album"))
				.append("track", track.getString("track"))
				.append("spartistid", track.getString("spartistid"))
				.append("spalbumid", track.getString("spalbumid"))
				.append("youtube", track.getString("youtube"))
				.append("artistid", replaceNA(track.getString("artistid")));
	}

	private String replaceNA(String val) {
		return HumanBeats.NA.equals(val) ? null : val;
	}

	private List<Document> subList(List<Document> list, int size) {
		return list.size() <= size ? list : list.subList(0, size);
	}
}
