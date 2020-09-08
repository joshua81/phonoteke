package org.phonoteke.loader;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phonoteke.loader.HumanBeats.TYPE;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class YoutubeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(YoutubeLoader.class);

	private MongoCollection<org.bson.Document> docs = new MongoDB().getDocs();
	
	private YouTube youtube = null;


	public YoutubeLoader()
	{
		super();
		try 
		{
			youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {}
			}).setApplicationName("humanbeats").build();
		}
		catch (Throwable t) 
		{
			LOGGER.error("ERROR YoutubeLoader: " + t.getMessage());
		}
	}

	protected void load(String task)
	{
		try
		{
			LOGGER.info("Loading Youtube...");
			MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.podcast.name()), 
					Filters.or(Filters.exists("tracks.youtube", false),Filters.eq("tracks.youtube", null)))).
					sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).noCursorTimeout(true).iterator();
			while(i.hasNext())
			{
				org.bson.Document page = i.next();
				loadTracks(page);
			}
		}
		catch(IOException e)
		{
			LOGGER.error("ERROR YoutubeLoader: " + e.getMessage());
		}
	}

	private void loadTracks(org.bson.Document page) throws IOException
	{
		String id = page.getString("id");
		for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks"))
		{
			String title = track.getString("title");
			String youtube = track.getString("youtube");
			if(youtube == null)
			{
				youtube = getYoutubeId(title);
				track.put("youtube", youtube);
				LOGGER.info(id + ", title: " + title + ", youtube: " + youtube);
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
	}

	private String getYoutubeId(String track) throws IOException
	{
		// Define the API request for retrieving search results.
		YouTube.Search.List search = youtube.search().list("id,snippet");

		// Set your developer key
		search.setKey(System.getenv("YOUTUBE_KEY"));
		search.setQ(track);

		// Restrict the search results to only include videos. See:
		// https://developers.google.com/youtube/v3/docs/search/list#type
		search.setType("video");

		// To increase efficiency, only retrieve the fields that the
		// application uses.
		search.setFields("items(id/videoId,snippet/title)");
		search.setMaxResults(1L);

		// Call the API and print results.
		SearchListResponse searchResponse = search.execute();
		List<SearchResult> searchResults = searchResponse.getItems();
		if(CollectionUtils.isNotEmpty(searchResults))
		{
			for(SearchResult searchResult : searchResults)
			{
				String id = searchResult.getId().getVideoId();
				String title = searchResult.getSnippet().getTitle();
				int score = FuzzySearch.tokenSetRatio(track, title);
				LOGGER.info(track + ", " + title + " (score: " + score + ")");
				if(score >= HumanBeats.THRESHOLD)
				{
					return id;
				}
			}
		}
		return null;
	}
}
