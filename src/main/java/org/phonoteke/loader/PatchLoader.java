package org.phonoteke.loader;

import java.util.List;

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
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("id", "7a09fd3f3c5cd2ed3e870ff997dacdbc225285565a764d3f033513281bf6f6a3"))).
				noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks"))
			{
				if("Fade (Into View) - MT. LOW".equals(track.getString("title")))
					track.append("youtube", "UkvPvOmgLhE");
				else if("Solo - Jay Daniel".equals(track.getString("title")))
					track.append("youtube", "WkIQUJombXg");
				else if("Attitude (feat. Naomi Daniel) - Carl Craig".equals(track.getString("title")))
					track.append("youtube", "O3lUJDrDjCI");
				else if("Televised Green Smoke - Carl Craig".equals(track.getString("title")))
					track.append("youtube", "WTxWY-0NEWg");
				else if("Southern Dub (feat. Domenico Candellori) - Clap! Clap!".equals(track.getString("title")))
					track.append("youtube", "EPkmwWvM_W0");
				else if("Kinshasa's music warriors - Fulu Miziki".equals(track.getString("title")))
					track.append("youtube", "Ri2oK4gApMU");
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Documenti " + id + " updated");
		}
	}
	
	private void patch1()
	{
		LOGGER.info("Loading patch...");
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", TYPE.podcast.name()), Filters.ne("tracks.youtube", null))).
				noCursorTimeout(true).iterator();
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			for(org.bson.Document track : (List<org.bson.Document>)page.get("tracks"))
			{
				track.remove("youtube");
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
			LOGGER.info("Documenti " + id + " updated");
		}
	}
}
