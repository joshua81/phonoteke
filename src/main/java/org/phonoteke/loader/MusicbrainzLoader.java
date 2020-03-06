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
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MusicbrainzLoader
{
	private static final Logger LOGGER = LogManager.getLogger(MusicbrainzLoader.class);

	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";

	private static final List<String> SEPARATOR = Lists.newArrayList("-", "–");
	private static final int SLEEP_TIME = 2000;
	private static final int THRESHOLD = 90;


	public void loadAlbumMBId(org.bson.Document page)
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

	public void loadArtistMBId(org.bson.Document page)
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

	public void loadTracksMBId(org.bson.Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String album = page.getString("title");
		String artistId = page.getString("artistid");
		String albumId = page.getString("albumid");

		try
		{
			for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks", List.class))
			{
				String title = track.getString("title");
				if(title != null)
				{
					if(artistId != null && albumId != null)
					{
						track.append("artistid", artistId).append("albumid", albumId);
						LOGGER.info(artist + " - " + album + " (" + artistId + ", " + albumId + ")");
					}
					else
					{
						String tartistId = track.getString("artistid");
						String talbumId = track.getString("albumid");
						if(tartistId == null || talbumId == null)
						{
							LOGGER.debug("Loading Track: " + title);
							// no Artist - Recording separator
							if(!isChunkTitle(title))
							{
								org.bson.Document mbtrack = getRecording(title);
								if(mbtrack != null && mbtrack.getInteger("count") > 0)
								{
									String[] ids = getTrackId(title, mbtrack);
									if(ids != null)
									{
										tartistId = ids[0];
										talbumId = ids[1];
									}
								}
							}
							else
							{
								// Recording - Artist
								String recording = getTitleChunk(title, 0);
								artist = getTitleChunk(title, 1);
								org.bson.Document mbtrack = getRecording(artist, recording);
								if(mbtrack != null && mbtrack.getInteger("count") > 0)
								{
									String[] ids = getTrackId(title, mbtrack);
									if(ids != null)
									{
										tartistId = ids[0];
										talbumId = ids[1];
									}
								}
								// Artist - Recording
								if(tartistId == null || talbumId == null)
								{
									artist = getTitleChunk(title, 0);
									recording = getTitleChunk(title, 1);
									mbtrack = getRecording(artist, recording);
									if(mbtrack != null && mbtrack.getInteger("count") > 0)
									{
										String[] ids = getTrackId(title, mbtrack);
										if(ids != null)
										{
											tartistId = ids[0];
											talbumId = ids[1];
										}
									}
								}
							}
							track.append("artistid", tartistId).append("albumid", talbumId);
							LOGGER.info(title + " (" + tartistId + ", " + talbumId + ")");
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

	private boolean isChunkTitle(String title)
	{
		Preconditions.checkNotNull(title);

		for(String s : SEPARATOR)
		{
			if(title.split(s).length == 2)
			{
				return true;
			}
		}
		return false;
	}

	private String getTitleChunk(String title, int i)
	{
		Preconditions.checkNotNull(title);
		Preconditions.checkArgument(i == 0 || i == 1);

		for(String s : SEPARATOR)
		{
			if(title.split(s).length == 2)
			{
				return title.split(s)[i];
			}
		}
		return null;
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
