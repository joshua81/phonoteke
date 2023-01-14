package org.phonoteke.loader;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.phonoteke.loader.Utils.TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.internal.operation.OrderBy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@Component
@Slf4j
public class YoutubeLoader
{
	private static final String MATCH1 = "(?i)(.{1,100})[\\(\\[]Official(.{1,10})Video[\\)\\]]";
	private static final String MATCH2 = "(?i)(.{1,100})[\\(\\[]Video(.{1,10})Ufficiale[\\)\\]]";
	private static final List<String> MATCH = Lists.newArrayList(MATCH1, MATCH2);

	@Autowired
	private MongoRepository repo;

	private YouTube youtube = null;
	

	@PostConstruct
	@SneakyThrows
	public void init()
	{
		youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {}
		}).setApplicationName("humanbeats").build();
	}

	public void load(String... args)
	{
		log.info("Loading Youtube...");
		MongoCursor<Document> i = repo.getDocs().find(Filters.and(Filters.eq("type", TYPE.podcast.name()), 
				Filters.or(Filters.exists("tracks.youtube", false),Filters.eq("tracks.youtube", null)))).
				sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).iterator();
		while(i.hasNext())
		{
			Document page = i.next();
			String id = page.getString("id");
			try {
				loadTracks(page);
			} 
			catch (IOException e) {
				log.error("ERROR YoutubeLoader: " + e.getMessage());
				return;
			}
			finally {
				repo.getDocs().updateOne(Filters.eq("id", id), new Document("$set", page));
			}
		}
	}

	private void loadTracks(Document page) throws IOException
	{
		String id = page.getString("id");
		for(Document track : (List<Document>)page.get("tracks"))
		{
			String title = track.getString("title");
			String youtube = track.getString("youtube");
			String spotify = track.getString("spotify");
			Integer score = track.getInteger("score");
			if(youtube == null)
			{
				if(!Utils.NA.equals(spotify) && score != null && score >= Utils.THRESHOLD) {
					youtube = getYoutubeId(title);
					track.put("youtube", youtube);
					log.info("title: " + title + ", youtube: " + youtube);
				}
				else {
					youtube = Utils.NA;
				}
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
				if(score >= Utils.THRESHOLD) {
					for(String match : MATCH) {
						if(title.matches(match)) {
							return id;
						}
					}
				}
			}
		}
		return Utils.NA;
	}
}
