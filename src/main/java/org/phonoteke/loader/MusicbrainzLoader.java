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
		//		new MusicbrainzLoader().loadMBIDs("https://www.raiplayradio.it/audio/2019/10/MUSICAL-BOX-22af6316-e860-47ee-bd39-f41cc5177fce.html");
		new MusicbrainzLoader().loadMBIDs();
	}

	public MusicbrainzLoader()
	{
		super();
		beforeStart();
	}

	private void beforeStart()
	{
		// does nothing
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
				loadArtistMBId(page);
			default:
				break;
			};
		}
	}

	private void loadAlbum(String id, String artist, String title)
	{
		MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.eq("type", TYPE.ALBUM.name().toLowerCase()))).iterator();
		if(!i.hasNext())
		{
			org.bson.Document json = getAlbum(artist, title);
			if(json != null && json.getInteger("count") > 0)
			{
				json.append("id", id).
				append("type", TYPE.ALBUM.name().toLowerCase());
				musicbrainz.insertOne(json);
				LOGGER.info("MB " + id + ": Album added");
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
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");
		String title = page.getString("title");
		String albumId = page.getString("albumid");

		if(artistId == null || albumId == null)
		{
			LOGGER.info("MB Loading Album " + id);
			loadAlbum(id, artist, title);
			MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.eq("type", TYPE.ALBUM.name().toLowerCase()))).noCursorTimeout(true).iterator();
			if(i.hasNext())
			{
				List<org.bson.Document> releases = i.next().get("releases", List.class);
				if(CollectionUtils.isNotEmpty(releases))
				{
					for(org.bson.Document release : releases)
					{
						String mbartist = ((org.bson.Document)release.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).getString("name");
						String mbalbum = release.getString("title");
						int score = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbalbum);
						if(score > 90)
						{
							artistId = ((List<org.bson.Document>)release.get("artist-credit")).get(0).get("artist", org.bson.Document.class).getString("id");
							page.put("artistid", artistId);

							albumId = release.get("release-group", org.bson.Document.class).getString("id");
							page.put("albumid", albumId);
							docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
							LOGGER.info("MB " + artist + " - " + title + ": " + artistId + " - " + albumId);
							break;
						}
					}
				}
			}
		}
	}

	private void loadArtist(String id, String artist)
	{
		MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.eq("type", TYPE.ARTIST.name().toLowerCase()))).iterator();
		if(!i.hasNext())
		{
			org.bson.Document json = getArtist(artist);
			if(json != null && json.getInteger("count") > 0)
			{
				json.append("id", id).
				append("type", TYPE.ARTIST.name().toLowerCase());
				musicbrainz.insertOne(json);
				LOGGER.info("MB " + id + ": Artist added");
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
		String id = page.getString("id");
		String artist = page.getString("artist");
		String artistId = page.getString("artistid");

		if(artistId == null)
		{
			LOGGER.info("MB Loading Artist: " + id);
			loadArtist(id, artist);
			MongoCursor<org.bson.Document> i = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.eq("type", TYPE.ARTIST.name().toLowerCase()))).noCursorTimeout(true).iterator();
			if(i.hasNext())
			{
				List<org.bson.Document> mbartists = i.next().get("artists", List.class);
				if(CollectionUtils.isNotEmpty(mbartists))
				{
					for(org.bson.Document mbartist : mbartists)
					{
						String mbartist2 = mbartist.getString("name");
						int score = FuzzySearch.tokenSetRatio(artist, mbartist2);
						if(score > 90)
						{
							artistId = mbartist.getString("id");
							page.put("artistid", artistId);
							docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
							LOGGER.info("MB " + artist + ": " + artistId);
							break;
						}
					}
				}
			}
		}
	}

	protected void loadTrack(String id, String title, String youtube, String artistId, String albumId)
	{
		if(title != null)
		{
			org.bson.Document mbtrack = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.eq("type", TYPE.TRACK.name().toLowerCase()), Filters.eq("title", title))).noCursorTimeout(true).iterator().tryNext();
			if(mbtrack == null)
			{
				if(artistId != null && albumId != null)
				{
					org.bson.Document page = new org.bson.Document("id", id).
							append("title", title).
							append("youtube", youtube).
							append("artistid", artistId).
							append("albumid", albumId).
							append("type", TYPE.TRACK.name().toLowerCase());
					musicbrainz.insertOne(page);
					LOGGER.info("MB " + id + ": Tracks added");
				}
				else
				{
					org.bson.Document json = getRecording(title);
					if(json != null && json.getInteger("count") > 0)
					{
						json.append("id", id).
						append("title", title).
						append("youtube", youtube).
						append("type", TYPE.TRACK.name().toLowerCase());
						musicbrainz.insertOne(json);
						LOGGER.info("MB " + id + ": Tracks added");
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
		String id = page.getString("id");
		String source = page.getString("source");
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
				String youtube = track.getString("youtube");
				String title = track.getString("title");
				if(title != null)
				{
					LOGGER.info("MB Loading Tracks: " + id);
					loadTrack(id, title, youtube, artistId, albumId);
					org.bson.Document mbtrack = musicbrainz.find(Filters.and(Filters.eq("id", id), Filters.eq("type", TYPE.TRACK.name().toLowerCase()), Filters.eq("title", title))).noCursorTimeout(true).iterator().tryNext();
					if(mbtrack != null)
					{
						String mbartistId = mbtrack.getString("artistid");
						String mbalbumId = mbtrack.getString("albumid");
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
									String mbartist = ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).getString("name");
									String mbalbum = recording.getString("title");
									int score = FuzzySearch.tokenSetRatio(title, mbartist + " - " + mbalbum);
									if(score > 90)
									{
										if(CollectionUtils.isNotEmpty(recording.get("artist-credit", List.class)))
										{
											mbartistId = ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).getString("id");
											track.append("artistid", mbartistId);
										}
										if(CollectionUtils.isNotEmpty(recording.get("releases", List.class)))
										{
											mbalbumId = ((org.bson.Document)recording.get("releases", List.class).get(0)).get("release-group", org.bson.Document.class).getString("id");
											track.append("albumid", mbalbumId);
										}
										LOGGER.info("MB " + title + ": " + mbartistId + " - " + mbalbumId);
										break;
									}
								}
							}
						}
					}
				}
			}

			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("MB " + id + ": Tracks updated");
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			LOGGER.error("Track Musicbrainz: " + id, t);
		}
	}
}
