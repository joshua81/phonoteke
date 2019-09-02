package org.phonoteke.crawler;

public class MusicalboxCrawler extends PhonotekeCrawler
{
	private static final String URL1 = "https://www.raiplayradio.it/programmi/musicalbox/";
	private static final String URL2 = "https://www.raiplayradio.it/audio";
	private static final String SOURCE = "musicalbox";


	public MusicalboxCrawler()
	{
		super();
	}
	
	protected String getBaseUrl()
	{
		return URL1;
	}

	protected Boolean shouldVisit(String url) 
	{
		return url.startsWith(URL1) || url.startsWith(URL2);
	}
	
	protected String getSource() 
	{
		return SOURCE;
	}
}