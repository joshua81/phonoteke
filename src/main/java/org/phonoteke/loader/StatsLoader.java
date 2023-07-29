package org.phonoteke.loader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.client.util.Sets;
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

	private Map<String, Set<String>> affinity = Maps.newHashMap();

	private Map<String, BigDecimal> artistsScore = Maps.newHashMap();
	private Map<String, Document> artistsDocs = Maps.newHashMap();
	private List<String> artistsList = Lists.newArrayList();

	private Map<String, BigDecimal> albumsScore = Maps.newHashMap();
	private Map<String, Document> albumsDocs = Maps.newHashMap();
	private List<String> albumsList = Lists.newArrayList();

	private Map<String, BigDecimal> songsScore = Maps.newHashMap();
	private Map<String, Document> songsDocs = Maps.newHashMap();
	private List<String> songsList = Lists.newArrayList();

	private Map<String, BigDecimal> videosScore = Maps.newHashMap();
	private Map<String, Document> videosDocs = Maps.newHashMap();
	private List<String> videosList = Lists.newArrayList();


	public void load(String... args) {
		if(args.length == 0) {
			calculateStats(null);
			MongoCursor<Document> i = repo.getAuthors().find().iterator();
			while(i.hasNext()) {
				Document page = i.next();
				String source = page.getString("source");
				calculateStats(source);
			}
			calculateAffinities();
		}
		else {
			String source = args[0];
			calculateStats(source);
		}
	}

	private void calculateAffinities() {
		affinity.keySet().forEach(source -> {
			log.info("Calculating affinities " + source + "...");
			Map<String, BigDecimal> affinities = calculateAffinities(source);
			BigDecimal affinitiesTot = affinities.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

			MongoCursor<Document> j = repo.getStats().find(Filters.and(Filters.eq("source", source))).iterator();
			Document doc = j.next();
			List<Document> docs = Lists.newArrayList();
			affinities.keySet().forEach(artist -> {
				docs.add(new Document("source", artist)
						.append("affinity", affinities.get(artist).doubleValue()));
			});
			doc.append("affinities", docs);
			doc.append("affinitiesTot", affinitiesTot.doubleValue());
			repo.getStats().updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
			log.info(source + " updated");
		});
	}

	private Map<String, BigDecimal> calculateAffinities(String source) {
		Map<String, BigDecimal> affinities = Maps.newHashMap();
		affinity.keySet().forEach(artist -> {
			if(!source.equals(artist)) {
				Set<String> set1 = new HashSet<String>(affinity.get(source));
				Set<String> set2 = new HashSet<String>(affinity.get(artist));
				BigDecimal affinity;
				if(set1.size() >= set2.size()) {
					int size = set1.size();
					set1.retainAll(set2);
					affinity = new BigDecimal(set1.size()).divide(new BigDecimal(size), 4, RoundingMode.HALF_UP);
				}
				else {
					int size = set2.size();
					set2.retainAll(set1);
					affinity = new BigDecimal(set2.size()).divide(new BigDecimal(size), 4, RoundingMode.HALF_UP);
				}
				log.debug("affinity(" + source + ", " + artist + "): " + affinity);
				affinities.put(artist, affinity);
			}
		});
		return affinities;
	}

	private void calculateStats(String source) {
		log.info("Calculating stats " + source + "...");
		collectData(source);

		MongoCursor<Document> j = repo.getStats().find(Filters.and(Filters.eq("source", source))).iterator();
		Document doc = j.next();
		doc.append("artists", getTopArtists());
		doc.append("artistsVar", getArtistsVar().doubleValue());
		doc.append("albums", getTopAlbums());
		doc.append("albumsVar", getAlbumsVar().doubleValue());
		doc.append("tracks", getTopTracks());
		doc.append("tracksVar", getTracksVar().doubleValue());
		doc.append("videos", getTopVideos());
		repo.getStats().updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
		log.info(source + " updated");
	}

	private void collectData(String source) {
		artistsScore = Maps.newHashMap();
		artistsDocs = Maps.newHashMap();
		artistsList = Lists.newArrayList();

		albumsScore = Maps.newHashMap();
		albumsDocs = Maps.newHashMap();
		albumsList = Lists.newArrayList();

		songsScore = Maps.newHashMap();
		songsDocs = Maps.newHashMap();
		songsList = Lists.newArrayList();

		videosScore = Maps.newHashMap();
		videosDocs = Maps.newHashMap();
		videosList = Lists.newArrayList();

		MongoCursor<Document> i = getDocs(source);
		if(!i.hasNext()) {
			return;
		}

		i.forEachRemaining(page -> {
			List<Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isEmpty(tracks)) {
				return;
			}

			Date date = page.getDate("date");
			tracks.forEach(t -> {
				t.put("date", date);

				// Artist
				String artist = t.getString("spartistid");
				if(artist != null && !HumanBeatsUtils.NA.equals(artist)) {
					BigDecimal score = artistsScore.getOrDefault(artist, BigDecimal.ZERO);
					artistsScore.put(artist, score.add(BigDecimal.ONE));
					artistsDocs.put(artist, t);
					artistsList.add(artist);

					if(source != null) {
						affinity.putIfAbsent(source, Sets.newHashSet());
						affinity.get(source).add(artist);
					}
				}

				// Album
				String album = t.getString("spalbumid");
				if(album != null && !HumanBeatsUtils.NA.equals(album)) {
					BigDecimal score = albumsScore.getOrDefault(album, BigDecimal.ZERO);
					albumsScore.put(album, score.add(BigDecimal.ONE));
					albumsDocs.put(album, t);
					albumsList.add(album);
				}

				// Song
				String song = t.getString("spotify");
				if(song != null && !HumanBeatsUtils.NA.equals(song)) {
					BigDecimal score = songsScore.getOrDefault(song, BigDecimal.ZERO);
					songsScore.put(song, score.add(BigDecimal.ONE));
					songsDocs.put(song, t);
					songsList.add(song);
				}

				// Video
				String video = t.getString("youtube");
				if(video != null && !HumanBeatsUtils.NA.equals(video)) {
					BigDecimal score = videosScore.getOrDefault(video, BigDecimal.ZERO);
					videosScore.put(video, score.add(BigDecimal.ONE));
					videosDocs.put(video, t);
					videosList.add(video);
				}
			});
		});
	}

	private BigDecimal getArtistsVar() {
		return new BigDecimal(artistsDocs.keySet().size()).divide(new BigDecimal(artistsList.size()), 4, RoundingMode.HALF_UP);
	}

	private BigDecimal getAlbumsVar() {
		return new BigDecimal(albumsDocs.keySet().size()).divide(new BigDecimal(albumsList.size()), 4, RoundingMode.HALF_UP);
	}

	private BigDecimal getTracksVar() {
		return new BigDecimal(songsDocs.keySet().size()).divide(new BigDecimal(songsList.size()), 4, RoundingMode.HALF_UP);
	}

	private List<Document> getTopVideos() {
		List<Document> topVideos = Lists.newArrayList();
		if(MapUtils.isNotEmpty(videosScore)) {
			List<Document> jsonVideos = Lists.newArrayList(videosDocs.values());
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

			subList(jsonVideos, 100).forEach(v -> {
				log.debug("Video: " + v.getString("artist") + " - " + v.getString("album") + " - " + v.getString("track") + ": " + videoScore(v));
				topVideos.add(getVideo(v));
			});
		}
		return topVideos;
	}

	private List<Document> getTopTracks() {
		List<Document> jsonSongs = Lists.newArrayList(songsDocs.values());
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

		List<Document> topTracks = Lists.newArrayList();
		subList(jsonSongs, 100).forEach(s -> {
			log.debug("Track: " + s.getString("artist") + " - " + s.getString("album") + " - " + s.getString("track") + ": " + trackScore(s));
			topTracks.add(getTrack(s));
		});
		return topTracks;
	}

	private List<Document> getTopAlbums() {
		List<Document> jsonAlbums = Lists.newArrayList(albumsDocs.values());
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

		List<Document> topAlbums = Lists.newArrayList();
		subList(jsonAlbums, 100).forEach(a -> {
			log.debug("Album: " + a.getString("artist") + " - " + a.getString("album") + ": " + albumScore(a));
			topAlbums.add(getAlbum(a));
		});
		return topAlbums;
	}

	private List<Document> getTopArtists() {
		List<Document> jsonArtists = Lists.newArrayList(artistsDocs.values());
		Collections.sort(jsonArtists, new Comparator<Document>() {
			@Override
			public int compare(Document a1, Document a2) {
				int res = artistScore(a2).compareTo(artistScore(a1));
				return res != 0 ? res : trackDate(a2).compareTo(trackDate(a1));
			}
		});

		List<Document> topArtists = Lists.newArrayList();
		subList(jsonArtists, 100).forEach(a -> {
			log.debug("Artist: " + a.getString("artist") + ": " + artistScore(a));
			topArtists.add(getArtist(a));
		});
		return topArtists;
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
