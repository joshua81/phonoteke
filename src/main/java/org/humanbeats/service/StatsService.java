package org.humanbeats.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.bson.Document;
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.util.HumanBeatsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StatsService
{
	@Autowired
	private MongoRepository repo;

	private Map<String, BigDecimal> artistsScore = Maps.newHashMap();
	private List<String> artistsList = Lists.newArrayList();

	private Map<String, Document> albumsDocs = Maps.newHashMap();
	private List<String> albumsList = Lists.newArrayList();

	private Map<String, Document> songsDocs = Maps.newHashMap();
	private List<String> songsList = Lists.newArrayList();

	private Map<String, Document> videosDocs = Maps.newHashMap();
	private List<String> videosList = Lists.newArrayList();

	private String source;
	private Integer year;


	public void load(String... args) {
		if(args.length == 0) {
			calculateStats();
			MongoCursor<Document> i = repo.getAuthors().find().iterator();
			while(i.hasNext()) {
				Document page = i.next();
				source = page.getString("source");
				calculateStats();
			}
		}
		else {
			source = args[0];
			year = args.length == 2 ? Integer.parseInt(args[1]) : null;
			calculateStats();
		}
	}

	private void calculateStats() {
		log.info("Calculating stats: " + source + ", " + year + "...");
		collectData();

		MongoCursor<Document> i = repo.getStats().find(Filters.and(Filters.eq("source", source))).iterator();
		Document doc = i.next();
		if(year == null) {
			doc.append("albums", getAlbums());
			doc.append("albumsVar", getAlbumsVar().doubleValue());
			doc.append("tracks", getTracks());
			doc.append("tracksVar", getTracksVar().doubleValue());
			doc.append("videos", getVideos());
			doc.append("reviews", getReviews());
		}
		else {
			Document yearDoc = new Document();
			yearDoc.append("albums", getAlbums());
			yearDoc.append("albumsVar", getAlbumsVar().doubleValue());
			yearDoc.append("tracks", getTracks());
			yearDoc.append("tracksVar", getTracksVar().doubleValue());
			yearDoc.append("videos", getVideos());
			yearDoc.append("reviews", getReviews());
			doc.append(year.toString(), yearDoc);
		}
		repo.getStats().updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
		log.info(source + " updated");
	}

	private void collectData() {
		artistsScore = Maps.newHashMap();
		artistsList = Lists.newArrayList();

		albumsDocs = Maps.newHashMap();
		albumsList = Lists.newArrayList();

		songsDocs = Maps.newHashMap();
		songsList = Lists.newArrayList();

		videosDocs = Maps.newHashMap();
		videosList = Lists.newArrayList();

		MongoCursor<Document> i = getDocs();
		if(!i.hasNext()) {
			return;
		}

		i.forEachRemaining(page -> {
			List<Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isEmpty(tracks)) {
				return;
			}

			Date date = page.getDate("date");
			String src = page.getString("source");
			tracks.forEach(t -> {
				t.put("date", date);
				t.put("source", src);
				t.put("index", tracks.indexOf(t));

				// Artist
				String artist = t.getString("spartistid");
				if(artist != null && !HumanBeatsUtils.NA.equals(artist)) {
					BigDecimal score = artistsScore.getOrDefault(artist, BigDecimal.ZERO);
					artistsScore.put(artist, score.add(BigDecimal.ONE));
					artistsList.add(artist);
				}

				// Album
				String album = t.getString("spalbumid");
				if(album != null && !HumanBeatsUtils.NA.equals(album)) {
					albumsDocs.put(album, t);
					albumsList.add(album);
				}

				// Song
				String song = t.getString("spotify");
				if(song != null && !HumanBeatsUtils.NA.equals(song)) {
					songsDocs.put(song, t);
					songsList.add(song);
				}

				// Video
				String video = t.getString("youtube");
				if(video != null && !HumanBeatsUtils.NA.equals(video)) {
					videosDocs.put(video, t);
					videosList.add(video);
				}
			});
		});
	}

	private BigDecimal getAlbumsVar() {
		return new BigDecimal(albumsDocs.keySet().size()).divide(new BigDecimal(albumsList.size()), 4, RoundingMode.HALF_UP);
	}

	private BigDecimal getTracksVar() {
		return new BigDecimal(songsDocs.keySet().size()).divide(new BigDecimal(songsList.size()), 4, RoundingMode.HALF_UP);
	}

	private List<Document> getVideos() {
		List<Document> topVideos = Lists.newArrayList();
		if(MapUtils.isNotEmpty(videosDocs)) {
			List<Document> jsonVideos = Lists.newArrayList(videosDocs.values());
			Collections.sort(jsonVideos, new Comparator<Document>() {
				@Override
				public int compare(Document v1, Document v2) {
					// date desc
					int res = documentDate(v2).compareTo(documentDate(v1));
					// source asc
					res = res != 0 ? res : documentSource(v1).compareTo(documentSource(v2));
					// index asc
					return res != 0 ? res : documentIndex(v1).compareTo(documentIndex(v2));
				}
			});

			subList(jsonVideos).forEach(v -> {
				log.debug("Video: " + v.getString("artist") + " - " + v.getString("album") + " - " + v.getString("track"));
				topVideos.add(getVideo(v));
			});
		}
		return topVideos;
	}

	private List<Document> getReviews() {
		List<Document> topReviews = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(artistsList)) {
			MongoCursor<Document> i = repo.getDocs().find(Filters.and(
					Filters.eq("source", "ondarock"), 
					Filters.eq("type", "album"), 
					Filters.in("spartistid", artistsList)))
					.sort(new BasicDBObject("year", -1).append("date", -1)).limit(100).iterator();
			while(i.hasNext()) {
				Document page = i.next();
				topReviews.add(getReview(page));
			}
		}
		return topReviews;
	}

	private List<Document> getTracks() {
		List<Document> jsonSongs = Lists.newArrayList(songsDocs.values());
		Collections.sort(jsonSongs, new Comparator<Document>() {
			@Override
			public int compare(Document s1, Document s2) {
				// date desc
				int res = documentDate(s2).compareTo(documentDate(s1));
				// source asc
				res = res != 0 ? res : documentSource(s1).compareTo(documentSource(s2));
				// index asc
				return res != 0 ? res : documentIndex(s1).compareTo(documentIndex(s2));
			}
		});

		List<Document> topTracks = Lists.newArrayList();
		subList(jsonSongs).forEach(s -> {
			log.debug("Track: " + s.getString("artist") + " - " + s.getString("album") + " - " + s.getString("track"));
			topTracks.add(getTrack(s));
		});
		return topTracks;
	}

	private List<Document> getAlbums() {
		List<Document> jsonAlbums = Lists.newArrayList(albumsDocs.values());
		Collections.sort(jsonAlbums, new Comparator<Document>() {
			@Override
			public int compare(Document a1, Document a2) {
				// artist score desc
				int res = artistScore(a2).compareTo(artistScore(a1));
				// artist asc
				return res != 0 ? res : documentArtist(a1).compareTo(documentArtist(a2));
			}
		});

		List<Document> topAlbums = Lists.newArrayList();
		subList(jsonAlbums).forEach(a -> {
			log.debug("Album: " + a.getString("artist") + " - " + a.getString("album"));
			topAlbums.add(getAlbum(a));
		});
		return topAlbums;
	}

	private MongoCursor<Document> getDocs() {
		// all episodes in the last month
		if(source == null) {
			LocalDateTime end = LocalDateTime.now();
			LocalDateTime start = end.minusMonths(1);
			return repo.getDocs().find(Filters.and(
					Filters.eq("type", "podcast"),
					Filters.gte("date", start),
					Filters.lte("date", end)))
					.sort(new BasicDBObject("date", -1)).iterator();
		}
		// last 30 episodes
		else if(year == null){
			return repo.getDocs().find(Filters.and(
					Filters.eq("type", "podcast"),
					Filters.eq("source", source)))
					.sort(new BasicDBObject("date", -1))
					.limit(30).iterator();
		}
		// all episodes in the specified year
		else {
			LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59);
			LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
			return repo.getDocs().find(Filters.and(
					Filters.eq("type", "podcast"),
					Filters.eq("source", source),
					Filters.gte("date", start),
					Filters.lte("date", end)))
					.sort(new BasicDBObject("date", -1)).iterator();
		}
	}

	private Document getAlbum(Document track) {
		return new Document("artist", track.getString("artist"))
				.append("album", track.getString("album"))
				.append("spartistid", track.getString("spartistid"))
				.append("spalbumid", track.getString("spalbumid"))
				.append("cover", track.getString("coverM"));
	}

	private Document getTrack(Document track) {
		return new Document("artist", track.getString("artist"))
				.append("album", track.getString("album"))
				.append("track", track.getString("track"))
				.append("spartistid", track.getString("spartistid"))
				.append("spalbumid", track.getString("spalbumid"))
				.append("spotify", track.getString("spotify"))
				.append("cover", track.getString("coverS"))
				.append("youtube", replaceNA(track.getString("youtube")));
	}

	private Document getVideo(Document track) {
		return new Document("artist", track.getString("artist"))
				.append("album", track.getString("album"))
				.append("track", track.getString("track"))
				.append("spartistid", track.getString("spartistid"))
				.append("spalbumid", track.getString("spalbumid"))
				.append("youtube", track.getString("youtube"));
	}

	private Document getReview(Document review) {
		log.debug("Review: " + review.getString("artist") + " - " + review.getString("title"));
		return new Document("id", review.getString("id"))
				.append("artist", review.getString("artist"))
				.append("album", review.getString("title"))
				.append("cover", review.getString("coverM"))
				.append("year", review.getInteger("year"));
	}

	private String replaceNA(String val) {
		return HumanBeatsUtils.NA.equals(val) ? null : val;
	}

	private List<Document> subList(List<Document> list) {
		return (year != null || list.size() <= 100) ? list : list.subList(0, 100);
	}

	private BigDecimal artistScore(Document track) {
		return artistsScore.getOrDefault(track.get("spartistid"), BigDecimal.ZERO);
	}

	private Date documentDate(Document doc) {
		return doc.getDate("date");
	}

	private Integer documentIndex(Document doc) {
		return doc.getInteger("index");
	}

	private String documentSource(Document doc) {
		return doc.getString("source");
	}

	private String documentArtist(Document doc) {
		return doc.getString("artist");
	}
}
