package org.phonoteke;

import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

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
	public static final String ONDAROCK_URL = "http://www.ondarock.it/";
	
	private static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	private static final int NUMBER_OF_CRAWLERS = 1;
	
	private static final Logger LOGGER = LogManager.getLogger(OndarockCrawler.class);
	private static final Pattern FILTERS = Pattern.compile(".*(\\.(htm|html))$");

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "phonoteke";

	private DBCollection pages;
	private DBCollection seq;
	
	
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
			DB db = new MongoClient(MONGO_HOST, MONGO_PORT).getDB(MONGO_DB);
			pages = db.getCollection("pages");
			seq = db.getCollection("sequences");
			DBObject pageId = seq.findOne("pageId");
			if(pageId == null)
			{
				seq.insert(BasicDBObjectBuilder.start().
						add("_id", "pageId").
						add("value", 1).get());
			}
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
				DBObject pageDB = pages.findOne(BasicDBObjectBuilder.start().add("url", url).get());
				if(pageDB == null)
				{
					DBObject json = BasicDBObjectBuilder.start().
							add("_id", nextVal()).
							add("url", url).
							add("page", html).get();
					pages.insert(json);
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

	private Number nextVal()
	{
		DBObject o = seq.findAndModify(BasicDBObjectBuilder.start().add("_id", "pageId").get(), 
				BasicDBObjectBuilder.start().add("$inc", BasicDBObjectBuilder.start().
						add("value", 1).get()).get());
		return (Number)o.get("value");
	}
}