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
		new MusicbrainzLoader().loadMBIDs("2921cb995216d0350986eb7dac9f1f685e7aae6e7c1f7c0cf36447ef08ae39bd");
	}

	public MusicbrainzLoader()
	{
		super();
	}

	public void loadMBIDs(String id)
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("id", id)).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			switch (TYPE.valueOf(page.get("type", String.class).toUpperCase())) {
			case ALBUM:
				loadAlbumMBId(page);
				loadTracksMBId(page);
				break;
			case ARTIST:
				loadArtistMBId(page);
				break;
			case CONCERT:
				loadArtistMBId(page);
				loadTracksMBId(page);
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
			switch (TYPE.valueOf(page.get("type", String.class).toUpperCase())) {
			case ALBUM:
				loadAlbumMBId(page);
				loadTracksMBId(page);
				break;
			case ARTIST:
				loadArtistMBId(page);
				break;
			case CONCERT:
				loadArtistMBId(page);
				loadTracksMBId(page);
			default:
				break;
			};
		}
	}

	private void loadAlbum(String id, String artist, String title)
	{
		if(!musicbrainz.find(Filters.eq("id", id)).iterator().hasNext())
		{
			org.bson.Document json = getAlbum(artist, title);
			if(json != null)
			{
				json.append("id", id);
				musicbrainz.insertOne(json);
				LOGGER.info(id + ": Album added");
			}
		}
	}

	private org.bson.Document getAlbum(String artist, String title)
	{
		if(StringUtils.isBlank(artist) || StringUtils.isBlank(title))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/release/?query=artist:" + artist.trim().replace(" ", "%20") + "%20AND%20" + "release:" + title.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
	}

	private void loadAlbumMBId(org.bson.Document page)
	{
		String id = page.get("id", String.class);
		String artist = page.get("artist", String.class);
		String artistId = page.get("artistid", String.class);
		String title = page.get("title", String.class);
		String albumId = page.get("albumid", String.class);

		LOGGER.info("Loading Album MBID: " + id);
		if(artistId == null || albumId == null)
		{
			loadAlbum(id, artist, title);
			MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id))).noCursorTimeout(true).iterator();
			if(i.hasNext())
			{
				List<org.bson.Document> mbalbums = i.next().get("releases", List.class);
				for(org.bson.Document mbalbum : mbalbums)
				{
					int score = mbalbum.get("score", Integer.class);
					if(score == 100)
					{
						artistId = ((List<org.bson.Document>)mbalbum.get("artist-credit")).get(0).get("artist", org.bson.Document.class).get("id", String.class);
						page.put("artistid", artistId);

						albumId = mbalbum.get("release-group", org.bson.Document.class).get("id", String.class);
						page.put("albumid", albumId);
						docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
						LOGGER.info(artist + " - " + title + ": " + artistId + ", " + albumId);
						break;
					}
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
				LOGGER.info(id + ": Artist added");
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
		String artist = page.get("artist", String.class);
		String artistId = page.get("artistid", String.class);

		LOGGER.info("Loading Artist MBID: " + id);
		if(artistId == null)
		{
			loadArtist(id, artist);
			MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id))).noCursorTimeout(true).iterator();
			if(i.hasNext())
			{
				List<org.bson.Document> mbartists = i.next().get("artists", List.class);
				for(org.bson.Document mbartist : mbartists)
				{
					int score = mbartist.get("score", Integer.class);
					if(score == 100)
					{
						artistId = mbartist.get("id", String.class);
						page.put("artistid", artistId);
						docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
						LOGGER.info(artist + ": " + artistId);
						break;
					}
				}
			}
		}
	}

	protected void loadTrack(String id, String title, String youtube, String artistId, String albumId)
	{
		if(title != null)
		{
			org.bson.Document mbtrack = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.exists("title"), Filters.eq("title", title))).noCursorTimeout(true).iterator().tryNext();
			if(mbtrack == null)
			{
				if(artistId != null && albumId != null)
				{
					org.bson.Document page = new org.bson.Document("id", id).
							append("title", title).
							append("youtube", youtube).
							append("artistid", artistId).
							append("albumid", albumId);
					musicbrainz.insertOne(page);
					LOGGER.info(id + ": Tracks added");
				}
				else
				{
					org.bson.Document json = getRecording(title);
					if(json != null)
					{
						json.append("id", id).
						append("title", title).
						append("youtube", youtube);
						musicbrainz.insertOne(json);
						LOGGER.info(id + ": Tracks added");
					}
				}
			}
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
		String id = page.get("id", String.class);
		String artist = page.get("artist", String.class);
		String source = page.get("source", String.class);
		String artistId = page.get("artistid", String.class);
		String albumId = page.get("albumid", String.class);
		
		try
		{
			LOGGER.info("Loading Tracks MBID: " + id);
			for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks", List.class))
			{
				String title = track.get("title", String.class);
				String youtube = track.get("youtube", String.class);

				if(title != null)
				{
					if(source.equals(OndarockLoader.SOURCE))
					{
						title = artist + " - " + title;
					}
					loadTrack(id, title, youtube, artistId, albumId);
					org.bson.Document mbtrack = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.exists("title"), Filters.eq("title", title))).noCursorTimeout(true).iterator().tryNext();
					if(mbtrack != null)
					{
						String mbartistId = mbtrack.get("artistid", String.class);
						String mbalbumId = mbtrack.get("albumid", String.class);
						if(mbartistId != null && mbalbumId != null)
						{
							track.append("artistid", mbartistId).
							append("albumid", mbalbumId);
						}
						else
						{
							List<org.bson.Document> recordings = mbtrack.get("recordings", List.class);
							if(CollectionUtils.isNotEmpty(recordings))
							{
								for(org.bson.Document recording : recordings)
								{
									//						int score = recording.get("score", Integer.class);
									String mbartist = ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).get("name", String.class);
									int score = FuzzySearch.tokenSetRatio(title, mbartist);
									if(score > 70)
										//						if(StringUtils.stripAccents(title.toLowerCase()).contains(StringUtils.stripAccents(artist.toLowerCase())))
									{
										if(CollectionUtils.isNotEmpty(recording.get("artist-credit", List.class)))
										{
											mbartistId = ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).get("id", String.class);
											track.append("artistid", mbartistId);
										}
										if(CollectionUtils.isNotEmpty(recording.get("releases", List.class)))
										{
											mbalbumId = ((org.bson.Document)recording.get("releases", List.class).get(0)).get("release-group", org.bson.Document.class).get("id", String.class);
											track.append("albumid", mbalbumId);
										}
										break;
									}
								}
							}
						}
					}
				}
			}
			
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info(id + ": Tracks updated");
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			LOGGER.error("Track Musicbrainz: " + id, t);
		}
	}
}
