package org.phonoteke.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.phonoteke.loader.HumanBeatsUtils.TYPE;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BBCRadioCrawler extends AbstractCrawler
{
	private static final String URL = "https://www.bbc.co.uk/";
	private static final String BBC = "bbc";


	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", BBC))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", BBC), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			BBCRadioCrawler.url = show.getString("url");
			BBCRadioCrawler.artist = show.getString("title");
			BBCRadioCrawler.source = show.getString("source");
			BBCRadioCrawler.authors = show.get("authors", List.class);
			BBCRadioCrawler.page = args.length == 2 ? Integer.parseInt(args[1]) : 1;

			log.info("Crawling " + artist + " (" + page + " page)");
			crawl(url + "?page=" + page);
			updateLastEpisodeDate(source);
		}
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) 
	{
		return page.getWebURL().getURL().startsWith(this.url);
	}

	@Override
	protected String getBaseUrl()
	{
		return URL;
	}

	@Override
	protected String getSource() 
	{
		return source;
	}

	@Override
	protected String getArtist(String url, Document doc) 
	{
		return artist;
	}

	@Override
	protected List<String> getAuthors(String url, Document doc) 
	{
		return authors;
	}

	@Override
	protected Date getDate(String url, Document doc) 
	{
		Date date = null;
		try
		{
			Element content = doc.select("div.broadcast-event__time").first();
			if(content != null)
			{
				date = new SimpleDateFormat("yyyy-MM-dd").parse(content.attr("content").trim().substring(0, 10));
			}
		}
		catch(ParseException e)
		{
			// nothing to do
		}
		log.debug("date: " + date);
		return date;
	}

	@Override
	protected Integer getYear(String url, Document doc) 
	{
		Integer year = null;
		Date date = getDate(url, doc);
		if(date != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			year = cal.get(Calendar.YEAR);
		}
		return year;
	}

	@Override
	protected String getDescription(String url, Document doc) 
	{
		String desc = null;
		Element content = doc.select("meta[property=og:description]").first();
		if(content != null)
		{
			desc = content.attr("content").trim();
		}
		log.debug("description: " + desc);
		return desc;
	}

	@Override
	protected String getTitle(String url, Document doc) 
	{
		String title = null;
		Element content = doc.select("meta[property=og:title]").first();
		if(content != null)
		{
			title = content.attr("content").trim();
		}
		log.debug("title: " + title);
		return title;
	}

	@Override
	protected List<org.bson.Document> getTracks(String url, Document doc) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		Elements content = doc.select("div.segment__track");
		if(content != null && content.size() > 0) {
			Iterator<Element> i = content.iterator();
			while(i.hasNext()) {
				Element track = i.next();
				String title = track.select("h3").first() != null ? track.select("h3").first().text() : track.select("h4").first().text();
				title += " - " + track.select("p").first().text();
				tracks.add(newTrack(title, null));
				log.debug("track: " + title);
			}
		}
		return checkTracks(tracks);
	}

	@Override
	protected String getCover(String url, Document doc) 
	{
		String cover = null;
		Element content = doc.select("meta[property=og:image]").first();
		if(content != null)
		{
			cover = content.attr("content").trim();
		}
		log.debug("cover: " + cover);
		return cover;
	}

	@Override
	protected TYPE getType(String url) 
	{
		return TYPE.podcast;
	}

	@Override
	protected String getAudio(String url, Document doc) 
	{
		return null;
	}
}
