package org.phonoteke.crawler;

public class OndarockCrawler extends PhonotekeCrawler
{
	private static final String URL = "https://www.ondarock.it/";
	private static final String SOURCE = "ondarock";


	public OndarockCrawler()
	{
		super();
	}
	
	protected String getBaseUrl()
	{
		return URL;
	}
	
	protected Boolean shouldVisit(String url) 
	{
		return url.startsWith(URL);
	}
	
	protected String getSource() 
	{
		return SOURCE;
	}
}