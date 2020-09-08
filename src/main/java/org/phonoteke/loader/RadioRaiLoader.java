package org.phonoteke.loader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class RadioRaiLoader extends AbstractCrawler
{
	private static final Logger LOGGER = LogManager.getLogger(RadioRaiLoader.class);

	private static final String URL = "https://www.raiplayradio.it/";
	private static final String URL_AUDIO = "https://www.raiplayradio.it/audio";
	private static final String BABYLON = "https://www.raiplayradio.it/programmi/babylon/";
	private static final String MUSICALBOX = "https://www.raiplayradio.it/programmi/musicalbox/";
	private static final String INTHEMIX = "https://www.raiplayradio.it/programmi/radio2inthemix/";
	private static final String BATTITI = "https://www.raiplayradio.it/programmi/battiti/";
	private static final String SEIGRADI = "https://www.raiplayradio.it/programmi/seigradi/";
	private static final String STEREONOTTE = "https://www.raiplayradio.it/programmi/stereonotte/";
	private static final List<String> URLS = Lists.newArrayList(	BABYLON, MUSICALBOX, INTHEMIX, BATTITI, SEIGRADI, STEREONOTTE, URL_AUDIO);

	private static String artist;
	private static String source;


	public void load(String task) 
	{
		if(task == null) {
			load("musicalbox");
			load("battiti");
			load("seigradi");
			load("stereonotte");
		}

		if("babylon".equals(task))
		{
			RadioRaiLoader.artist = "Babylon";
			RadioRaiLoader.source = task;
			crawl(BABYLON);
		}
		else if("musicalbox".equals(task))
		{
			RadioRaiLoader.artist = "Musicalbox";
			RadioRaiLoader.source = task;
			crawl(MUSICALBOX);
		}
		else if("inthemix".equals(task))
		{
			RadioRaiLoader.artist = "Inthemix";
			RadioRaiLoader.source = task;
			crawl(INTHEMIX);
		}
		else if("battiti".equals(task))
		{
			RadioRaiLoader.artist = "Battiti";
			RadioRaiLoader.source = task;
			crawl(BATTITI);
		}
		else if("seigradi".equals(task))
		{
			RadioRaiLoader.artist = "Sei Gradi";
			RadioRaiLoader.source = task;
			crawl(SEIGRADI);
		}
		else if("stereonotte".equals(task))
		{
			RadioRaiLoader.artist = "Stereonotte";
			RadioRaiLoader.source = task;
			crawl(STEREONOTTE);
		}
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
		if("seigradi".equals(source))
		{
			return getTitle(url, doc);
		}
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
			content.select("br").after(TRACKS_NEW_LINE);
			content.select("p").after(TRACKS_NEW_LINE);
			content.select("li").after(TRACKS_NEW_LINE);
			content.select("h1").after(TRACKS_NEW_LINE);
			content.select("h2").after(TRACKS_NEW_LINE);
			content.select("h3").after(TRACKS_NEW_LINE);
			content.select("div").after(TRACKS_NEW_LINE);

			String[] chunks = content.text().replace("||", TRACKS_NEW_LINE).split(TRACKS_NEW_LINE);
			if("seigradi".equals(source))
			{
				String str = content.text().replace(TRACKS_NEW_LINE + " "+ TRACKS_NEW_LINE, "||").replace(TRACKS_NEW_LINE, " - ");
				chunks = str.replace("||", TRACKS_NEW_LINE).split(TRACKS_NEW_LINE);
			}
			for(int i = 0; i < chunks.length; i++)
			{
				String title = chunks[i].trim();
				if(StringUtils.isNotBlank(title))
				{
					for(String p : TRACKS_TRIM)
					{
						if(title.toUpperCase().startsWith(p))
						{
							title = title.substring(p.length()).trim();
						}
					}
				}
				if(StringUtils.isNotBlank(title) && HumanBeats.isTrack(title))
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
		return RadioRaiLoader.source.equals("babylon") ? Lists.newArrayList("Carlo Pastore") : 
			RadioRaiLoader.source.equals("musicalbox") ? Lists.newArrayList("Raffaele Costantino") : 
				RadioRaiLoader.source.equals("inthemix") ? Lists.newArrayList("Lele Sacchi") : 
					RadioRaiLoader.source.equals("battiti") ? Lists.newArrayList("Nicola Catalano", "Ghighi Di Paola", "Giovanna Scandale", "Antonia Tessitore") : 
						RadioRaiLoader.source.equals("seigradi") ? Lists.newArrayList("Luca Damiani") :
							RadioRaiLoader.source.equals("stereonotte") ? Lists.newArrayList("Francesco Adinolfi", "Max De Tomassi", "Lele Sacchi", "Luca Sapio", "Mauro Zanda") : null;
	}
}
