package org.phonoteke;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class PhonotekeCrawler 
{
	private static final String CRAWL_STORAGE_FOLDER = "target/data/phonoteke";
	private static final int NUMBER_OF_CRAWLERS = 1;
	
	public static void main(String[] args) throws Exception 
	{
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		controller.addSeed(OndarockCrawler.ONDAROCK_URL);
		controller.start(OndarockCrawler.class, NUMBER_OF_CRAWLERS);
	}
}
