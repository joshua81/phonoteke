package org.phonoteke.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class MusicalboxCrawler extends PhonotekeCrawler
{
	private static final String URL1 = "https://www.raiplayradio.it/programmi/musicalbox/";
	private static final String URL2 = "https://www.raiplayradio.it/audio";
	private static final String SOURCE = "musicalbox";


	public MusicalboxCrawler()
	{
		super();
	}
	
	public static String getBaseUrl()
	{
		return URL1;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return url.getURL().toLowerCase().startsWith(URL1) || url.getURL().toLowerCase().startsWith(URL2);
	}
	
	@Override
	protected String getSource() 
	{
		return SOURCE;
	}
}