package org.phonoteke.loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DiscogsLoader
{
	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";

	@Autowired
	private MongoRepository repo;


	public void load(String... args) {
		log.info("Loading Discogs...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.or(
				Filters.and(Filters.eq("type", "album"), Filters.ne("albumid", null), Filters.ne("albumid", HumanBeatsUtils.NA)),
				Filters.and(Filters.eq("type", "podcast"), Filters.ne("tracks.albumtid", null), Filters.ne("tracks.albumtid", HumanBeatsUtils.NA)))).iterator();

		while(i.hasNext()) {
			Document page = i.next();
			String id = page.getString("id"); 
			String type = page.getString("type");
			if("album".equals(type)) {
				loadAlbumDGId(page);
			}
			else if("podcast".equals(type)) {
				loadTracksDGId(page);
			}
			repo.getDocs().updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
		}
	}

	private void loadAlbumDGId(org.bson.Document page) {
		String albumId = page.getString("albumid");
		String dgalbumId = page.getString("dgalbumid");

		log.debug("Loading Album " + albumId);
		org.bson.Document dgalbum = getAlbum(albumId);
		if(dgalbum != null) {
			dgalbumId = getAlbumId(dgalbum);
		}
		page.append("dgalbumid", dgalbumId == null ? HumanBeatsUtils.NA : dgalbumId);
		log.info(albumId + ": " + dgalbumId);
	}

	private org.bson.Document getAlbum(String albumId) {
		if(StringUtils.isBlank(albumId)) {
			return null;
		}
		String url = MUSICBRAINZ + "/release-group/" + albumId.trim() + "?fmt=json";
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
			return org.bson.Document.parse(json);
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

	private void loadTracksDGId(org.bson.Document page) {
		List<org.bson.Document> tracks = page.get("tracks", List.class);
		for(org.bson.Document track : tracks) {
			String albumId = track.getString("albumid");
			String dgalbumId = page.getString("dgalbumid");

			log.debug("Loading Album " + albumId);
			org.bson.Document dgalbum = getAlbum(albumId);
			if(dgalbum != null) {
				dgalbumId = getAlbumId(dgalbum);
			}
			track.append("dgalbumid", dgalbumId == null ? HumanBeatsUtils.NA : dgalbumId);
			log.info(albumId + ": " + dgalbumId);
		}
	}

	private String getAlbumId(org.bson.Document album) {
		// ex. https://musicbrainz.org/ws/2/release-group/3bd76d40-7f0e-36b7-9348-91a33afee20e?inc=url-rels&fmt=json
		// relations[].type = 'discogs' -> relations[].url.resource
		return null;
		//		TreeMap<Integer, String> scores = Maps.newTreeMap();
		//		List<org.bson.Document> releases = album.get("releases", List.class);
		//		if(CollectionUtils.isNotEmpty(releases)) {
		//			for(org.bson.Document release : releases) {
		//				int score = release == null ? 0 : release.getInteger("score");
		//				String mbartist = getRecordingArtist(release);
		//				String mbtitle = getRecordingTitle(release);
		//				int scoreTitle = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbtitle);
		//				if(score >= HumanBeatsUtils.THRESHOLD && scoreTitle >= HumanBeatsUtils.THRESHOLD) {
		//					String albumId =  getRecordingAlbumId(release);
		//					scores.put(scoreTitle, albumId);
		//				}
		//			}
		//		}
		//		return CollectionUtils.isEmpty(scores.keySet()) ? HumanBeatsUtils.NA : scores.get(scores.lastEntry().getKey());
	}
}
