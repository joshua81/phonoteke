package org.phonoteke.loader;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class YoutubeLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(YoutubeLoader.class);

	private static final String API_KEY = "AIzaSyDshyjPIgMCMIcwIG2JQfqZ7AR3kfrqHNI";

	private YouTube youtube;


	public static void main(String[] args) 
	{
		new YoutubeLoader().load();
	}

	public YoutubeLoader()
	{
		super();

		try 
		{
			youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {}
			}).setApplicationName("Phonoteke").build();
		}
		catch (Throwable t) 
		{
			LOGGER.error("ERROR getYoutube(): " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	private void load()
	{
		MongoCursor<org.bson.Document> i = tracks.find().iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String id = (String)page.get("id");
			for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks"))
			{
				String youtube = (String)track.get("youtube");
				String title = (String)track.get("title");
				if(youtube == null && title != null)
				{
					youtube = getYoutubeId(title);
					track.put("youtube", youtube);
					tracks.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
					LOGGER.info("id: " + id + ", title: " + title + ", youtube: " + youtube);
				}
				else if(title == null && youtube != null)
				{
					title = getYoutubeTitle(youtube);
					track.put("title", title);
					tracks.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
					LOGGER.info("id: " + id + ", title: " + title + ", youtube: " + youtube);
				}
			}
		}
	}

	private String getYoutubeId(String track) 
	{
		try
		{
			// Define the API request for retrieving search results.
			YouTube.Search.List search = youtube.search().list("id");

			// Set your developer key
			search.setKey(API_KEY);
			search.setQ(track);

			// Restrict the search results to only include videos. See:
			// https://developers.google.com/youtube/v3/docs/search/list#type
			search.setType("video");

			// To increase efficiency, only retrieve the fields that the
			// application uses.
			search.setFields("items(id/videoId)");
			search.setMaxResults(1L);

			// Call the API and print results.
			String youtubeId = "UNKNOWN";
			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResults = searchResponse.getItems();
			if(CollectionUtils.isNotEmpty(searchResults))
			{
				youtubeId = searchResults.get(0).getId().getVideoId();
			}
			return youtubeId;
		}
		catch(Exception e)
		{
			LOGGER.error("ERROR getYoutube(): " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private String getYoutubeTitle(String id) 
	{
		try
		{
			// Define the API request for retrieving search results.
			YouTube.Search.List search = youtube.search().list("snippet");

			// Set your developer key
			search.setKey(API_KEY);
			search.setQ(id);

			// Restrict the search results to only include videos. See:
			// https://developers.google.com/youtube/v3/docs/search/list#type
			search.setType("video");

			// To increase efficiency, only retrieve the fields that the
			// application uses.
			search.setFields("items(snippet/title)");
			search.setMaxResults(1L);

			// Call the API and print results.
			String youtubeTitle = "UNKNOWN";
			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResults = searchResponse.getItems();
			if(CollectionUtils.isNotEmpty(searchResults))
			{
				youtubeTitle = searchResults.get(0).getSnippet().getTitle();
			}
			return youtubeTitle;
		}
		catch(Exception e)
		{
			LOGGER.error("ERROR getYoutube(): " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
