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
	private static MongoCollection<org.bson.Document> docs;
	private static MongoCollection<org.bson.Document> shows;
	private static MongoCollection<org.bson.Document> authors;

	private MongoDB() {
		// private constructor
	}

	private static void init() {
		if(docs == null) {
			try
			{
				Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
				root.setLevel(Level.ERROR);

				MongoClientURI uri = new MongoClientURI(System.getenv("MONGO_URL"));
				MongoDatabase db = new MongoClient(uri).getDatabase(System.getenv("MONGO_DB"));
				docs = db.getCollection("docs");
				shows = db.getCollection("shows");
				authors = db.getCollection("authors");
			} 
			catch (Throwable t) 
			{
				throw new RuntimeException(t);
			}
		}
	}

	public static MongoCollection<org.bson.Document> getDocs()
	{
		init();
		return docs;
	}

	public static MongoCollection<org.bson.Document> getShows()
	{
		init();
		return shows;
	}

	public static MongoCollection<org.bson.Document> getAuthors()
	{
		init();
		return authors;
	}
}
