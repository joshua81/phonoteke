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
			List<Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isEmpty(tracks)) {
				return;
			}

			String s = page.getString("source");
			Date date = page.getDate("date");
			tracks.forEach(t -> {
				t.put("date", date);

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
		for(BigDecimal score : topArtists.descendingKeySet()) {
			Collections.sort(topArtists.get(score), new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					Document a1 = trackArtists.get(o1);
					Document a2 = trackArtists.get(o2);
					return a2.getDate("date").compareTo(a1.getDate("date"));
				}
			});
			topArtists.get(score).forEach(a -> {
				Document artist = trackArtists.get(a);
				LOGGER.info("Artist: " + a + " score: " + score);
				jsonArtists.add(getArtist(artist));
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
		for(BigDecimal score : topAlbums.descendingKeySet()) {
			Collections.sort(topAlbums.get(score), new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					Document a1 = trackAlbums.get(o1);
					Document a2 = trackAlbums.get(o2);
					int res = artists.get(a2.get("spartistid")).compareTo(artists.get(a1.get("spartistid")));
					return res != 0 ? res : a2.getDate("date").compareTo(a1.getDate("date"));
				}
			});
			topAlbums.get(score).forEach(a -> {
				Document album = trackAlbums.get(a);
				BigDecimal artistScore = artists.get(album.get("spartistid"));
				LOGGER.info("Album: " + a + " score: " + score + " artist: " + artistScore);
				jsonAlbums.add(getAlbum(album));
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
		for(BigDecimal score : topSongs.descendingKeySet()) {
			Collections.sort(topSongs.get(score), new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					Document s1 = trackSongs.get(o1);
					Document s2 = trackSongs.get(o2);
					int res = albums.get(s2.get("spalbumid")).compareTo(albums.get(s1.get("spalbumid")));
					if(res == 0) {
						res = artists.get(s2.get("spartistid")).compareTo(artists.get(s1.get("spartistid")));						
					}
					return res != 0 ? res : s2.getDate("date").compareTo(s1.getDate("date"));
				}
			});
			topSongs.get(score).forEach(s -> {
				Document track = trackSongs.get(s);
				BigDecimal albumScore = albums.get(track.get("spalbumid"));
				BigDecimal artistScore = artists.get(track.get("spartistid"));
				LOGGER.info("Track: " + s + " score: " + score + " album: " +  albumScore + " artist: " + artistScore);
				jsonSongs.add(getTrack(track));
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
			for(BigDecimal score : topVideos.descendingKeySet()) {
				Collections.sort(topVideos.get(score), new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						Document v1 = trackVideos.get(o1);
						Document v2 = trackVideos.get(o2);
						int res = albums.get(v2.get("spalbumid")).compareTo(albums.get(v1.get("spalbumid")));
						if(res == 0) {
							res = artists.get(v2.get("spartistid")).compareTo(artists.get(v1.get("spartistid")));
						}
						return res != 0 ? res : v2.getDate("date").compareTo(v1.getDate("date"));
					}
				});
				topVideos.get(score).forEach(v -> {
					Document video = trackVideos.get(v);
					BigDecimal albumScore = albums.get(video.get("spalbumid"));
					BigDecimal artistScore = artists.get(video.get("spartistid"));
					LOGGER.info("Video: " + v + " score: " + score + " album: " +  albumScore + " artist: " + artistScore);
					jsonVideos.add(getVideo(video));
				});
			}
			doc.append("videos", subList(jsonVideos, 100));
		}

		stats.updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
		LOGGER.info(source + " updated");
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

	private Document getArtist(Document track) {
		return new Document("artist", track.getString("artist"))
				.append("spartistid", track.getString("spartistid"))
				.append("artistid", replaceNA(track.getString("artistid")));
	}

	private Document getAlbum(Document track) {
		return new Document("artist", track.getString("artist"))
				.append("album", track.getString("album"))
				.append("spartistid", track.getString("spartistid"))
				.append("spalbumid", track.getString("spalbumid"))
				.append("cover", track.getString("coverM"))
				.append("artistid", replaceNA(track.getString("artistid")));
	}

	private Document getTrack(Document track) {
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

	private Document getVideo(Document track) {
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
