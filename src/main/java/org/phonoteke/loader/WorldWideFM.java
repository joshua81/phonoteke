package org.phonoteke.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class WorldWideFM extends AbstractCrawler
{
	private static final Logger LOGGER = LogManager.getLogger(WorldWideFM.class);

	private static final String URL = "https://worldwidefm.net/";

	private static final String artist = "World Wide FM";
	private static final String source = "worldwidefm";
	private static final List<String> authors = Lists.newArrayList("World Wide FM");


	public static void main(String[] args) {
		new WorldWideFM().load();
	}

	@Override
	public void load(String... args) 
	{
		crawl(URL);
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return url.getURL().toLowerCase().startsWith(URL);
	}

	@Override
	public void visit(Page page) 
	{
		if(page.getWebURL().getURL().toLowerCase().startsWith(URL + "show")) {
			super.visit(page);
		}
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
			Element content = doc.select("span.show-time").first();
			if(content != null && content.children() != null)
			{
				date = new SimpleDateFormat("dd.MM.yy").parse(content.text());
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
		Element content = doc.select("div.description").first();
		if(content != null)
		{
			desc = content.text();
		}
		LOGGER.debug("description: " + desc);
		return desc;
	}

	@Override
	protected String getTitle(String url, Document doc) 
	{
		String title = null;
		Element content = doc.select("h2").first();
		if(content != null)
		{
			title = content.text();

		}
		LOGGER.debug("title: " + title);
		return title;
	}

	@Override
	protected List<org.bson.Document> getTracks(String url, Document doc) 
	{
		Element content = doc.select("div.tracklist").first();
		return getTracks(content, source);
	}

	@Override
	protected String getCover(String url, Document doc) 
	{
		String cover = null;
		Element content = doc.select("img.size-show_thumbnail[src]").first();
		if(content != null)
		{
			cover = content.attr("src");
			cover = getUrl(cover);
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
		String audio = null;
		Element content = doc.select("a.listen[href]").first();
		if(content != null)
		{
			audio = content.attr("href");
			audio = getUrl(audio);
		}
		LOGGER.debug("audio: " + audio);
		return audio;
	}
}
