package org.phonoteke.crawler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.uci.ics.crawler4j.crawler.WebCrawler;

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
		//new OndarockCrawler().crawl();
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
}
