package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDB 
{
	private static final Logger LOGGER = LogManager.getLogger(MongoDB.class);

	private MongoCollection<org.bson.Document> docs;

	public MongoDB() {
		try
		{
			MongoClientURI uri = new MongoClientURI(System.getenv("MONGO_URL"));
			MongoDatabase db = new MongoClient(uri).getDatabase(System.getenv("MONGO_DB"));
			docs = db.getCollection("docs");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("ERROR connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	public MongoCollection<org.bson.Document> getDocs()
	{
		return docs;
	}
}
