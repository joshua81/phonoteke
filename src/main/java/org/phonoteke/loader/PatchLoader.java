package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader  extends MusicalboxLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);
	
	public static void main(String[] args) 
	{
		new PatchLoader().resetMBIds();
	}
	
	public PatchLoader()
	{
		super();
	}
	
	public void resetMBIds()
	{
		MongoCursor<org.bson.Document> i = docs.find().noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			doc.append("artistid", null);
			doc.append("albumid", null);
			List<org.bson.Document> tracks = (List<org.bson.Document>)doc.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					track.append("artistid", null);
					track.append("albumid", null);
				}
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
			LOGGER.info(id + " MB ids reset");
		}
	}
}
