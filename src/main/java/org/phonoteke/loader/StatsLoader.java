package org.phonoteke.loader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.internal.operation.OrderBy;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StatsLoader
{
	@Autowired
	private MongoRepository repo;

	private Map<String, BigDecimal> artistsScore = Maps.newHashMap();
	private Map<String, Document> artists = Maps.newHashMap();

	private Map<String, BigDecimal> albumsScore = Maps.newHashMap();
	private Map<String, Document> albums = Maps.newHashMap();

	private Map<String, BigDecimal> songsScore = Maps.newHashMap();
	private Map<String, Document> songs = Maps.newHashMap();

	private Map<String, BigDecimal> videosScore = Maps.newHashMap();
	private Map<String, Document> videos = Maps.newHashMap();


	public void load(String... args)
	{
		if(args.length == 0) {
			loadPodcast(null);

			MongoCursor<Document> i = repo.getAuthors().find().iterator();
			while(i.hasNext()) {
				Document page = i.next();
				String source = page.getString("source");
				loadPodcast(source);
			}
		}
		else {
			String source = args[0];
			loadPodcast(source);
		}
	}

	private void loadPodcast(String source) {
		log.info("Calculating stats " + source + "...");
		MongoCursor<Document> i = getDocs(source);
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
				if(artist != null && !HumanBeatsUtils.NA.equals(artist)) {
					BigDecimal score = artistsScore.getOrDefault(artist, BigDecimal.ZERO);
					artistsScore.put(artist, score.add(BigDecimal.ONE));
					artists.put(artist, t);
				}

				// Album
				String album = t.getString("spalbumid");
				if(album != null && !HumanBeatsUtils.NA.equals(album)) {
					BigDecimal score = albumsScore.getOrDefault(album, BigDecimal.ZERO);
					albumsScore.put(album, score.add(BigDecimal.ONE));
					albums.put(album, t);
				}

				// Song
				String song = t.getString("spotify");
				if(song != null && !HumanBeatsUtils.NA.equals(song)) {
					BigDecimal score = songsScore.getOrDefault(song, BigDecimal.ZERO);
					songsScore.put(song, score.add(BigDecimal.ONE));
					songs.put(song, t);
				}

				// Video
				String video = t.getString("youtube");
				if(video != null && !HumanBeatsUtils.NA.equals(video)) {
					BigDecimal score = videosScore.getOrDefault(video, BigDecimal.ZERO);
					videosScore.put(video, score.add(BigDecimal.ONE));
					videos.put(video, t);
				}
			});
		});

		MongoCursor<Document> j = repo.getStats().find(Filters.and(Filters.eq("source", source))).iterator();
		final Document doc = j.next();

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
			log.info("Artist: " + a.getString("artist") + ": " + artistScore(a));
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
			log.info("Album: " + a.getString("artist") + " - " + a.getString("album") + ": " + albumScore(a));
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
			log.info("Track: " + s.getString("artist") + " - " + s.getString("album") + " - " + s.getString("track") + ": " + trackScore(s));
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
				log.info("Video: " + v.getString("artist") + " - " + v.getString("album") + " - " + v.getString("track") + ": " + videoScore(v));
				doc.get("videos", List.class).add(getVideo(v));
			});
		}

		repo.getStats().updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
		log.info(source + " updated");
	}

	private MongoCursor<Document> getDocs(String source) {
		if(source == null) {
			LocalDateTime end = LocalDateTime.now();
			LocalDateTime start = end.minusMonths(1);
			return repo.getDocs().find(Filters.and(
					Filters.eq("type", "podcast"),
					Filters.gte("date", start),
					Filters.lte("date", end)))
					.sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).iterator();
		}
		else {
			return repo.getDocs().find(Filters.and(
					Filters.eq("type", "podcast"),
					Filters.eq("source", source)))
					.sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation()))
					.limit(30).iterator();
		}
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
		return HumanBeatsUtils.NA.equals(val) ? null : val;
	}

	private List<Document> subList(List<Document> list, int size) {
		return list.size() <= size ? list : list.subList(0, size);
	}

	private BigDecimal artistScore(Document track) {
		return artistsScore.getOrDefault(track.get("spartistid"), BigDecimal.ZERO);
	}

	private BigDecimal albumScore(Document track) {
		return albumsScore.getOrDefault(track.get("spalbumid"), BigDecimal.ZERO);
	}

	private BigDecimal videoScore(Document track) {
		return videosScore.getOrDefault(track.get("youtube"), BigDecimal.ZERO);
	}

	private BigDecimal trackScore(Document track) {
		return songsScore.getOrDefault(track.get("spotify"), BigDecimal.ZERO);
	}

	private Date trackDate(Document track) {
		return track.getDate("date");
	}
}
