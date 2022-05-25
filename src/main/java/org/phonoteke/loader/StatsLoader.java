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
	private static final BigDecimal MIN_WEIGHT = new BigDecimal(4);

	private Map<String, BigDecimal> artistsScore = Maps.newHashMap();
	private Map<String, Document> artists = Maps.newHashMap();

	private Map<String, BigDecimal> albumsScore = Maps.newHashMap();
	private Map<String, Document> albums = Maps.newHashMap();

	private Map<String, BigDecimal> songsScore = Maps.newHashMap();
	private Map<String, Document> songs = Maps.newHashMap();

	private Map<String, BigDecimal> videosScore = Maps.newHashMap();
	private Map<String, Document> videos = Maps.newHashMap();

	private Map<String, BigDecimal> weights = Maps.newHashMap();

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
		weights = Maps.newHashMap();
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

		artistsScore = Maps.newHashMap();
		artists = Maps.newHashMap();

		albumsScore = Maps.newHashMap();
		albums = Maps.newHashMap();

		songsScore = Maps.newHashMap();
		songs = Maps.newHashMap();

		videosScore = Maps.newHashMap();
		videos = Maps.newHashMap();

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
					BigDecimal score = artistNum.add(getScore(s));
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
					BigDecimal score = albumNum.add(getScore(s));
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
					BigDecimal score = songNum.add(getScore(s));
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
					BigDecimal score = videoNum.add(getScore(s));
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
				int res = artistScore(a2).compareTo(artistScore(a1));
				return res != 0 ? res : trackDate(a2).compareTo(trackDate(a1));
			}
		});
		doc.append("artists", Lists.newArrayList());
		subList(jsonArtists, 100).forEach(a -> {
			LOGGER.info("Artist: " + artistScore(a) + "|" + trackDate(a));
			doc.get("artists", List.class).add(getArtist(a));
		});

		// Albums
		List<Document> jsonAlbums = Lists.newArrayList(albums.values());
		Collections.sort(jsonAlbums, new Comparator<Document>() {
			@Override
			public int compare(Document a1, Document a2) {
				int res = albumScore(a2).compareTo(albumScore(a1));
				if(res != 0) {
					return res;
				}
				res = artistScore(a2).compareTo(artistScore(a1));
				return res != 0 ? res : trackDate(a2).compareTo(trackDate(a1));
			}
		});
		doc.append("albums", Lists.newArrayList());
		subList(jsonAlbums, 100).forEach(a -> {
			LOGGER.info("Album: " + albumScore(a) + "|" + artistScore(a) + "|" + trackDate(a));
			doc.get("albums", List.class).add(getAlbum(a));
		});

		// Tracks
		List<Document> jsonSongs = Lists.newArrayList(songs.values());
		Collections.sort(jsonSongs, new Comparator<Document>() {
			@Override
			public int compare(Document s1, Document s2) {
				int res = trackScore(s2).compareTo(trackScore(s1));
				if(res != 0) {
					return res;
				}
				res = albumScore(s2).compareTo(albumScore(s1));
				if(res != 0) {
					return res;
				}
				res = artistScore(s2).compareTo(artistScore(s1));
				return res != 0 ? res : trackDate(s2).compareTo(trackDate(s1));
			}
		});
		doc.append("tracks", Lists.newArrayList());
		subList(jsonSongs, 100).forEach(s -> {
			LOGGER.info("Track: " + trackScore(s) + "|" + albumScore(s) + "|" + artistScore(s) + "|" + trackDate(s));
			doc.get("tracks", List.class).add(getTrack(s));
		});

		// Videos
		if(MapUtils.isNotEmpty(videosScore)) {
			List<Document> jsonVideos = Lists.newArrayList(videos.values());
			Collections.sort(jsonVideos, new Comparator<Document>() {
				@Override
				public int compare(Document v1, Document v2) {
					int res = videoScore(v2).compareTo(videoScore(v1));
					if(res != 0) {
						return res;
					}
					res = albumScore(v2).compareTo(albumScore(v1));
					if(res != 0) {
						return res;
					}
					res = artistScore(v2).compareTo(artistScore(v1));
					return res != 0 ? res : trackDate(v2).compareTo(trackDate(v1));
				}
			});
			doc.append("videos", Lists.newArrayList());
			subList(jsonVideos, 100).forEach(v -> {
				LOGGER.info("Video: " + videoScore(v) + "|" + albumScore(v) + "|" + artistScore(v) + "|" + trackDate(v));
				doc.get("videos", List.class).add(getVideo(v));
			});
		}

		stats.updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
		LOGGER.info(source + " updated");
	}

	private MongoCursor<Document> getDocs(String source, Date lastEpisode) {
		LocalDateTime end = lastEpisode == null ? LocalDateTime.now() : LocalDateTime.ofInstant(lastEpisode.toInstant(), ZoneOffset.UTC);
		LocalDateTime start = end.minusMonths(1);

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

	private BigDecimal getScore(String source) {
		return BigDecimal.ONE.divide(weights.get(source).max(MIN_WEIGHT), MathContext.DECIMAL32);
	}

	private String replaceNA(String val) {
		return HumanBeats.NA.equals(val) ? null : val;
	}

	private List<Document> subList(List<Document> list, int size) {
		return list.size() <= size ? list : list.subList(0, size);
	}

	private BigDecimal artistScore(Document track) {
		return artistsScore.get(track.get("spartistid"));
	}

	private BigDecimal albumScore(Document track) {
		return albumsScore.get(track.get("spalbumid"));
	}

	private BigDecimal videoScore(Document track) {
		return videosScore.get(track.get("youtube"));
	}

	private BigDecimal trackScore(Document track) {
		return songsScore.get(track.get("spotify"));
	}

	private Date trackDate(Document track) {
		return track.getDate("date");
	}
}
