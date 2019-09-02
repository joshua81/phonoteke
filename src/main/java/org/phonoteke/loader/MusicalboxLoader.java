package org.phonoteke.loader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.phonoteke.model.ModelUtils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.common.collect.Lists;

public class MusicalboxLoader extends PhonotekeLoader
{
	private static final String URL = "https://www.raiplayradio.it/";
	private static final String ARTIST = "Musicalbox";
	private static final String SOURCE = "musicalbox";
	private static final List<String> ERRORS = Lists.newArrayList("An internal error occurred", "[an error occurred while processing this directive]", "PLAY");

	public MusicalboxLoader()
	{
		super();
	}

	protected String getBaseUrl()
	{
		return URL;
	}

	protected String getSource() 
	{
		return SOURCE;
	}

	protected String getArtist(String url, Document doc) 
	{
		return ARTIST;
	}

	protected Date getDate(String url, Document doc) 
	{
		Date date = null;
		try
		{
			Element content = doc.select("ul.menuDescriptionProgramma").first();
			if(content != null && content.children() != null)
			{
				date = new SimpleDateFormat("dd/MM/yyyy").parse(content.children().get(0).text());
			}
		}
		catch(ParseException e)
		{
			// nothing to do
		}
		LOGGER.debug("date: " + date);
		return date;
	}
	
	protected String getReview(String url, Document doc) 
	{
		return getDescription(url, doc);
	}

	protected String getDescription(String url, Document doc) 
	{
		String desc = null;
		Element content = doc.select("div.aodDescription").first();
		if(content != null)
		{
			desc = content.text();

		}
		LOGGER.debug("description: " + desc);
		return desc;
	}

	protected String getTitle(String url, Document doc) 
	{
		String title = null;
		Element content = doc.select("h1").first();
		if(content != null)
		{
			title = content.text();

		}
		LOGGER.debug("title: " + title);
		return title;
	}

	protected List<Map<String, String>> getTracks(String url, Document doc) 
	{
		List<Map<String, String>> tracks = Lists.newArrayList();
		Element content = doc.select("div.aodHtmlDescription").first();
		if(content != null && content.children() != null)
		{
			Iterator<Element> i = content.children().iterator();
			while(i.hasNext())
			{
				String title = i.next().text().trim();
				if(StringUtils.isNoneBlank(title) && !ERRORS.contains(title))
				{
					String youtube = null;//getYoutube(track);
					tracks.add(ModelUtils.newTrack(title, youtube));
					LOGGER.debug("tracks: " + title + ", youtube: " + youtube);
				}
			}
		}
		if(content != null && content.textNodes() != null)
		{
			Iterator<TextNode> i = content.textNodes().iterator();
			while(i.hasNext())
			{
				String title = i.next().text().trim();
				if(StringUtils.isNoneBlank(title) && !ERRORS.contains(title))
				{
					String youtube = null;//getYoutube(title);
					tracks.add(ModelUtils.newTrack(title, youtube));
					LOGGER.debug("tracks: " + title + ", youtube: " + youtube);
				}
			}
		}
		return tracks;
	}

	protected String getCover(String url, Document doc) 
	{
		String cover = null;
		Element content = doc.select("img.imgHomeProgramma[src]").first();
		if(content != null)
		{
			cover = content.attr("src");
			cover = getUrl(cover);
		}
		LOGGER.debug("cover: " + cover);
		return cover;
	}

	private String getYoutube(String track) 
	{
		try
		{
			// This object is used to make YouTube Data API requests. The last
			// argument is required, but since we don't need anything
			// initialized when the HttpRequest is initialized, we override
			// the interface and provide a no-op function.
			YouTube youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
				public void initialize(HttpRequest request) throws IOException {}
			}).setApplicationName("Phonoteke").build();

			// Define the API request for retrieving search results.
			YouTube.Search.List search = youtube.search().list("id,snippet");

			// Set your developer key, es. "AIzaSyDshyjPIgMCMIcwIG2JQfqZ7AR3kfrqHNI"
			String apiKey = null;
			search.setKey(apiKey);
			search.setQ(track);

			// Restrict the search results to only include videos. See:
			// https://developers.google.com/youtube/v3/docs/search/list#type
			search.setType("video");

			// To increase efficiency, only retrieve the fields that the
			// application uses.
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(1L);

			// Call the API and print results.
			String youtubeId = null;
			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResults = searchResponse.getItems();
			if(CollectionUtils.isNotEmpty(searchResults))
			{
				youtubeId = searchResults.get(0).getId().getVideoId();
			}
			LOGGER.debug("youtube: " + youtubeId);
			return youtubeId;
		}
		catch(Exception e)
		{
			LOGGER.error("ERROR getYoutube(): " + e.getMessage());
			return null;
		}
	}

	protected TYPE getType(String url, Document doc) 
	{
		return TYPE.ALBUM;
	}
}
