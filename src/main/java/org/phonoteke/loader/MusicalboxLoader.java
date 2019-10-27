package org.phonoteke.loader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import com.google.common.collect.Lists;

public class MusicalboxLoader extends PhonotekeLoader
{
	public static final String URL = "https://www.raiplayradio.it/";
	public static final String SOURCE = "musicalbox";

	public static final List<String> ERRORS = Lists.newArrayList("An internal error occurred", 
			"[an error occurred while processing this directive]", 
			"PLAY:", "PLAY",
			"TRACKLIST:", "TRACKLIST");
	private static final String ARTIST = "Musicalbox";


	public static void main(String[] args) 
	{
		new MusicalboxLoader().loadDocuments();
	}

	public MusicalboxLoader()
	{
		super();
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
		if(content != null && content.children() != null)
		{
			Iterator<Element> i = content.children().iterator();
			while(i.hasNext())
			{
				String title = i.next().text().trim();
				if(StringUtils.isNotBlank(title))
				{
					for(String error : ERRORS)
					{
						if(title.startsWith(error))
						{
							title = title.substring(error.length()).trim();
							break;
						}
					}
				}
				if(StringUtils.isNotBlank(title))
				{
					String youtube = null;
					tracks.add(newTrack(title, youtube));
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
				if(StringUtils.isNotBlank(title))
				{
					for(String error : ERRORS)
					{
						if(title.startsWith(error))
						{
							title = title.substring(error.length()).trim();
							break;
						}
					}
				}
				if(StringUtils.isNotBlank(title))
				{
					String youtube = null;
					tracks.add(newTrack(title, youtube));
					LOGGER.debug("tracks: " + title + ", youtube: " + youtube);
				}
			}
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
		return TYPE.ALBUM;
	}

	@Override
	protected String getAudio(String url, Document doc) 
	{
		String audio = null;
		try
		{
			Element content = doc.select("div[data-mediapolis]").first();
			if(content != null)
			{
				Jsoup.connect(content.attr("data-mediapolis")).get();
			}
		}
		catch(IOException e)
		{
			audio = ((UnsupportedMimeTypeException)e).getUrl();
		}
		LOGGER.debug("audio: " + audio);
		return audio;
	}
}
