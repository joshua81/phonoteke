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

public class MusicbrainzLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(MusicbrainzLoader.class);

	private static final String MUSICBRAINZ = "http://musicbrainz.org/ws/2";
	private static final int SLEEP_TIME = 2000;


	public static void main(String[] args) 
	{
		new MusicbrainzLoader().loadArtists();
		new MusicbrainzLoader().loadAlbums();
		new MusicbrainzLoader().loadTracks();
	}

	public MusicbrainzLoader()
	{
		super();
	}

	protected void loadAlbums()
	{
		LOGGER.info("Loading Musicbrainz Albums");
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "album"), Filters.ne("source", "musicalbox"), Filters.eq("albumid", null))).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String artist = page.get("artist", String.class);
			String album = page.get("title", String.class);
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
	}

	protected void loadArtists()
	{
		LOGGER.info("Loading Musicbrainz Artists");
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "artist"), Filters.eq("artistid", null))).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String artist = page.get("artist", String.class);
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
	}

	protected void loadTracks()
	{
		LOGGER.info("Loading Musicbrainz Tracks");
		MongoCursor<org.bson.Document> i = tracks.find(Filters.eq("musicbrainz", null)).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			List<org.bson.Document> recordings = page.get("tracks", List.class);
			for(org.bson.Document recording : recordings)
			{
				String title = recording.get("title", String.class);
				org.bson.Document json = getRecording(title);
				if(json != null)
				{
					recording.append("musicbrainz", json);
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

	private org.bson.Document getArtist(String artist)
	{
		if(StringUtils.isBlank(artist))
		{
			return null;
		}
		String url = MUSICBRAINZ + "/artist/?query=artist:" + artist.trim().replace(" ", "%20") + "&fmt=json";
		return callMusicbrainz(url);
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

	public void loadArtistsIds()
	{
		LOGGER.info("Loading Musicbrainz Artists ids");
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "artist"), Filters.eq("artistid", null))).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String artistId = page.get("artistid", String.class);
			try
			{
				MongoCursor<org.bson.Document> j = musicbrainz.find(Filters.and(Filters.eq("id", id))).iterator();
				if(j.hasNext())
				{
					List<org.bson.Document> artists = j.next().get("artists", List.class);
					for(org.bson.Document artist : artists)
					{
						int score = (Integer)artist.get("score");
						if(score == 100)
						{
							artistId = artist.get("id", String.class);
							page.put("artistid", artistId);
							docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
							LOGGER.info("Artist Musicbrainz " + id + ": " + artistId);
							break;
						}
					}
				}
			}
			catch(Throwable t)
			{
				LOGGER.error("Artist Musicbrainz: " + id, t.getMessage());
			}
		}
	}

	public void loadAlbumsIds()
	{
		LOGGER.info("Loading Musicbrainz Albums ids");
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "album"), Filters.eq("albumid", null))).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			String albumId = page.get("albumid", String.class);
			try
			{
				MongoCursor<org.bson.Document> j = musicbrainz.find(Filters.and(Filters.eq("id", id))).iterator();
				if(j.hasNext())
				{
					List<org.bson.Document> albums = j.next().get("releases", List.class);
					for(org.bson.Document album : albums)
					{
						int score = (Integer)album.get("score");
						if(score == 100)
						{
							String artistId = ((List<org.bson.Document>)album.get("artist-credit")).get(0).get("artist", org.bson.Document.class).get("id", String.class);
							albumId = album.get("release-group", org.bson.Document.class).get("id", String.class);
							page.put("artistid", artistId);
							page.put("albumid", albumId);
							docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
							LOGGER.info("Album Musicbrainz " + id + ": " + artistId + ", " + albumId);
							break;
						}
					}
				}
			}
			catch(Throwable t)
			{
				LOGGER.error("Album Musicbrainz: " + id, t.getMessage());
			}
		}
	}

	public void loadTracksIds()
	{
		LOGGER.info("Loading Musicbrainz Tracks ids");
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "album"))).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = page.get("id", String.class);
			try
			{
				MongoCursor<org.bson.Document> j = tracks.find(Filters.and(Filters.eq("id", id), Filters.ne("musicbrainz", null))).iterator();
				if(j.hasNext())
				{
					List<org.bson.Document> documents = Lists.newArrayList();
					for(org.bson.Document track : (List<org.bson.Document>)j.next().get("tracks", List.class))
					{
						String title = track.get("title", String.class);
						String youtube = track.get("youtube", String.class);
						org.bson.Document document = new org.bson.Document("title", title).append("youtube", youtube);
						documents.add(document);
						org.bson.Document json = track.get("musicbrainz", org.bson.Document.class);
						List<org.bson.Document> recordings = (List<org.bson.Document>)json.get("recordings", List.class);
						if(CollectionUtils.isEmpty(recordings))
						{
							break;
						}

						for(org.bson.Document recording : recordings)
						{
							String artist =  ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).get("name", String.class);
							if(StringUtils.stripAccents(title.toLowerCase()).contains(StringUtils.stripAccents(artist.toLowerCase())))
							{
								String artistId = null;
								if(CollectionUtils.isNotEmpty(recording.get("artist-credit", List.class)))
								{
									artistId = ((org.bson.Document)recording.get("artist-credit", List.class).get(0)).get("artist", org.bson.Document.class).get("id", String.class);
									document.put("artistid", artistId);
								}

								String albumId = null;
								if(CollectionUtils.isNotEmpty(recording.get("releases", List.class)))
								{
									albumId = ((org.bson.Document)recording.get("releases", List.class).get(0)).get("release-group", org.bson.Document.class).get("id", String.class);								
									document.put("albumid", albumId);
								}
								LOGGER.info("Track " + title + ": " + artistId + ", " + albumId);
								break;
							}
						}
					}
					page.put("tracks", documents);
					docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
				}
			}
			catch(Throwable t)
			{
				LOGGER.error("Track Musicbrainz: " + id, t);
			}
		}
	}
}
