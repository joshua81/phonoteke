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
		LOGGER.info("Loading patch...");
//		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.album.name()))).noCursorTimeout(true).iterator(); 
//		while(i.hasNext()) 
//		{ 
//			Document page = i.next();
//			String id = page.getString("id");
//			List<org.bson.Document> tracks = (List<org.bson.Document>)page.get("tracks", List.class);
//			if(CollectionUtils.isNotEmpty(tracks))
//			{
//				for(org.bson.Document track : tracks)
//				{
//					track.remove("artistid");
//					track.remove("albumid");
//					track.remove("sptrackid");
//					track.remove("spcover-l");
//					track.remove("spcover-m");
//					track.remove("spcover-s");
//				}
//				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
//				LOGGER.info("Updated Album tracks " + id);
//			}
//		}
	}
}
