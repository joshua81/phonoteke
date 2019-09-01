package org.phonoteke.crawler;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public class OndarockCrawler extends PhonotekeCrawler
{
	public static final String URL = "https://www.ondarock.it/";
	
//	private static final Pattern FILTERS = Pattern.compile(".*(\\.(htm|html))$");

	private MongoCollection<Document> pages;
	

	public OndarockCrawler()
	{
		super();
	}
	
	public void crawl()
	{
		try
		{
			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			
			CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
			controller.addSeed(URL);
			controller.start(OndarockCrawler.class, NUMBER_OF_CRAWLERS);
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error crawling Musilcabox: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String dest = url.getURL().toLowerCase();
		return dest.startsWith(URL);
	}

	@Override
	public void visit(Page page) {
		if (page.getParseData() instanceof HtmlParseData) 
		{
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			String url = page.getWebURL().getURL();

			try
			{
				Document json = pages.find(Filters.and(Filters.eq("url", url))).first();
				if(json == null)
				{
					json = new Document("url", url).
							append("page", html).
							append("source", "ondarock");
					pages.insertOne(json);
					LOGGER.info("Page " + url + " added");
				}
			} 
			catch (Throwable t) 
			{
				LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
				throw new RuntimeException(t);
			}
		}
	}
}