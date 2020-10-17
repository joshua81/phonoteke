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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MusicbrainzLoader implements HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(MusicbrainzLoader.class);

	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";
	
	private MongoCollection<org.bson.Document> docs = new MongoDB().getDocs();
	

	@Override
	public void load(String task) 
	{
		LOGGER.info("Loading Musicbrainz...");
		MongoCursor<Document> i = docs.find(Filters.or(
				Filters.and(Filters.ne("type", "podcast"), Filters.eq("artistid", null)),
				Filters.and(Filters.eq("type", "podcast"), Filters.eq("tracks.artistid", null)))).iterator();
		while(i.hasNext())
		{
			Document page = i.next();
			String id = page.getString("id"); 
			String type = page.getString("type");
			if("album".equals(type))
			{
				loadAlbumMBId(page);
			}
			else if("podcast".equals(type))
			{
				loadTracksMBId(page);
			}
			else
			{
				loadArtistMBId(page);
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
		}
	}

	private void loadAlbumMBId(org.bson.Document page)
	{
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");
		String title = page.getString("title");
		String source = page.getString("source");

		if(OndarockLoader.SOURCE.equals(source) && artistId == null)
		{
			LOGGER.debug("Loading Album " + id);
			org.bson.Document mbalbum = getAlbum(artist, title);
			if(mbalbum != null && mbalbum.getInteger("count") > 0)
			{
				artistId = getAlbumId(artist + " - " + title, mbalbum);
			}
			page.append("artistid", artistId == null ? NA : artistId);
			LOGGER.info(artist + " - " + title + ": " + artistId);
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
			page.append("artistid", artistId == null ? NA : artistId);
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

	private org.bson.Document callMusicbrainz(String url)
	{
		HttpURLConnection con;
		try 
		{
			con = (HttpURLConnection)new URL(url).openConnection();
		} 
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
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
			LOGGER.error("ERROR: " + t.getMessage());
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

		try
		{
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					String artistId = track.getString("artistid");
					String album = track.getString("album");
					String artist = track.getString("artist");
					if(artistId == null && album != null && artist != null)
					{
						LOGGER.debug("Loading Album " + artist + " - " + album);
						org.bson.Document mbalbum = getAlbum(artist, album);
						if(mbalbum != null && mbalbum.getInteger("count") > 0)
						{
							artistId = getAlbumId(artist + " - " + album, mbalbum);
						}
						track.append("artistid", artistId == null ? NA : artistId);
						LOGGER.info(artist + " - " + album + ": " + artistId);
					}
				}
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR track Musicbrainz " + id + ": " + t.getMessage());
		}
	}

	private String getAlbumId(String title, org.bson.Document album)
	{
		TreeMap<Integer, String> scores = Maps.newTreeMap();
		List<org.bson.Document> releases = album.get("releases", List.class);
		if(CollectionUtils.isNotEmpty(releases))
		{
			for(org.bson.Document release : releases)
			{
				int score = getRecordingScore(release);
				String mbartist = getRecordingArtist(release);
				String mbtitle = getRecordingTitle(release);
				int scoreTitle = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbtitle);

				if(score >= THRESHOLD && scoreTitle >= THRESHOLD)
				{
					String artistId =  getRecordingArtistId(release);
					scores.put(scoreTitle, artistId);
				}
			}
		}
		return CollectionUtils.isEmpty(scores.keySet()) ? NA : scores.get(scores.lastEntry().getKey());
	}

	private String getArtistId(String name, org.bson.Document artist)
	{
		TreeMap<Integer, String> scores = Maps.newTreeMap();
		List<org.bson.Document> mbartists = artist.get("artists", List.class);
		if(CollectionUtils.isNotEmpty(mbartists))
		{
			for(org.bson.Document mbartist : mbartists)
			{
				Integer score = mbartist.getInteger("score");
				String mbartistname = mbartist.getString("name");
				int scoreArtist = FuzzySearch.tokenSetRatio(name, mbartistname);
				if(score >= THRESHOLD && scoreArtist >= THRESHOLD)
				{
					String artistId = mbartist.getString("id");
					scores.put(score, artistId);
				}
			}
		}
		return CollectionUtils.isEmpty(scores.keySet()) ? NA : scores.get(scores.lastEntry().getKey());
	}

	private String getRecordingArtistId(org.bson.Document recording)
	{
		return CollectionUtils.isEmpty(recording.get("artist-credit", List.class)) ? null : ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).getString("id");
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
}
