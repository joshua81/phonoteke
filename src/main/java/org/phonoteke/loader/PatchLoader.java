package org.phonoteke.loader;

import java.time.LocalDateTime;
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
		new PatchLoader().renameSource();
	}

	@Override
	public void load(String... args) 
	{
		if("resetTracksTitle".equals(args[0])) {
			resetTracksTitle();
		}
		else if("calculateScore".equals(args[0])) {
			calculateScore();
		}
		else if("resetTracks".equals(args[0])) {
			int year = Integer.parseInt(args[1]);
			resetTracks(year);
		}
		else if("replaceSpecialChars".equals(args[0])) {
			replaceSpecialChars();
		}
		else if("deleteDoc".equals(args[0])) {
			deleteDoc(null);
		}
		else if("deleteEmptyPlaylist".equals(args[0])) {
			deleteEmptyPlaylists();
		}
	}

	private void deleteEmptyPlaylists()
	{
		LOGGER.info("Deleting empty playlists...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.size("tracks", 5))).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			docs.deleteOne(Filters.eq("id", id));
			LOGGER.info("Document " + id + " deleted. Tracks size "+ tracks.size());	
		}

		LOGGER.info("Deleting dismissed playlists...");
		i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.or(Filters.eq("source", "ondarock"), Filters.eq("source", "playaestas")))).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			String source = page.getString("source");
			docs.deleteOne(Filters.eq("id", id));
			LOGGER.info("Document " + id + " deleted. Source "+ source);	
		}
	}

	private void deleteDoc(String id)
	{
		LOGGER.info("Deleting doc...");
		docs.deleteOne(Filters.eq("id", id));
		LOGGER.info("Document " + id + " deleted");	
	}

	private void deleteDocs()
	{
		LOGGER.info("Deleting docs...");
		MongoCursor<Document> i = docs.find(Filters.eq("source", "worldwidefm")).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			docs.deleteOne(Filters.eq("id", id));
			LOGGER.info("Document " + id + " deleted");
		}
	}

	private void renameSource()
	{
		LOGGER.info("Renaming source...");
		MongoCursor<Document> i = docs.find(Filters.eq("source", "bbcradio6stevelamacq")).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			page.append("source", "stevelamacq");
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Document " + id + " updated");
		}
	}

	private void resetTracksTitle()
	{
		LOGGER.info("Resetting tracks title...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).iterator();
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
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).iterator();
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

	private void resetTracks(int year)
	{
		LOGGER.info("Resetting " + year + " tracks...");
		LocalDateTime start = LocalDateTime.now().withYear(year).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
		LocalDateTime end = LocalDateTime.now().withYear(year).withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59);
		MongoCursor<Document> i = docs.find(Filters.and(
				Filters.eq("type", "podcast"), 
				Filters.lt("tracks.score", SCORE),
				Filters.gt("date", start),
				Filters.lt("date", end))).iterator();
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
					if(score != null && score < SCORE)
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
		MongoCursor<Document> i = docs.find(Filters.or(Filters.regex("title", ".*&.*;.*"), Filters.regex("artist", ".*&.*;.*"))).iterator();
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
