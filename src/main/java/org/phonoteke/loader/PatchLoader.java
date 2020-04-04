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
		new PatchLoader().replaceSpecialChars();
	}

	public PatchLoader()
	{
		super();
	}
	
	private void replaceSpecialChars()
	{
		LOGGER.info("Loading patch...");
		MongoCursor<Document> i = docs.find(Filters.or(Filters.regex("title", ".*&amp;.*"), Filters.regex("artist", ".*&amp;.*"))).
				noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			String title = page.getString("title");
			page.append("title", title.replaceAll("&amp;", "&"));
			String artist = page.getString("artist");
			page.append("artist", artist.replaceAll("&amp;", "&"));
			page.append("spartistid", null);
			page.append("spalbumid", null);
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Documenti " + id + " updated");
		}
	}
}
