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

		Map<String, BigDecimal> artistsScore = Maps.newHashMap();
		Map<String, Document> artists = Maps.newHashMap();

		Map<String, BigDecimal> albumsScore = Maps.newHashMap();
		Map<String, Document> albums = Maps.newHashMap();

		Map<String, BigDecimal> songsScore = Maps.newHashMap();
		Map<String, Document> songs = Maps.newHashMap();

		Map<String, BigDecimal> videosScore = Maps.newHashMap();
		Map<String, Document> videos = Maps.newHashMap();

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
					BigDecimal artistNum = artistsScore.get(artist);
					if(artistNum == null) {
						artistNum = BigDecimal.ZERO;
					}
					BigDecimal score = artistNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
					artistsScore.put(artist, score);
					artists.put(artist, t);
				}

				// Album
				String album = t.getString("spalbumid");
				if(album != null && !HumanBeats.NA.equals(album)) {
					BigDecimal albumNum = albumsScore.get(album);
					if(albumNum == null) {
						albumNum = BigDecimal.ZERO;
					}
					BigDecimal score = albumNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
					albumsScore.put(album, score);
					albums.put(album, t);
				}

				// Song
				String song = t.getString("spotify");
				if(song != null && !HumanBeats.NA.equals(song)) {
					BigDecimal songNum = songsScore.get(song);
					if(songNum == null) {
						songNum = BigDecimal.ZERO;
					}
					BigDecimal score = songNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
					songsScore.put(song, score);
					songs.put(song, t);
				}

				// Video
				String video = t.getString("youtube");
				if(video != null && !HumanBeats.NA.equals(video)) {
					BigDecimal videoNum = videosScore.get(video);
					if(videoNum == null) {
						videoNum = BigDecimal.ZERO;
					}
					BigDecimal score = videoNum.add(BigDecimal.ONE.divide(weights.get(s), MathContext.DECIMAL32));
					videosScore.put(video, score);
					videos.put(video, t);
				}
			});
		});

		MongoCursor<Document> j = stats.find(Filters.and(Filters.eq("source", source))).iterator();
		final Document doc = j.next();
		//		if(doc == null) {
		//			doc = new Document("source", source)
		//					.append("name", name);
		//		}

		// Artists
		List<Document> jsonArtists = Lists.newArrayList(artists.values());
		Collections.sort(jsonArtists, new Comparator<Document>() {
			@Override
			public int compare(Document a1, Document a2) {
				int res = artistsScore.get(a2.get("spartistid")).compareTo(artistsScore.get(a1.get("spartistid")));
				return res != 0 ? res : a2.getDate("date").compareTo(a1.getDate("date"));
			}
		});
		doc.append("artists", Lists.newArrayList());
		subList(jsonArtists, 100).forEach(a -> {
			doc.get("artists", List.class).add(getArtist(a));
		});

		// Albums
		List<Document> jsonAlbums = Lists.newArrayList(albums.values());
		Collections.sort(jsonAlbums, new Comparator<Document>() {
			@Override
			public int compare(Document a1, Document a2) {
				int res = albumsScore.get(a2.get("spalbumid")).compareTo(albumsScore.get(a1.get("spalbumid")));
				if(res != 0) {
					return res;
				}
				res = artistsScore.get(a2.get("spartistid")).compareTo(artistsScore.get(a1.get("spartistid")));
				return res != 0 ? res : a2.getDate("date").compareTo(a1.getDate("date"));
			}
		});
		doc.append("albums", Lists.newArrayList());
		subList(jsonAlbums, 100).forEach(a -> {
			LOGGER.info("Album " +  a.get("spalbumid") + ": " + albumsScore.get(a.get("spalbumid")));
			doc.get("albums", List.class).add(getAlbum(a));
		});

		// Tracks
		List<Document> jsonSongs = Lists.newArrayList(songs.values());
		Collections.sort(jsonSongs, new Comparator<Document>() {
			@Override
			public int compare(Document s1, Document s2) {
				int res = songsScore.get(s2.get("spotify")).compareTo(songsScore.get(s1.get("spotify")));
				if(res != 0) {
					return res;
				}
				res = albumsScore.get(s2.get("spalbumid")).compareTo(albumsScore.get(s1.get("spalbumid")));
				if(res != 0) {
					return res;
				}
				res = artistsScore.get(s2.get("spartistid")).compareTo(artistsScore.get(s1.get("spartistid")));
				return res != 0 ? res : s2.getDate("date").compareTo(s1.getDate("date"));
			}
		});
		doc.append("tracks", Lists.newArrayList());
		subList(jsonSongs, 100).forEach(s -> {
			LOGGER.info("Track " +  s.get("spotify") + ": " + songsScore.get(s.get("spotify")));
			doc.get("tracks", List.class).add(getTrack(s));
		});

		// Videos
		if(MapUtils.isNotEmpty(videosScore)) {
			List<Document> jsonVideos = Lists.newArrayList(videos.values());
			Collections.sort(jsonVideos, new Comparator<Document>() {
				@Override
				public int compare(Document v1, Document v2) {
					int res = videosScore.get(v2.get("youtube")).compareTo(videosScore.get(v1.get("youtube")));
					if(res != 0) {
						return res;
					}
					res = albumsScore.get(v2.get("spalbumid")).compareTo(albumsScore.get(v1.get("spalbumid")));
					if(res != 0) {
						return res;
					}
					res = artistsScore.get(v2.get("spartistid")).compareTo(artistsScore.get(v1.get("spartistid")));
					return res != 0 ? res : v2.getDate("date").compareTo(v1.getDate("date"));
				}
			});
			doc.append("videos", Lists.newArrayList());
			subList(jsonVideos, 100).forEach(v -> {
				LOGGER.info("Video " +  v.get("youtube") + ": " + videosScore.get(v.get("youtube")));
				doc.get("videos", List.class).add(getVideo(v));
			});
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
