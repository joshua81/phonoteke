package org.phonoteke.loader;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
			load(null, "Human Beats", null);

			MongoCursor<Document> i = authors.find().iterator();
			while(i.hasNext()) {
				Document page = i.next();
				String source = page.getString("source");
				String name = page.getString("name");
				Date lastEpisode = page.getDate("lastEpisodeDate");
				load(source, name, lastEpisode);
			}
		}
	}

	private void load(String source, String name, Date lastEpisode)
	{
		Map<String, BigDecimal> weights = Maps.newHashMap();
		if(source != null) {
			weights.put(source, BigDecimal.ONE);
		}
		else {
			MongoCursor<Document> i = getDocs(source, lastEpisode);
			i.forEachRemaining(page -> {
				String s = page.getString("source");
				BigDecimal weight = weights.get(s);
				if(weight == null) {
					weight = BigDecimal.ZERO;
				}
				weights.put(s, weight.add(BigDecimal.ONE));
			});
		}

		MongoCursor<Document> i = getDocs(source, lastEpisode);
		if(!i.hasNext()) {
			return;
		}

		TreeMap<BigDecimal, List<String>> topArtists = Maps.newTreeMap();
		TreeMap<String, BigDecimal> artists = Maps.newTreeMap();
		Map<String, Document> trackArtists = Maps.newHashMap();
		Map<String, Date> dateArtists = Maps.newHashMap();
		
		TreeMap<BigDecimal, List<String>> topAlbums = Maps.newTreeMap();
		TreeMap<String, BigDecimal> albums = Maps.newTreeMap();
		Map<String, Document> trackAlbums = Maps.newHashMap();
		Map<String, Date> dateAlbums = Maps.newHashMap();
		
		TreeMap<BigDecimal, List<String>> topSongs = Maps.newTreeMap();
		TreeMap<String, BigDecimal> songs = Maps.newTreeMap();
		Map<String, Document> trackSongs = Maps.newHashMap();
		Map<String, Date> dateSongs = Maps.newHashMap();
		
		TreeMap<BigDecimal, List<String>> topVideos = Maps.newTreeMap();
		TreeMap<String, BigDecimal> videos = Maps.newTreeMap();
		Map<String, Document> trackVideos = Maps.newHashMap();
		Map<String, Date> dateVideos = Maps.newHashMap();

		i.forEachRemaining(page -> {
			String s = page.getString("source");
			Date date = page.getDate("date");
			List<Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isEmpty(tracks)) {
				return;
			}

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
					if(!dateArtists.containsKey(artist) || dateArtists.get(artist).before(date)) {
						dateArtists.put(artist, date);
					}
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
					if(!dateAlbums.containsKey(album) || dateAlbums.get(album).before(date)) {
						dateAlbums.put(album, date);
					}
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
					if(!dateSongs.containsKey(song) || dateSongs.get(song).before(date)) {
						dateSongs.put(song, date);
					}
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
					if(!dateVideos.containsKey(video) || dateVideos.get(video).before(date)) {
						dateVideos.put(video, date);
					}
				}
			});
		});

		MongoCursor<Document> j = stats.find(Filters.and(Filters.eq("source", source))).iterator();
		Document doc = j.tryNext();
		if(doc == null) {
			doc = new Document("source", source)
					.append("name", name);
		}

		artists.keySet().forEach(a -> {
			if(!topArtists.containsKey(artists.get(a))) {
				topArtists.put(artists.get(a), Lists.newArrayList());
			}
			topArtists.get(artists.get(a)).add(a);
		});

		// Artists
		List<Document> jsonArtists = Lists.newArrayList();
		BigDecimal topScore = topArtists.descendingKeySet().iterator().next();
		for(BigDecimal score : topArtists.descendingKeySet()) {
			final Float score10 = calculateScore(score, topScore);
			Collections.sort(topArtists.get(score), new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return dateArtists.get(o2).compareTo(dateArtists.get(o1));
				}
			});
			topArtists.get(score).forEach(a -> {
				LOGGER.info("Artist: " + a + " score: " + score10);
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

		// Albums
		List<Document> jsonAlbums = Lists.newArrayList();
		topScore = topAlbums.descendingKeySet().iterator().next();
		for(BigDecimal score : topAlbums.descendingKeySet()) {
			final Float score10 = calculateScore(score, topScore);
			Collections.sort(topAlbums.get(score), new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					Document a1 = trackAlbums.get(o1);
					Document a2 = trackAlbums.get(o2);
					int res = artists.get(a2.get("spartistid")).compareTo(artists.get(a1.get("spartistid")));
					return res != 0 ? res : dateAlbums.get(o2).compareTo(dateAlbums.get(o1));
				}
			});
			topAlbums.get(score).forEach(a -> {
				Document a1 = trackAlbums.get(a);
				LOGGER.info("Album: " + a + " score: " + score10 + " artist: " + artists.get(a1.get("spartistid")));
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

		// Tracks
		List<Document> jsonSongs = Lists.newArrayList();
		topScore = topSongs.descendingKeySet().iterator().next();
		for(BigDecimal score : topSongs.descendingKeySet()) {
			final Float score10 = calculateScore(score, topScore);
			Collections.sort(topSongs.get(score), new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					Document s1 = trackSongs.get(o1);
					Document s2 = trackSongs.get(o2);
					int res = artists.get(s2.get("spartistid")).compareTo(artists.get(s1.get("spartistid")));
					if(res == 0) {
						res = albums.get(s2.get("spalbumid")).compareTo(albums.get(s1.get("spalbumid")));
					}
					return res != 0 ? res : dateSongs.get(o2).compareTo(dateSongs.get(o1));
				}
			});
			topSongs.get(score).forEach(s -> {
				LOGGER.info("Track: " + s + " score: " + score10);
				jsonSongs.add(getTrack(trackSongs.get(s), score10));
			});
		}
		doc.append("tracks", subList(jsonSongs, 100));

		// Videos
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
				Collections.sort(topVideos.get(score), new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						Document v1 = trackVideos.get(o1);
						Document v2 = trackVideos.get(o2);
						int res = artists.get(v2.get("spartistid")).compareTo(artists.get(v1.get("spartistid")));
						if(res == 0) {
							res = albums.get(v2.get("spalbumid")).compareTo(albums.get(v1.get("spalbumid")));
						}
						return res != 0 ? res : dateVideos.get(o2).compareTo(dateVideos.get(o1));
					}
				});
				topVideos.get(score).forEach(v -> {
					LOGGER.info("Video: " + v + " score: " + score10);
					jsonVideos.add(getVideo(trackVideos.get(v), score10));
				});
			}
			doc.append("videos", subList(jsonVideos, 100));
		}

		stats.updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
		LOGGER.info(source + " updated");
	}

	private Float calculateScore(BigDecimal score, BigDecimal topScore) {
		return score.divide(topScore, MathContext.DECIMAL32).multiply(BigDecimal.TEN).floatValue();
	}

	private MongoCursor<Document> getDocs(String source, Date lastEpisode) {
		LocalDateTime end = lastEpisode == null ? LocalDateTime.now() : LocalDateTime.ofInstant(lastEpisode.toInstant(), ZoneOffset.UTC);
		LocalDateTime start = end.minusDays(31);

		return source == null ? docs.find(Filters.and(
				Filters.eq("type", "podcast"),
				Filters.gte("date", start),
				Filters.lte("date", end))).iterator() :

					docs.find(Filters.and(
							Filters.eq("type", "podcast"),
							Filters.eq("source", source), 
							Filters.gte("date", start),
							Filters.lte("date", end))).iterator();
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
				.append("youtube", replaceNA(track.getString("youtube")))
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
