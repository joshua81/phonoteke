package org.phonoteke.crawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public class PhonotekeCrawler extends WebCrawler
{
	protected static final Logger LOGGER = LogManager.getLogger(PhonotekeCrawler.class);

	protected static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	protected static final int NUMBER_OF_CRAWLERS = 1;

	protected static final String MONGO_HOST = "localhost";
	protected static final int MONGO_PORT = 27017;
	protected static final String MONGO_DB = "phonoteke";

	protected MongoCollection<Document> pages;

	public static void main(String[] args) throws Exception 
	{
		new OndarockCrawler().crawl();
		new MusicalboxCrawler().crawl();
	}

	public PhonotekeCrawler()
	{
		try
		{
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
			pages = db.getCollection("pages");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	protected void crawl()
	{
		try
		{
			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
			controller.addSeed(getBaseUrl());
			controller.start(PhonotekeCrawler.class, NUMBER_OF_CRAWLERS);
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error crawling Musilcabox: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return shouldVisit(url.getURL().toLowerCase());
	}

	@Override
	public void visit(Page page) 
	{
		if (page.getParseData() instanceof HtmlParseData) 
		{
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			String url = page.getWebURL().getURL();

			try
			{
				if(url.endsWith(".htm") || url.endsWith(".html"))
				{
					Document json = pages.find(Filters.and(Filters.eq("url", url))).first();
					if(json == null)
					{
						json = new Document("url", url).
								append("page", html).
								append("source", getSource());
						pages.insertOne(json);
						LOGGER.info("Page " + url + " added");
					}
				}
			} 
			catch (Throwable t) 
			{
				LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
				throw new RuntimeException(t);
			}
		}
	}

	//---------------------------------
	// Methods to be overridden
	//---------------------------------
	protected String getBaseUrl()
	{
		return null;
	}

	protected Boolean shouldVisit(String url)
	{
		return null;
	}

	protected String getSource() 
	{
		return null;
	}
}
