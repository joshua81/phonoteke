package org.phonoteke.loader;

import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class MongoDB 
{
	private MongoCollection<org.bson.Document> docs;

	public MongoDB() {
		try
		{
			Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.ERROR);
			
			MongoClientURI uri = new MongoClientURI(System.getenv("MONGO_URL"));
			MongoDatabase db = new MongoClient(uri).getDatabase(System.getenv("MONGO_DB"));
			docs = db.getCollection("docs");
		} 
		catch (Throwable t) 
		{
			throw new RuntimeException(t);
		}
	}

	public MongoCollection<org.bson.Document> getDocs()
	{
		return docs;
	}
}
