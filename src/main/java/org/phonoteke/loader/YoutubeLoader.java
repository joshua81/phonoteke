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
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class YoutubeLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(YoutubeLoader.class);

	private static final String API_KEY = "AIzaSyDshyjPIgMCMIcwIG2JQfqZ7AR3kfrqHNI";

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
			}).setApplicationName("Phonoteke").build();
			beforeStart();
		}
		catch (Throwable t) 
		{
			LOGGER.error("ERROR YoutubeLoader: " + t.getMessage(), t);
		}
	}

	private void beforeStart()
	{
		MongoCursor<org.bson.Document> i = tracks.find(Filters.and(Filters.ne("tracks.youtube", null), Filters.ne("tracks.title", null))).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document json = i.next();
			List<org.bson.Document> mbtracks = json.get("tracks", List.class);
			org.bson.Document page = docs.find(Filters.eq("id", json.get("id"))).noCursorTimeout(true).iterator().tryNext();
			if(page != null)
			{
				String id = page.getString("id");
				List<org.bson.Document> tracks = page.get("tracks", List.class);
				for(org.bson.Document mbtrack : mbtracks)
				{
					for(org.bson.Document track : tracks)
					{
						if(mbtrack.getString("youtube").equals(track.getString("youtube")))
						{
							track.append("title", mbtrack.getString("title"));
						}
						else if(mbtrack.getString("title").equals(track.getString("title")))
						{
							track.append("youtube", mbtrack.getString("youtube"));
						}
					}
				}
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
				LOGGER.info("YOUTUBE id: " + id + " updated");
			}
		}
	}

	public void loadTracks()
	{
		try
		{
			LOGGER.info("Loading Youtube");
			MongoCursor<org.bson.Document> i = tracks.find().noCursorTimeout(true).iterator();
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
						LOGGER.info("YOUTUBE id: " + id + ", title: " + title + ", youtube: " + youtube);
					}
					else if(title == null && youtube != null)
					{
						title = getYoutubeTitle(youtube);
						track.put("title", title);
						tracks.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
						LOGGER.info("YOUTUBE id: " + id + ", title: " + title + ", youtube: " + youtube);
					}
				}
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
		String youtubeId = "UNKNOWN";
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
		String youtubeTitle = "UNKNOWN";
		SearchListResponse searchResponse = search.execute();
		List<SearchResult> searchResults = searchResponse.getItems();
		if(CollectionUtils.isNotEmpty(searchResults))
		{
			youtubeTitle = searchResults.get(0).getSnippet().getTitle();
		}
		return youtubeTitle;
	}
}
