package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader implements HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	private MongoCollection<org.bson.Document> docs = new MongoDB().getDocs();


	public static void main(String[] args) {
		new PatchLoader().replaceSpecialChars();
	}

	private void resetRefresh()
	{
		LOGGER.info("Resetting tracks title...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"),Filters.eq("source", "resetrefresh"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			page.append("source", "resetrefresh");
			page.append("artist", "Reset Refresh");
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}

	@Override
	public void load(String task) 
	{
		if("resetTracksTitle".equals(task)) {
			resetTracksTitle();
		}
		else if("calculateScore".equals(task)) {
			calculateScore();
		}
		else if("resetTracks".equals(task)) {
			resetTracks();
		}
		else if("replaceSpecialChars".equals(task)) {
			replaceSpecialChars();
		}
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
					String spotify = track.getString("spotify");
					if(spotify == null || NA.equals(spotify)) {
						track.append("title", track.getString("titleOrig"));
					}
				}
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}

	private void calculateScore()
	{
		LOGGER.info("Calculating podcasts score...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			int score = 0;
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					score += track.getInteger("score", 0);
				}
			}
			score = score/tracks.size();
			page.append("score", score);

			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}

	private void resetTracks()
	{
		LOGGER.info("Resetting tracks...");
		//		MongoCursor<Document> i = docs.find(Filters.eq("id", "50c6be1f6578b50c167a0fad0748168d0fa6f57ac031b7cdcfa9b7893c5c532d")).noCursorTimeout(true).iterator();
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.lt("tracks.score", 60))).noCursorTimeout(true).iterator();
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
					Integer score = track.getInteger("score");
					if(score != null && score < 60)
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
						track.append("score", null);
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
			page.append("score", null);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + ": " + title + " - " + artist);
		}
	}
}
