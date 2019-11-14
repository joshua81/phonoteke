package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.util.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader extends OndarockLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);
	
	public static void main(String[] args) 
	{
//		new PatchLoader().resetArtistAndTitle();
	}
	
	public PatchLoader()
	{
		super();
	}
	
	private void blankReview()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", OndarockLoader.SOURCE)).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			String review = doc.getString("review");
			if(StringUtils.isBlank(review) || review.trim().length() < 100)
			{
				docs.deleteOne(Filters.eq("id", id));
				LOGGER.info(id + " is blank");
			}
		}
	}
	
	private void findLongTracks()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", MusicalboxLoader.SOURCE)).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			List<org.bson.Document> tracks = doc.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					String title = track.getString("title");
					if(title.trim().length() > 200)
					{
						LOGGER.info(id + ": track " + title);
					}
				}
			}
		}
	}
	
	private void patchTracks()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", MusicalboxLoader.SOURCE)).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			List<org.bson.Document> tracks = doc.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				List<org.bson.Document> toremove = Lists.newArrayList();
				for(org.bson.Document track : tracks)
				{
					String title = track.getString("title");
					if(StringUtils.isNotBlank(title))
					{
						for(String error : MusicalboxLoader.ERRORS)
						{
							if(title.startsWith(error))
							{
								title = title.substring(error.length()).trim();
								track.append("title", title);
								break;
							}
						}
					}
					if(StringUtils.isBlank(title))
					{
						toremove.add(track);
					}
				}
				tracks.removeAll(toremove);
			}
			
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
			LOGGER.info(id + " tracks patched");
		}
	}
	
//	private void resetArtistAndTitle()
//	{
//		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("url", "https://www.ondarock.it/recensioni/2016_trentemoller_fixion.htm")).noCursorTimeout(true).iterator();
//		while(i.hasNext())
//		{
//			org.bson.Document doc = i.next();
//			String id = doc.getString("id");
//			String url = doc.getString("url");
//			
//			org.bson.Document page = pages.find(Filters.eq("url", url)).noCursorTimeout(true).iterator().tryNext();
//			Document html = Jsoup.parse(page.getString("page"));
//			doc.append("artist", getArtist(url, html));
//			doc.append("title", getTitle(url, html));
////			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
//			LOGGER.info(id + " Artist and Title set");
//		}
//	}
	
	private void resetMBIds()
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
