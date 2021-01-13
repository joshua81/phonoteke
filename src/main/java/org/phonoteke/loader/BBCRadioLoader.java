package org.phonoteke.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class BBCRadioLoader extends AbstractCrawler
{
	private static final Logger LOGGER = LogManager.getLogger(BBCRadioLoader.class);

	public static final String URL = "https://www.bbc.co.uk/";
	public static final String GILLES_PETERSON = "https://www.bbc.co.uk/programmes/b01fm4ss/episodes/guide";
	public static final String ARTIST = "BBC Radio 6 - Gilles Peterson";
	public static final String SOURCE = "bbcradio6gillespeterson";
	public static final List<String> AUTHORS = Lists.newArrayList("Gilles Peterson");


	public static void main(String[] args) {
		new BBCRadioLoader().load();
	}

	@Override
	public void load(String... args) 
	{
		crawl(GILLES_PETERSON);
		//		for(int i = 2; i <= 10; i++) {
		//			crawl(GILLES_PETERSON + "?page" + i);
		//		}
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) 
	{
		return page.getWebURL().getURL().startsWith(GILLES_PETERSON) && url.getURL().substring((URL + "programmes/").length()).length() == 8;
	}

	@Override
	protected String getBaseUrl()
	{
		return URL;
	}

	@Override
	protected String getSource() 
	{
		return SOURCE;
	}

	@Override
	protected String getArtist(String url, Document doc) 
	{
		return ARTIST;
	}

	@Override
	protected List<String> getAuthors(String url, Document doc) 
	{
		return AUTHORS;
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
		LOGGER.debug("date: " + date);
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
		LOGGER.debug("description: " + desc);
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
		Preconditions.checkArgument(title.startsWith("BBC Radio 6 Music - Gilles Peterson"));
		LOGGER.debug("title: " + title);
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
				String title = track.select("h3").first().text();
				title += " - " + track.select("p").first().text();
				tracks.add(newTrack(title, null));
				LOGGER.debug("track: " + title);
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
		LOGGER.debug("cover: " + cover);
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
