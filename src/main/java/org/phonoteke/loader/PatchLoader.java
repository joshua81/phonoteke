package org.phonoteke.loader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	public static void main(String[] args)
	{
		new PatchLoader().resetTracksTitle();
	}

	public PatchLoader()
	{
		super();
	}

	private void resetTracksTitle()
	{
		LOGGER.info("Resetting tracks title...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					String title = track.getString("titleOrig");
					List<String> chunks = Lists.newArrayList();
					for(String match : TRACKS_MATCH)
					{
						Pattern p = Pattern.compile(match);
						Matcher m = p.matcher(title);
						if(m.matches()) {
							for(int j=1; j<= m.groupCount(); j++){
								chunks.add(m.group(j));
							}
							break;
						}
					}

					if(chunks.size() >= 2) {
						track.append("title", StringUtils.capitalize(chunks.get(0).trim()) + " - " + StringUtils.capitalize(chunks.get(1).trim()));
					}
				}
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}

	private void resetTracks()
	{
		LOGGER.info("Resetting tracks...");
		//		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).noCursorTimeout(true).iterator();
		MongoCursor<Document> i = docs.find(Filters.eq("id", "b771b4dc8081520f7b43f3788b63dfc5a6f6587d7e68c38e26c9ae02ca8397bb")).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{
			boolean update = false;
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
						track.append("youtube", null);
						track.append("spotify", null);
						track.append("artist", null);
						track.append("album", null);
						track.append("track", null);
						track.append("spartistid", null);
						track.append("spalbumid", null);
						track.append("coverL", null);
						track.append("coverM", null);
						track.append("coverS", null);
						track.append("artistid", null);
						update = true;
					}
				}
			}
			if(update) {
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
				LOGGER.info("Document " + id + " updated");
			}
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

	private void deletePodcasts()
	{
		LOGGER.info("Deleting podcasts...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", "stereonotte"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			docs.deleteOne(Filters.eq("id", id));
			LOGGER.info("Document " + id + " deleted");
		}
	}

	private void clearPodcasts()
	{
		LOGGER.info("Clearing podcasts...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", "stereonotte"))).noCursorTimeout(true).iterator();
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

	private void updateBertallot()
	{
		LOGGER.info("Updating Bertallot...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", "casabertallot"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			page.append("artist", "Casa Bertallot");
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}
}
