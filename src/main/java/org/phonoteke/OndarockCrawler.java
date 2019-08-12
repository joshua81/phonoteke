package org.phonoteke;

import java.util.regex.Pattern;

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

public class OndarockCrawler extends WebCrawler
{
	public static final String ONDAROCK_URL = "https://www.ondarock.it/";
	
	private static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	private static final int NUMBER_OF_CRAWLERS = 1;
	
	private static final Logger LOGGER = LogManager.getLogger(OndarockCrawler.class);
	private static final Pattern FILTERS = Pattern.compile(".*(\\.(htm|html))$");

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "phonoteke";

	private MongoCollection<Document> pages;
	
	
	public static void main(String[] args) throws Exception 
	{
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		controller.addSeed(ONDAROCK_URL);
		controller.start(OndarockCrawler.class, NUMBER_OF_CRAWLERS);
	}

	public OndarockCrawler()
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


	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String dest = url.getURL().toLowerCase();
		return FILTERS.matcher(dest).matches() && dest.startsWith(ONDAROCK_URL);
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
					json = new Document("url", url).append("page", html);
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