package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class PatchLoader extends OndarockLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "phonoteke";

	private MongoCollection<Document> docsLocal;

	public static void main(String[] args) 
	{
		new PatchLoader().missingYoutube();
	}

	public PatchLoader()
	{
		super();
		MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
		docsLocal = db.getCollection("docs");
	}

	private void missingDocs()
	{
		MongoCursor<Document> i = docsLocal.find().noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			Document docLocal = i.next();
			String id = docLocal.getString("id");
			String url = docLocal.getString("url");
			MongoCursor<Document> j = docs.find(Filters.eq("url", url)).noCursorTimeout(true).iterator();
			Document doc = j.tryNext();
			if(doc == null)
			{
				crawl(url);
				j = docs.find(Filters.eq("url", url)).noCursorTimeout(true).iterator();
				doc = j.tryNext();
			}
		}
	}

	private void missingYoutube()
	{
		MongoCursor<Document> i = docsLocal.find().noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			Document docLocal = i.next();
			String idLocal = docLocal.getString("id");
			MongoCursor<Document> j = docs.find(Filters.eq("id", idLocal)).noCursorTimeout(true).iterator();
			Document doc = j.tryNext();
			if(doc != null)
			{
				List<Document> tracksLocal = docLocal.get("tracks", List.class);
//				List<Document> tracks = doc.get("tracks", List.class);
				if(CollectionUtils.isNotEmpty(tracksLocal))
				{
					doc.append("tracks", tracksLocal);
					docs.updateOne(Filters.eq("id", idLocal), new Document("$set", doc));
					LOGGER.info(idLocal + " patched");
				}
			}
		}
	}
}
