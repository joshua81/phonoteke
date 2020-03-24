package org.phonoteke.loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.api.client.util.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MusicbrainzLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(MusicbrainzLoader.class);

	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";


	public static void main(String[] args)
	{
		new MusicbrainzLoader().loadTracks();
		new MusicbrainzLoader().loadAlbums();
		new MusicbrainzLoader().loadArtists();
	}

	private void loadTracks() {
		LOGGER.info("Loading Tracks...");
		for(int p = 0; p < 20; p++)
		{
			MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.podcast.name()), Filters.eq("tracks.artistid", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).skip(p*2000).limit(2000).noCursorTimeout(true).iterator();
			while(i.hasNext())
			{
				Document page = i.next();
				String id = page.getString("id"); 
				loadTracksMBId(page);
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
	}

	private void loadAlbums() {
		LOGGER.info("Loading Albums...");
		for(int p = 0; p < 20; p++)
		{
			MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.album.name()), Filters.eq("albumid", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).skip(p*2000).limit(2000).noCursorTimeout(true).iterator();
			while(i.hasNext())
			{
				Document page = i.next();
				String id = page.getString("id"); 
				loadAlbumMBId(page);
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
	}

	private void loadArtists() {
		LOGGER.info("Loading Artists...");
		for(int p = 0; p < 20; p++)
		{
			MongoCursor<Document> i = docs.find(Filters.and(Filters.and(Filters.ne("type", TYPE.album.name()), Filters.ne("type", TYPE.podcast.name())), Filters.eq("artistid", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).skip(p*2000).limit(2000).noCursorTimeout(true).iterator();
			while(i.hasNext())
			{
				Document page = i.next();
				String id = page.getString("id"); 
				loadArtistMBId(page);
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
	}

	private void loadAlbumMBId(org.bson.Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");
		String title = page.getString("title");
		String albumId = page.getString("albumid");
		String source = page.getString("source");

		if(OndarockLoader.SOURCE.equals(source) && (artistId == null || albumId == null))
		{
			LOGGER.debug("Loading Album " + id);
			org.bson.Document mbalbum = getAlbum(artist, title);
			if(mbalbum != null && mbalbum.getInteger("count") > 0)
			{
				String[] ids = getAlbumId(artist + " - " + title, mbalbum);
				if(ids != null)
				{
					artistId = ids[0];
					albumId = ids[1];
				}
			}
			page.append("artistid", artistId).append("albumid", albumId);
			LOGGER.info(artist + " - " + title + ": " + artistId + " - " + albumId);
		}
	}

	private org.bson.Document getAlbum(String artist, String title)
	{
		if(StringUtils.isBlank(artist) || StringUtils.isBlank(title))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20release:" + title.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
	}

	private void loadArtistMBId(org.bson.Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");

		if(artistId == null)
		{
			LOGGER.debug("Loading Artist: " + id);
			org.bson.Document mbartist = getArtist(artist);
			if(mbartist != null && mbartist.getInteger("count") > 0)
			{
				artistId = getArtistId(artist, mbartist);
			}
			page.append("artistid", artistId);
			LOGGER.debug(artist + ": " + artistId);
		}
	}

	private org.bson.Document getArtist(String artist)
	{
		if(StringUtils.isBlank(artist))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/artist/?query=artist:" + artist.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
	}

	protected org.bson.Document getTrack(String artist, String song)
	{
		if(StringUtils.isBlank(artist) || StringUtils.isBlank(song))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/recording/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20recording:" + song.trim().replace(" ", "%20") + "&fmt=json";
		org.bson.Document album = callMusicbrainz(url);

		TreeMap<Integer, org.bson.Document> scores = Maps.newTreeMap();
		if(album != null)
		{
			List<org.bson.Document> recordings = album.get("recordings", List.class);
			if(CollectionUtils.isNotEmpty(recordings))
			{
				for(org.bson.Document recording : recordings)
				{
					int score = getRecordingScore(recording);
					if(score == 100)
					{
						String mbartist = getReleaseArtist(recording);
						score = FuzzySearch.tokenSetRatio(artist, mbartist);
						String artistId =  getRecordingArtistId(recording);
						if(score > THRESHOLD && artistId != null)
						{
							LOGGER.info(artist + ", " + mbartist + " " + artistId + " (score: " + score + ")");
							Document page = new Document("artistid", artistId);
							scores.put(score, page);
						}
					}
				}
			}
		}
		return CollectionUtils.isEmpty(scores.keySet()) ? null : scores.get(scores.lastEntry().getKey());
	}

	private org.bson.Document callMusicbrainz(String url)
	{
		HttpURLConnection con;
		try 
		{
			con = (HttpURLConnection)new URL(url).openConnection();
		} 
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage(), t);
			return null;
		}

		try(BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));)
		{
			String json = "";
			String line = null;
			while ((line = rd.readLine()) != null) 
			{
				json += line;
			}
			org.bson.Document doc = org.bson.Document.parse(json);
			return (doc != null && doc.getInteger("count") > 0) ? doc : null;
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage(), t);
			return null;
		}
		finally
		{
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	private void loadTracksMBId(org.bson.Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String album = page.getString("title");
		String artistId = page.getString("artistid");

		if(artistId != null)
		{
			return;
		}

		try
		{
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					String title = track.getString("title");
					if(title != null)
					{
						artistId = track.getString("artistid");
						if(artistId == null)
						{
							LOGGER.debug("Loading Track ids: " + title);
							org.bson.Document mbtrack = getTrack(title);
							if(mbtrack != null)
							{
								track.append("artistid", mbtrack.getString("artistid")).
								append("albumid", mbtrack.getString("albumid"));
								LOGGER.info(title + " (" + artistId + ")");
							}
						}
					}
				}
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			LOGGER.error("Track Musicbrainz: " + id, t);
		}
	}

	private String[] getAlbumId(String title, org.bson.Document album)
	{
		TreeMap<Integer, String[]> scores = Maps.newTreeMap();
		List<org.bson.Document> releases = album.get("releases", List.class);
		if(CollectionUtils.isNotEmpty(releases))
		{
			for(org.bson.Document release : releases)
			{
				int score = getRecordingScore(release);
				String mbartist = getRecordingArtist(release);
				String mbtitle = getRecordingTitle(release);
				int scoreTitle = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbtitle);

				if(score > THRESHOLD && scoreTitle > THRESHOLD)
				{
					String artistId =  getRecordingArtistId(release);
					String recordingId = getReleaseId(release);
					scores.put(scoreTitle, new String[] {artistId, recordingId});
					break;
				}
			}
		}
		return CollectionUtils.isEmpty(scores.keySet()) ? null : scores.get(scores.lastEntry().getKey());
	}

	private String getArtistId(String name, org.bson.Document artist)
	{
		List<org.bson.Document> mbartists = artist.get("artists", List.class);
		if(CollectionUtils.isNotEmpty(mbartists))
		{
			for(org.bson.Document mbartist : mbartists)
			{
				Integer score = mbartist.getInteger("score");
				String mbartistname = mbartist.getString("name");
				int scoreArtist = FuzzySearch.tokenSetRatio(name, mbartistname);
				if(score > THRESHOLD && scoreArtist > THRESHOLD)
				{
					return mbartist.getString("id");
				}
			}
		}
		return null;
	}

	private String getRecordingArtistId(org.bson.Document recording)
	{
		return CollectionUtils.isEmpty(recording.get("artist-credit", List.class)) ? null : ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).getString("id");
	}

	private String getReleaseId(org.bson.Document release)
	{
		return release.get("release-group", org.bson.Document.class).getString("id");
	}

	private Integer getRecordingScore(org.bson.Document recording) 
	{
		return recording == null ? 0 : recording.getInteger("score");
	}

	private String getRecordingArtist(org.bson.Document recording) 
	{
		String artist = "";
		if(recording != null)
		{
			if(CollectionUtils.isNotEmpty(recording.get("artist-credit", List.class)))
			{
				for(org.bson.Document credit : (List<org.bson.Document>)recording.get("artist-credit", List.class))
				{
					artist += credit.getString("name");
					String joinphrase = credit.getString("joinphrase");
					if(joinphrase != null)
					{
						artist += joinphrase;
					}
				}
			}
		}
		return artist;
	}

	private String getRecordingTitle(org.bson.Document recording) 
	{
		String title = "";
		if(recording != null)
		{
			title = recording.getString("title");
		}
		return title;
	}

	private String getReleaseArtist(org.bson.Document recording)
	{
		String artist = "";
		if(recording != null)
		{
			if(CollectionUtils.isEmpty(recording.get("releases", List.class)))
			{
				artist = getRecordingArtist(recording);
			}
			else
			{
				org.bson.Document release = ((List<org.bson.Document>)recording.get("releases", List.class)).get(0);
				if(CollectionUtils.isEmpty(release.get("artist-credit", List.class)))
				{
					artist = getRecordingArtist(recording);
				}
				else
				{
					artist = ((org.bson.Document)release.get("artist-credit", List.class).get(0)).getString("name");
				}
			}
		}
		return artist;
	}
}
