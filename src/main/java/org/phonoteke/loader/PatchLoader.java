package org.phonoteke.loader;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

public class PatchLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	private MusicbrainzLoader mbrainz = new MusicbrainzLoader();
	private SpotifyLoader spotify = new SpotifyLoader();
	private YoutubeLoader youtube = new YoutubeLoader();

	public static void main(String[] args)
	{
		new PatchLoader().patch();
		new PatchLoader().youtube();
		new PatchLoader().musicbrainz();
		new PatchLoader().spotify();
	}

	public PatchLoader()
	{
		super();
	}

	private void patch()
	{
		LOGGER.info("Loading patch...");
//		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.album.name()))).noCursorTimeout(true).iterator(); 
//		while(i.hasNext()) 
//		{ 
//			Document page = i.next();
//			String id = page.getString("id");
//			List<org.bson.Document> tracks = (List<org.bson.Document>)page.get("tracks", List.class);
//			if(CollectionUtils.isNotEmpty(tracks))
//			{
//				for(org.bson.Document track : tracks)
//				{
//					track.remove("artistid");
//					track.remove("albumid");
//					track.remove("sptrackid");
//					track.remove("spcover-l");
//					track.remove("spcover-m");
//					track.remove("spcover-s");
//				}
//				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
//				LOGGER.info("Updated Album tracks " + id);
//			}
//		}
	}

	private void musicbrainz()
	{
		LOGGER.info("Loading Musicbrainz...");
		for(int p = 0; p < 20; p++)
		{
			MongoCursor<Document> i = docs.find().sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).skip(p*2000).limit(2000).noCursorTimeout(true).iterator();
			while(i.hasNext())
			{
				Document page = i.next();
				String id = page.getString("id"); 
				switch (TYPE.valueOf(page.getString("type"))) {
				case album:
					mbrainz.loadAlbumMBId(page);
					break;
				case artist:
				case concert:
				case interview:
					mbrainz.loadArtistMBId(page);
				case podcast:
					mbrainz.loadTracksMBId(page);
					break;
				default:
					break;
				};
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
	}

	private void spotify()
	{
		LOGGER.info("Loading Spotify...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.album.name()), Filters.eq("spalbumid", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(2000).noCursorTimeout(true).iterator(); 
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id"); 
			spotify.loadAlbum(page);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
		}

		i = docs.find(Filters.and(Filters.ne("type", TYPE.album.name()), Filters.eq("spartistid", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(2000).noCursorTimeout(true).iterator(); 
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id"); 
			spotify.loadArtist(page);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page)); 
		}

		//		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("source", source), Filters.eq("tracks.sptrackid", null))).noCursorTimeout(true).iterator(); 
		//		while(i.hasNext()) 
		//		{ 
		//			Document page = i.next(); 
		//			String id = page.getString("id"); 
		//		}
	}

	private void youtube()
	{
		LOGGER.info("Loading Youtube...");
		try
		{
			MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.ne("tracks", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).noCursorTimeout(true).iterator();
			while(i.hasNext())
			{
				org.bson.Document page = i.next();
				String id = (String)page.get("id");
				youtube.loadTracks(page);
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
		catch(IOException e)
		{
			LOGGER.error("ERROR YoutubeLoader: " + e.getMessage(), e);
		}
	}
}
