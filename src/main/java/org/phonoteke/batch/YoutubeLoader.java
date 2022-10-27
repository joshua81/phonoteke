package org.phonoteke.batch;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.phonoteke.batch.model.Doc;
import org.springframework.stereotype.Component;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.common.collect.Lists;

import lombok.extern.log4j.Log4j2;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@Log4j2
@Component
public class YoutubeLoader extends HumanBeats
{
	private static final String MATCH1 = "(?i)(.{1,100})[\\(\\[]Official(.{1,10})Video[\\)\\]]";
	private static final String MATCH2 = "(?i)(.{1,100})[\\(\\[]Video(.{1,10})Ufficiale[\\)\\]]";
	private static final List<String> MATCH = Lists.newArrayList(MATCH1, MATCH2);

	private YouTube youtube = null;


	@PostConstruct
	public void init()
	{
		try {
			youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {}
			}).setApplicationName("humanbeats").build();
		}
		catch (Throwable t) {
			logger.error("ERROR YoutubeLoader: " + t.getMessage());
		}
	}

	@Override
	public void load(String... args)
	{
		logger.info("Loading Youtube...");
		List<Doc> pages = docs.findByType(TYPE.podcast.name());
		pages.stream().forEach(page -> {
			loadTracks(page);
			docs.save(page);
		});
	}

	private void loadTracks(Doc page)
	{
		String id = page.getId();
		page.getTracks().forEach(track -> {
			String title = track.getString("title");
			String youtube = track.getString("youtube");
			String spotify = track.getString("spotify");
			Integer score = track.getInteger("score");
			if(youtube == null)
			{
				if(!NA.equals(spotify) && score != null && score >= THRESHOLD) {
					youtube = getYoutubeId(title);
					track.put("youtube", youtube);
					logger.info("title: " + title + ", youtube: " + youtube);
				}
				else {
					youtube = NA;
				}
			}
		});
	}

	private String getYoutubeId(String track)
	{
		try {
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
					if(score >= THRESHOLD) {
						for(String match : MATCH) {
							if(title.matches(match)) {
								return id;
							}
						}
					}
				}
			}
		}
		catch (IOException e) {
			logger.error(e.getMessage());
		}
		return NA;
	}
}
