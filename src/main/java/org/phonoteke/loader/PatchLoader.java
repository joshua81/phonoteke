package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader extends Radio2Loader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	public static void main(String[] args) 
	{
		new PatchLoader().changeType();
	}

	public PatchLoader()
	{
		super();
	}

	private void changeType()
	{
		MongoCursor<Document> i = docs.find(Filters.or(Filters.eq("source", "babylon"), Filters.eq("source", "musicalbox"), Filters.eq("source", "inthemix"))).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			Document doc = i.next();
			String id = doc.getString("id");
			String source = doc.getString("source");
			doc.append("type", TYPE.podcast.name());
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
			LOGGER.info("Doc " + source + " updated");
		}
	}
}
