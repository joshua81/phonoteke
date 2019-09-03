package org.phonoteke.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class OndarockCrawler extends PhonotekeCrawler
{
	private static final String URL = "https://www.ondarock.it/";
	private static final String SOURCE = "ondarock";


	public OndarockCrawler()
	{
		super();
	}
	
	public static String getBaseUrl()
	{
		return URL;
	}
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return url.getURL().toLowerCase().startsWith(URL);
	}
	
	@Override
	protected String getSource() 
	{
		return SOURCE;
	}
}