package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	public static void main(String[] args)
	{
		new PatchLoader().updateDesc();
	}

	public PatchLoader()
	{
		super();
	}

	private void resetTracks()
	{
		LOGGER.info("Resetting tracks...");
		MongoCursor<Document> i = docs.find(Filters.eq("type", "podcast")).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					if(NA.equals(track.getString("spotify")))
					{
						track.append("spotify", null);
						track.append("artistid", null);
					}
				}
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}

	private void replaceSpecialChars()
	{
		LOGGER.info("Replacing special chars...");
		MongoCursor<Document> i = docs.find(Filters.or(Filters.regex("title", ".*&.*;.*"), Filters.regex("artist", ".*&.*;.*"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			String title = page.getString("title");
			title = title.replaceAll("&amp;", "&");
			title = title.replaceAll("&gt;", ">");
			title = title.replaceAll("&lt;", "<");
			page.append("title", title);
			String artist = page.getString("artist");
			artist = artist.replaceAll("&amp;", "&");
			artist = artist.replaceAll("&gt;", ">");
			artist = artist.replaceAll("&lt;", "<");
			page.append("artist", artist);
			page.append("spartistid", null);
			page.append("spalbumid", null);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + ": " + title + " - " + artist);
		}
	}

	private void clearPodcasts()
	{
		LOGGER.info("Clearing podcasts...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", "seigradi"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			for(org.bson.Document track : tracks)
			{
				if(NA.equals(track.getString("spotify")))
				{
					track.append("spotify", null);
				}
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}

	private void updateDesc()
	{
		LOGGER.info("Updating description...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", "seigradi"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			String title = page.getString("title");
			page.append("description", title);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}
}
