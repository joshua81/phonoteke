package org.phonoteke.loader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.util.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MusicbrainzLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(MusicbrainzLoader.class);

	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";
	private static final int SLEEP_TIME = 2000;


	public static void main(String[] args)
	{
		new MusicbrainzLoader().loadMBIDs();
	}

	public MusicbrainzLoader()
	{
		super();
	}

	public void loadMBIDs()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("artistid", null), Filters.eq("albumid", null), Filters.eq("type", "album"))).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String artist = page.get("artist", String.class);
			String album = page.get("title", String.class);

			loadAlbum(id, artist, album);
			loadAlbumMBId(page);
			loadTracks(id);
			loadTracksMBId(page);
		}

		i = docs.find(Filters.and(Filters.eq("artistid", null), Filters.eq("type", "artist"))).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String artist = page.get("artist", String.class);

			loadArtist(id, artist);
			loadArtistMBId(page);
		}
	}

	private void loadAlbum(String id, String artist, String album)
	{
		if(!musicbrainz.find(Filters.eq("id", id)).iterator().hasNext())
		{
			org.bson.Document json = getAlbum(artist, album);
			if(json != null)
			{
				json.append("id", id);
				musicbrainz.insertOne(json);
				LOGGER.info("Musicbrainz " + id + " Album added");
			}
		}
	}

	private org.bson.Document getAlbum(String artist, String album)
	{
		if(StringUtils.isBlank(artist) || StringUtils.isBlank(album))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20" + "release:" + album.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
	}

	private void loadAlbumMBId(org.bson.Document page)
	{
		String id = page.get("id", String.class);
		MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id))).noCursorTimeout(true).iterator();
		if(i.hasNext())
		{
			List<org.bson.Document> albums = i.next().get("releases", List.class);
			for(org.bson.Document album : albums)
			{
				int score = album.get("score", Integer.class);
				if(score == 100)
				{
					String artistId = ((List<org.bson.Document>)album.get("artist-credit")).get(0).get("artist", org.bson.Document.class).get("id", String.class);
					page.put("artistid", artistId);

					String albumId = album.get("release-group", org.bson.Document.class).get("id", String.class);
					page.put("albumid", albumId);
					docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
					LOGGER.info("Album Musicbrainz " + id + ": " + artistId + ", " + albumId);
				}
			}
		}
	}

	private void loadArtist(String id, String artist)
	{
		if(!musicbrainz.find(Filters.eq("id", id)).iterator().hasNext())
		{
			org.bson.Document json = getArtist(artist);
			if(json != null)
			{
				json.append("id", id);
				musicbrainz.insertOne(json);
				LOGGER.info("Musicbrainz " + id + " Artist added");
			}
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
		String id = page.get("id", String.class);
		MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id))).noCursorTimeout(true).iterator();
		if(i.hasNext())
		{
			List<org.bson.Document> artists = i.next().get("artists", List.class);
			for(org.bson.Document artist : artists)
			{
				int score = artist.get("score", Integer.class);
				if(score == 100)
				{
					String artistId = artist.get("id", String.class);
					page.put("artistid", artistId);
					docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
					LOGGER.info("Artist Musicbrainz " + id + ": " + artistId);
				}
			}
		}
	}

	protected void loadTracks(String id)
	{
		MongoCursor<org.bson.Document> i = tracks.find(Filters.eq("id", id)).noCursorTimeout(true).iterator();
		if(i.hasNext())
		{
			org.bson.Document page = i.next();
			List<org.bson.Document> recordings = page.get("tracks", List.class);
			for(org.bson.Document recording : recordings)
			{
				String title = recording.get("title", String.class);
				org.bson.Document musicbrainz = recording.get("musicbrainz", org.bson.Document.class);
				if(musicbrainz == null)
				{
					org.bson.Document json = getRecording(title);
					if(json != null)
					{
						recording.append("musicbrainz", json);
					}
				}
			}
			tracks.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Musicbrainz " + id + " Tracks updated");
		}
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
			return org.bson.Document.parse(json);
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
		String id = page.get("id", String.class);
		String artistId = page.get("artistid", String.class);
		String albumId = page.get("albumid", String.class);
		List<org.bson.Document> tracks = loadTracksMBId(id, artistId, albumId);
		if(CollectionUtils.isNotEmpty(tracks))
		{
			for(org.bson.Document track : tracks)
			{
				String docId = track.get("docid", String.class);
				String title = track.get("title", String.class);
				String youtube = track.get("youtube", String.class);
				MongoCursor<org.bson.Document> j = docs.find(Filters.and(Filters.eq("type", "track"), Filters.eq("docid", docId))).noCursorTimeout(true).iterator();
				if(!j.hasNext())
				{
					docs.insertOne(track);
					LOGGER.info("Track " + docId + ": " + title + " added");
				}
				else
				{
					while(j.hasNext())
					{
						page = j.next();
						if(title != null && title.equals(page.get("title", String.class)))
						{
							page.put("youtube", track.get("youtube"));
							page.put("artistid", track.get("artistid"));
							page.put("albumid", track.get("albumid"));
							docs.updateOne(Filters.and(Filters.eq("docid", docId), Filters.eq("title", title)), new org.bson.Document("$set", page));
							LOGGER.info("Track " + docId + ": " + title + " updated");
						}
						else if(youtube != null && youtube.equals(page.get("youtube", String.class)))
						{
							page.put("title", track.get("title"));
							page.put("artistid", track.get("artistid"));
							page.put("albumid", track.get("albumid"));
							docs.updateOne(Filters.and(Filters.eq("docid", docId), Filters.eq("youtube", youtube)), new org.bson.Document("$set", page));
							LOGGER.info("Track " + docId + ": " + youtube + " updated");
						}
					}
				}
			}	
		}
	}

	private List<org.bson.Document> loadTracksMBId(String id, String artistId, String albumId)
	{
		List<org.bson.Document> mbIds = Lists.newArrayList();
		try
		{
			MongoCursor<org.bson.Document> i = tracks.find(Filters.and(Filters.eq("id", id))).noCursorTimeout(true).iterator();
			if(i.hasNext())
			{
				List<org.bson.Document> pages = i.next().get("tracks", List.class);
				for(org.bson.Document page : pages)
				{
					String title = page.get("title", String.class);
					String youtube = page.get("youtube", String.class);
					org.bson.Document mbId = new org.bson.Document("title", title).
							append("type", TYPE.TRACK.name().toLowerCase()).
							append("youtube", youtube).
							append("docid", id);
					mbIds.add(mbId);

					if(artistId != null && albumId != null)
					{
						mbId.append("artistid", artistId).
						append("albumid", albumId);
						break;
					}

					org.bson.Document mb = page.get("musicbrainz", org.bson.Document.class);
					if(mb == null)
					{
						break;
					}

					List<org.bson.Document> recordings = mb.get("recordings", List.class);
					if(CollectionUtils.isEmpty(recordings))
					{
						break;
					}

					for(org.bson.Document recording : recordings)
					{
						//						int score = recording.get("score", Integer.class);
						String artist = ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).get("name", String.class);
						int score = FuzzySearch.tokenSetRatio(title, artist);
						if(score > 70)
							//						if(StringUtils.stripAccents(title.toLowerCase()).contains(StringUtils.stripAccents(artist.toLowerCase())))
						{
							if(CollectionUtils.isNotEmpty(recording.get("artist-credit", List.class)))
							{
								artistId = ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).get("id", String.class);
							}

							if(CollectionUtils.isNotEmpty(recording.get("releases", List.class)))
							{
								albumId = ((org.bson.Document)recording.get("releases", List.class).get(0)).get("release-group", org.bson.Document.class).get("id", String.class);								
							}

							if(artistId != null && albumId != null)
							{
								mbId.append("artistid", artistId).
								append("albumid", albumId);
								break;
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
		return mbIds;
	}
}
