package org.phonoteke.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.phonoteke.model.ModelUtils;

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
					String youtube = null;
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
					String youtube = null;
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

	protected TYPE getType(String url) 
	{
		return TYPE.ALBUM;
	}
}
