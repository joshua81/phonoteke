package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	public static void main(String[] args)
	{
		new PatchLoader().patch();
	}

	public PatchLoader()
	{
		super();
	}

	private void patch()
	{
		LOGGER.info("Loading patch...");
		MongoCursor<Document> i = docs.find().noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			page.remove("spcover-l");
			page.remove("spcover-m");
			page.remove("spcover-s");
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Documenti " + id + " updated");
		}
	}
}
