package org.humanbeats.loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.humanbeats.util.HumanBeatsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Maps;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@Component
@Slf4j
public class MusicbrainzLoader
{
	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";

	@Autowired
	private MongoRepository repo;


	public void load(String... args) {
		log.info("Loading Musicbrainz...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.or(
				Filters.and(Filters.ne("type", "podcast"), Filters.eq("artistid", null)),
				Filters.and(Filters.eq("type", "podcast"), Filters.eq("tracks.artistid", null)))).iterator();

		while(i.hasNext()) {
			Document page = i.next();
			String id = page.getString("id"); 
			String type = page.getString("type");
			if("album".equals(type)) {
				loadAlbumMBId(page);
			}
			else if("podcast".equals(type)) {
				loadTracksMBId(page);
			}
			repo.getDocs().updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
		}
	}

	private void loadAlbumMBId(org.bson.Document page) {
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");
		String albumId = page.getString("albumid");
		String title = page.getString("title");

		log.debug("Loading Album " + id);
		org.bson.Document mbalbum = getAlbum(artist, title);
		if(mbalbum != null && mbalbum.getInteger("count") > 0) {
			artistId = getArtistId(artist + " - " + title, mbalbum);
			albumId = getAlbumId(artist + " - " + title, mbalbum);
		}
		page.append("artistid", artistId == null ? HumanBeatsUtils.NA : artistId);
		page.append("albumid", albumId == null ? HumanBeatsUtils.NA : albumId);
		log.info(artist + " - " + title + ": " + artistId + " - " + albumId);
	}

	private org.bson.Document getAlbum(String artist, String title) {
		if(StringUtils.isBlank(artist) || StringUtils.isBlank(title)) {
			return null;
		}
		String url = MUSICBRAINZ + "/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20release:" + title.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
	}

	private org.bson.Document callMusicbrainz(String url) {
		HttpURLConnection con;
		try {
			con = (HttpURLConnection)new URL(url).openConnection();
		} 
		catch(Throwable t) {
			log.error("ERROR: " + t.getMessage());
			return null;
		}

		try(BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String json = "";
			String line = null;
			while ((line = rd.readLine()) != null) {
				json += line;
			}
			org.bson.Document doc = org.bson.Document.parse(json);
			return (doc != null && doc.getInteger("count") > 0) ? doc : null;
		}
		catch(Throwable t) {
			log.error("ERROR: " + t.getMessage());
			return null;
		}
		finally {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	private void loadTracksMBId(org.bson.Document page) {
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		for(org.bson.Document track : tracks) {
			String artistId = track.getString("artistid");
			String albumId = track.getString("albumid");
			String album = track.getString("album");
			String artist = track.getString("artist");
			if(artistId == null || albumId == null) {
				if(album != null && artist != null) {
					log.debug("Loading Album " + artist + " - " + album);
					org.bson.Document mbalbum = getAlbum(artist, album);
					if(mbalbum != null && mbalbum.getInteger("count") > 0) {
						artistId = getArtistId(artist + " - " + album, mbalbum);
						albumId = getAlbumId(artist + " - " + album, mbalbum);
					}
				}
				track.append("artistid", artistId == null ? HumanBeatsUtils.NA : artistId);
				track.append("albumid", albumId == null ? HumanBeatsUtils.NA : albumId);
				log.info(artist + " - " + album + ": " + artistId + " - " + albumId);
			}
		}
	}

	private String getArtistId(String title, org.bson.Document album) {
		TreeMap<Integer, String> scores = Maps.newTreeMap();
		List<org.bson.Document> releases = album.get("releases", List.class);
		if(CollectionUtils.isNotEmpty(releases)) {
			for(org.bson.Document release : releases) {
				int score = release == null ? 0 : release.getInteger("score");
				String mbartist = getRecordingArtist(release);
				String mbtitle = getRecordingTitle(release);
				int scoreTitle = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbtitle);
				if(score >= HumanBeatsUtils.THRESHOLD && scoreTitle >= HumanBeatsUtils.THRESHOLD) {
					String artistId =  getRecordingArtistId(release);
					scores.put(scoreTitle, artistId);
				}
			}
		}
		return CollectionUtils.isEmpty(scores.keySet()) ? HumanBeatsUtils.NA : scores.get(scores.lastEntry().getKey());
	}

	private String getAlbumId(String title, org.bson.Document album) {
		TreeMap<Integer, String> scores = Maps.newTreeMap();
		List<org.bson.Document> releases = album.get("releases", List.class);
		if(CollectionUtils.isNotEmpty(releases)) {
			for(org.bson.Document release : releases) {
				int score = release == null ? 0 : release.getInteger("score");
				String mbartist = getRecordingArtist(release);
				String mbtitle = getRecordingTitle(release);
				int scoreTitle = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbtitle);
				if(score >= HumanBeatsUtils.THRESHOLD && scoreTitle >= HumanBeatsUtils.THRESHOLD) {
					String albumId =  getRecordingAlbumId(release);
					scores.put(scoreTitle, albumId);
				}
			}
		}
		return CollectionUtils.isEmpty(scores.keySet()) ? HumanBeatsUtils.NA : scores.get(scores.lastEntry().getKey());
	}

	private String getRecordingArtistId(org.bson.Document recording) {
		return CollectionUtils.isEmpty(recording.get("artist-credit", List.class)) ? null : 
			((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).getString("id");
	}

	private String getRecordingAlbumId(org.bson.Document recording) {
		return recording.get("release-group", org.bson.Document.class).getString("id");
	}

	private String getRecordingArtist(org.bson.Document recording) {
		String artist = "";
		if(recording != null) {
			if(CollectionUtils.isNotEmpty(recording.get("artist-credit", List.class))) {
				for(org.bson.Document credit : (List<org.bson.Document>)recording.get("artist-credit", List.class)) {
					artist += credit.getString("name");
					String joinphrase = credit.getString("joinphrase");
					if(joinphrase != null) {
						artist += joinphrase;
					}
				}
			}
		}
		return artist;
	}

	private String getRecordingTitle(org.bson.Document recording) {
		String title = "";
		if(recording != null) {
			title = recording.getString("title");
		}
		return title;
	}
}
