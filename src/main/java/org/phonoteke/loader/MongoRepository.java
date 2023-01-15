package org.phonoteke.loader;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.Getter;
import lombok.SneakyThrows;

@Component
public class MongoRepository {

	@Autowired 
	private MongoClient client;

	@Getter
	private MongoCollection<Document> shows;
	@Getter
	private MongoCollection<Document> docs;
	@Getter
	private MongoCollection<Document> stats;
	@Getter
	private MongoCollection<Document> authors;

	@PostConstruct
	@SneakyThrows
	public void init() {
		Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.ERROR);

		MongoDatabase db = client.getDatabase("mbeats");
		docs = db.getCollection("docs");
		shows = db.getCollection("shows");
		authors = db.getCollection("authors");
	}
}
