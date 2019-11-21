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

import com.google.api.client.util.Maps;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MusicbrainzLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(MusicbrainzLoader.class);

	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";

	private static final int SLEEP_TIME = 2000;
	private static final int THRESHOLD = 90;


	public static void main(String[] args)
	{
		//		new MusicbrainzLoader().loadMBIDs("");
		new MusicbrainzLoader().loadMBIDs();
	}

	public MusicbrainzLoader()
	{
		super();
	}

	public void loadMBIDs(String url)
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("url", url)).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			switch (TYPE.valueOf(page.getString("type").toUpperCase())) {
			case ALBUM:
				loadAlbumMBId(page);
				loadTracksMBId(page);
				break;
			case ARTIST:
				loadArtistMBId(page);
				break;
			case CONCERT:
			case INTERVIEW:
				loadArtistMBId(page);
			default:
				break;
			};
		}
	}

	public void loadMBIDs()
	{
		MongoCursor<org.bson.Document> i = docs.find().noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			switch (TYPE.valueOf(page.getString("type").toUpperCase())) {
			case ALBUM:
				loadAlbumMBId(page);
				loadTracksMBId(page);
				break;
			case ARTIST:
				loadArtistMBId(page);
				break;
			case CONCERT:
			case INTERVIEW:
				loadArtistMBId(page);
			default:
				break;
			};
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

	private void loadAlbumMBId(org.bson.Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");
		String title = page.getString("title");
		String albumId = page.getString("albumid");

		if(artistId == null || albumId == null)
		{
			LOGGER.info("MB Loading Album " + id);
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
			if(artistId == null || albumId == null)
			{
				artistId = UNKNOWN;
				albumId = UNKNOWN;
			}
			page.append("artistid", artistId).append("albumid", albumId);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("MB " + artist + " - " + title + ": " + artistId + " - " + albumId);
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

	private void loadArtistMBId(org.bson.Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");

		if(artistId == null)
		{
			LOGGER.info("MB Loading Artist: " + id);
			org.bson.Document mbartist = getArtist(artist);
			if(mbartist != null && mbartist.getInteger("count") > 0)
			{
				artistId = getArtistId(artist, mbartist);
			}
			if(artistId == null)
			{
				artistId = UNKNOWN;
			}
			page.append("artistid", artistId);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("MB " + artist + ": " + artistId);
		}
	}

	private org.bson.Document getRecording(String artist, String recording)
	{
		if(StringUtils.isBlank(recording))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/recording/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20recording:" + recording.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
	}

	private org.bson.Document getRecording(String recording)
	{
		if(StringUtils.isBlank(recording))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/recording/?query=recording:" + recording.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
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
			return org.bson.Document.parse(json);
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
		String source = page.getString("source");
		String artist = page.getString("artist");
		String album = page.getString("title");
		String artistId = page.getString("artistid");
		String albumId = page.getString("albumid");

		if(source.equals(OndarockLoader.SOURCE))
		{
			return;
		}

		try
		{
			for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks", List.class))
			{
				String title = track.getString("title");
				if(title != null)
				{
					if(artistId != null && albumId != null && !UNKNOWN.equals(artistId) && !UNKNOWN.equals(albumId))
					{
						track.append("artistid", artistId).append("albumid", albumId).
						append("artist", artist).append("album", album);
						LOGGER.info("MB " + artist + " - " + album + " (" + artistId + ", " + albumId + ")");
					}
					else
					{
						String tartistId = track.getString("artistid");
						String tartist = track.getString("artist");
						String talbumId = track.getString("albumid");
						String talbum = track.getString("album");
						if(tartistId == null || talbumId == null || tartist == null || talbum == null)
						{
							LOGGER.info("MB Loading Track: " + title);
							// no Artist - Recording separator
							if(title.split("-").length != 2 && title.split("–").length != 2)
							{
								org.bson.Document mbtrack = getRecording(title);
								if(mbtrack != null && mbtrack.getInteger("count") > 0)
								{
									String[] ids = getTrackId(title, mbtrack);
									if(ids != null)
									{
										tartistId = ids[0];
										talbumId = ids[1];
										tartist = ids[2];
										talbum = ids[3];
									}
								}
							}
							else
							{
								// Recording - Artist
								String recording = title.split("-").length == 2 ? title.split("-")[0].trim() : title.split("–")[0].trim();
								artist = title.split("-").length == 2 ? title.split("-")[1].trim() : title.split("–")[1].trim();
								org.bson.Document mbtrack = getRecording(artist, recording);
								if(mbtrack != null && mbtrack.getInteger("count") > 0)
								{
									String[] ids = getTrackId(title, mbtrack);
									if(ids != null)
									{
										tartistId = ids[0];
										talbumId = ids[1];
										tartist = ids[2];
										talbum = ids[3];
									}
								}
								// Artist - Recording
								if(tartistId == null || talbumId == null)
								{
									artist = title.split("-").length == 2 ? title.split("-")[0].trim() : title.split("–")[0].trim();
									recording = title.split("-").length == 2 ? title.split("-")[1].trim() : title.split("–")[1].trim();
									mbtrack = getRecording(artist, recording);
									if(mbtrack != null && mbtrack.getInteger("count") > 0)
									{
										String[] ids = getTrackId(title, mbtrack);
										if(ids != null)
										{
											tartistId = ids[0];
											talbumId = ids[1];
											tartist = ids[2];
											talbum = ids[3];
										}
									}
								}
							}
							if(tartistId == null || talbumId == null || UNKNOWN.equals(tartistId) || UNKNOWN.equals(talbumId))
							{
								tartistId = UNKNOWN;
								talbumId = UNKNOWN;
								tartist = UNKNOWN;
								talbum = UNKNOWN;
							}
							track.append("artistid", tartistId).append("albumid", talbumId).
							append("artist", tartist).append("album", talbum);
							LOGGER.info("MB " + tartist + " - " + talbum + " (" + tartistId + ", " + talbumId + ")");
						}
					}
				}
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
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

	private String[] getTrackId(String title, org.bson.Document album)
	{
		TreeMap<Integer, String[]> scores = Maps.newTreeMap();
		List<org.bson.Document> recordings = album.get("recordings", List.class);
		if(CollectionUtils.isNotEmpty(recordings))
		{
			for(org.bson.Document recording : recordings)
			{
				int score = getRecordingScore(recording);
				String mbartist = getRecordingArtist(recording);
				String mbreleaseartist = getReleaseArtist(recording);
				int scoreArtist = FuzzySearch.tokenSetRatio(mbartist, mbreleaseartist);
				String mbtitle = getRecordingTitle(recording);
				int scoreTitle = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbtitle);

				String artistId =  getRecordingArtistId(recording);
				String recordingId = getRecordingId(recording);
				if(score > THRESHOLD && artistId != null && recordingId != null)
				{
					if(scoreArtist > THRESHOLD && scoreTitle > THRESHOLD)
					{
						scores.put(scoreArtist + scoreTitle, new String[] {artistId, recordingId, mbartist, mbtitle});
						break;
					}
					else if(scoreTitle == 100 && !scores.containsKey(100))
					{
						scores.put(100, new String[] {artistId, recordingId, mbartist, mbtitle});
					}
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

	private String getRecordingId(org.bson.Document recording)
	{
		return CollectionUtils.isEmpty(recording.get("releases", List.class)) ? null : ((org.bson.Document)recording.get("releases", List.class).get(0)).get("release-group", org.bson.Document.class).getString("id");
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
