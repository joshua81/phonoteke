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
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

public class YoutubeLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(YoutubeLoader.class);

	private static final String API_KEY = "AIzaSyAYqrw65aNPioXzxuzlW4qW9j3GiKkqduo";
	private static final String UNKNOWN = "UNKNOWN";
	private YouTube youtube;


	public static void main(String[] args)
	{
		new YoutubeLoader().loadTracks();
	}

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
			LOGGER.error("ERROR YoutubeLoader: " + t.getMessage(), t);
		}
	}

	public void loadTracks()
	{
		try
		{
			LOGGER.info("Loading Youtube");
			MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "album"), Filters.ne("tracks", null))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).noCursorTimeout(true).iterator();
			while(i.hasNext())
			{
				org.bson.Document page = i.next();
				String id = (String)page.get("id");
				for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks"))
				{
					String youtube = track.getString("youtube");
					String title = track.getString("title");
					if(youtube == null && title != null)
					{
						youtube = getYoutubeId(title);
						track.put("youtube", youtube);
						LOGGER.info("YOUTUBE id: " + id + ", title: " + title + ", youtube: " + youtube);
					}
					else if(title == null && youtube != null)
					{
						title = getYoutubeTitle(youtube);
						track.put("title", title);
						LOGGER.info("YOUTUBE id: " + id + ", title: " + title + ", youtube: " + youtube);
					}
				}
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR YoutubeLoader: " + t.getMessage(), t);
		}
	}

	private String getYoutubeId(String track) throws IOException
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
		String youtubeId = UNKNOWN;
		SearchListResponse searchResponse = search.execute();
		List<SearchResult> searchResults = searchResponse.getItems();
		if(CollectionUtils.isNotEmpty(searchResults))
		{
			youtubeId = searchResults.get(0).getId().getVideoId();
		}
		return youtubeId;
	}

	private String getYoutubeTitle(String id) throws IOException
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
		String youtubeTitle = UNKNOWN;
		SearchListResponse searchResponse = search.execute();
		List<SearchResult> searchResults = searchResponse.getItems();
		if(CollectionUtils.isNotEmpty(searchResults))
		{
			youtubeTitle = searchResults.get(0).getSnippet().getTitle();
		}
		return youtubeTitle;
	}
}
