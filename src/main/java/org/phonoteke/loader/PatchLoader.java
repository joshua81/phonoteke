package org.phonoteke.loader;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PatchLoader
{
	@Autowired
	private MongoRepository repo;
	
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
		log.info("Deleting empty playlists...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.and(Filters.eq("type", "podcast"), Filters.size("tracks", 5))).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			repo.getDocs().deleteOne(Filters.eq("id", id));
			log.info("Document " + id + " deleted. Tracks size "+ tracks.size());	
		}

		log.info("Deleting dismissed playlists...");
		i = repo.getDocs().find(Filters.and(Filters.eq("type", "podcast"), Filters.or(Filters.eq("source", "ondarock"), Filters.eq("source", "playaestas")))).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			String source = page.getString("source");
			repo.getDocs().deleteOne(Filters.eq("id", id));
			log.info("Document " + id + " deleted. Source "+ source);	
		}
	}

	private void deleteDoc(String id)
	{
		log.info("Deleting doc...");
		repo.getDocs().deleteOne(Filters.eq("id", id));
		log.info("Document " + id + " deleted");	
	}

	private void deleteDocs()
	{
		log.info("Deleting repo.getDocs()...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.eq("source", "worldwidefm")).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			repo.getDocs().deleteOne(Filters.eq("id", id));
			log.info("Document " + id + " deleted");
		}
	}

	private void renameSource()
	{
		log.info("Renaming source...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.eq("source", "bbcradio6stevelamacq")).iterator();
		while(i.hasNext()) 
		{
			Document page = i.next();
			String id = page.getString("id");
			page.append("source", "stevelamacq");
			repo.getDocs().updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			log.info("Document " + id + " updated");
		}
	}

	private void resetTracksTitle()
	{
		log.info("Resetting tracks title...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.and(Filters.eq("type", "podcast"))).iterator();
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
					if(spotify == null || Utils.NA.equals(spotify)) {
						track.append("title", track.getString("titleOrig"));
					}
				}
			}
			repo.getDocs().updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			log.info("Document " + id + " updated");
		}
	}

	private void calculateScore()
	{
		log.info("Calculating podcasts score...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.and(Filters.eq("type", "podcast"))).iterator();
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

			repo.getDocs().updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			log.info("Document " + id + " updated");
		}
	}

	private void resetTracks(int year)
	{
		log.info("Resetting " + year + " tracks...");
		LocalDateTime start = LocalDateTime.now().withYear(year).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
		LocalDateTime end = LocalDateTime.now().withYear(year).withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59);
		MongoCursor<Document> i = repo.getDocs().find(Filters.and(
				Filters.eq("type", "podcast"), 
				Filters.lt("tracks.score", Utils.SCORE),
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
					if(score != null && score < Utils.SCORE)
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
				repo.getDocs().updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
				log.info("Document " + id + " updated");
			}
		}
	}

	private void replaceSpecialChars()
	{
		log.info("Replacing special chars...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.or(Filters.regex("title", ".*&.*;.*"), Filters.regex("artist", ".*&.*;.*"))).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");

			String title = page.getString("title");
			title = Utils.cleanText(title);
			page.append("title", title);

			String artist = page.getString("artist");
			artist = Utils.cleanText(artist);
			page.append("artist", artist);

			page.append("spartistid", null);
			page.append("spalbumid", null);
			page.append("score", null);
			repo.getDocs().updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			log.info("Document " + id + ": " + title + " - " + artist);
		}
	}
}
