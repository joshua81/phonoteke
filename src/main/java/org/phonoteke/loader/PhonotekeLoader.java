package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PhonotekeLoader 
{
	protected static final Logger LOGGER = LogManager.getLogger(PhonotekeLoader.class);

	protected static final String MONGO_HOST = "localhost";
	protected static final int MONGO_PORT = 27017;
	protected static final String MONGO_DB = "phonoteke";

	protected MongoCollection<org.bson.Document> pages;
	protected MongoCollection<org.bson.Document> albums;
	protected MongoCollection<org.bson.Document> artists;
	
	public static void main(String[] args) 
	{
		new OndarockLoader().load();
		new MusicalboxLoader().load();
	}
	
	public PhonotekeLoader()
	{
		try
		{
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
			pages = db.getCollection("pages");
			albums = db.getCollection("albums");
			artists = db.getCollection("artists");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}
}
