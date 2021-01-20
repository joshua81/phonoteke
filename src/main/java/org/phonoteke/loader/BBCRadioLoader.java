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
	public static final String GILLES_PETERSON_SOURCE = "bbcradio6gillespeterson";
	public static final String JORJA_SMITH = "https://www.bbc.co.uk/programmes/m000r6g5/episodes/guide";
	public static final String JORJA_SMITH_SOURCE = "bbcradio3jorjasmith";

	private static String pageUrl;
	private static String artist;
	private static String source;
	private static List<String> authors;


	public static void main(String[] args) {
		new BBCRadioLoader().load(JORJA_SMITH_SOURCE);
	}

	@Override
	public void load(String... args) 
	{
		if(args.length == 0) {
			load(GILLES_PETERSON_SOURCE);
			load(JORJA_SMITH_SOURCE);
		}
		else if(GILLES_PETERSON_SOURCE.equals(args[0]))
		{
			BBCRadioLoader.pageUrl = GILLES_PETERSON;
			BBCRadioLoader.artist = "Gilles Peterson at Radio6";
			BBCRadioLoader.source = GILLES_PETERSON_SOURCE;
			BBCRadioLoader.authors = Lists.newArrayList("Gilles Peterson");
			crawl(GILLES_PETERSON);
		}
		else if(JORJA_SMITH_SOURCE.equals(args[0]))
		{
			BBCRadioLoader.pageUrl = JORJA_SMITH;
			BBCRadioLoader.artist = "Tearjerker with Jorja Smith";
			BBCRadioLoader.source = JORJA_SMITH_SOURCE;
			BBCRadioLoader.authors = Lists.newArrayList("Jorja Smith");
			crawl(JORJA_SMITH);
		}
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) 
	{
		return page.getWebURL().getURL().startsWith(pageUrl);
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
		Preconditions.checkArgument(year >= 2020);
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
		Preconditions.checkArgument(title.contains(artist));
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
				String title = track.select("h3").first() != null ? track.select("h3").first().text() : track.select("h4").first().text();
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
