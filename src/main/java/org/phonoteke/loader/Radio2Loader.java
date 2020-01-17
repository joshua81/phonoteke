package org.phonoteke.loader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class Radio2Loader extends PhonotekeLoader
{
	private static final String URL = "https://www.raiplayradio.it/";
	private static final String BABYLON = "https://www.raiplayradio.it/programmi/babylon/";
	private static final String MUSICALBOX = "https://www.raiplayradio.it/programmi/musicalbox/";
	private static final String INTHEMIX = "https://www.raiplayradio.it/programmi/radio2inthemix/";
	private static final List<String> URLS = Lists.newArrayList(	BABYLON, MUSICALBOX, INTHEMIX, "https://www.raiplayradio.it/audio");
	private static final String NEW_LINE = "_NEW_LINE_";
	private static final List<String> TRIM = Lists.newArrayList("100% Bellamusica ®", "PLAYLIST:", "PLAYLIST", "TRACKLIST:", "TRACKLIST", "PLAY:", "PLAY", "LIST:", "LIST", "TRACKS:", "TRACKS");

	private static String artist;
	private static String source;


	public static void main(String[] args) 
	{
		if(args.length == 1)
		{
			if("babylon".equals(args[0]))
			{
				new Radio2Loader("Babylon", "babylon").crawl(BABYLON);
			}
			else if("musicalbox".equals(args[0]))
			{
				new Radio2Loader("Musicalbox", "musicalbox").crawl(MUSICALBOX);
			}
			else if("inthemix".equals(args[0]))
			{
				new Radio2Loader("Inthemix", "inthemix").crawl(INTHEMIX);
			}
			else if("test".equals(args[0]))
			{
				new Radio2Loader("Musicalbox", "musicalbox").crawl("https://www.raiplayradio.it/audio/2018/03/MUSICAL-BOX-996c7a54-e845-4f4f-a9e1-3dd18379f956.html");
			}
		}
	}

	public Radio2Loader()
	{
		// default constructor
	}

	public Radio2Loader(String artist, String source)
	{
		super();
		Radio2Loader.artist = artist;
		Radio2Loader.source = source;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		for(String u : URLS)
		{
			if(url.getURL().toLowerCase().startsWith(u))
			{
				return true;
			}
		}
		return false;
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
		try
		{
			if(date == null)
			{
				int year = Integer.parseInt(url.split("/")[4]);
				int month = Integer.parseInt(url.split("/")[5])-1;
				int day = 1;
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				date = cal.getTime();
			}
		}
		catch(NumberFormatException e)
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
		Element content = doc.select("div.aodDescription").first();
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
		Element content = doc.select("h1").first();
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
		List<org.bson.Document> tracks = Lists.newArrayList();
		Element content = doc.select("div.aodHtmlDescription").first();
		if(content != null)
		{
			content.select("br").after(NEW_LINE);
			content.select("p").after(NEW_LINE);
			content.select("li").after(NEW_LINE);
			content.select("h1").after(NEW_LINE);
			content.select("h2").after(NEW_LINE);
			content.select("h3").after(NEW_LINE);
			content.select("div").after(NEW_LINE);
			String[] chunks = content.text().replace("||", NEW_LINE).split(NEW_LINE);
			for(int i = 0; i < chunks.length; i++)
			{
				String title = chunks[i].trim();
				if(StringUtils.isNotBlank(title))
				{
					for(String p : TRIM)
					{
						if(title.toUpperCase().startsWith(p))
						{
							title = title.substring(p.length()).trim();
						}
					}
				}
				if(StringUtils.isNotBlank(title) && isTrack(title))
				{
					String youtube = null;
					tracks.add(newTrack(title, youtube));
					LOGGER.debug("tracks: " + title + ", youtube: " + youtube);
				}
			}
		}
		if(CollectionUtils.isEmpty(tracks))
		{
			throw new IllegalArgumentException("Empty tracks!");
		}
		return tracks;
	}

	@Override
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

	@Override
	protected TYPE getType(String url) 
	{
		return TYPE.podcast;
	}

	@Override
	protected String getAudio(String url, Document doc) 
	{
		String audio = null;
		try
		{
			Element content = doc.select("div[data-player-href]").first();
			if(content != null)
			{
				doc = Jsoup.connect(URL + content.attr("data-player-href")).get();
				content = doc.select("li[data-mediapolis]").first();
				if(content != null)
				{
					Jsoup.connect(content.attr("data-mediapolis")).get();
				}
			}
		}
		catch(IOException e)
		{
			audio = ((UnsupportedMimeTypeException)e).getUrl();
		}
		LOGGER.debug("audio: " + audio);
		return audio;
	}

	@Override
	protected List<String> getAuthors(String url, Document doc) 
	{
		return Radio2Loader.source.equals("babylon") ? Lists.newArrayList("Carlo Pastore") : 
			Radio2Loader.source.equals("musicalbox") ? Lists.newArrayList("Raffaele Costantino") : 
				Radio2Loader.source.equals("inthemix") ? Lists.newArrayList("Lele Sacchi") : null;
	}
}
