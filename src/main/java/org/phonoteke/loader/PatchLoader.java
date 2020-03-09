package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		//		LOGGER.info("Loading patch...");
		//		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.podcast.name()))).noCursorTimeout(true).iterator(); 
		//		while(i.hasNext()) 
		//		{ 
		//			Document page = i.next();
		//			String id = page.getString("id");
		//			page.append("spalbumid", null);
		//			page.append("spartistid", null);
		//			page.append("spcover-l", null);
		//			page.append("spcover-m", null);
		//			page.append("spcover-s", null);
		//			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
		//			LOGGER.info("Updated Podcast " + id);
		//		}
	}
}
