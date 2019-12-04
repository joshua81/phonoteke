package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader extends OndarockLoader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	public static void main(String[] args) 
	{
		new PatchLoader().radio2Authors();
	}

	public PatchLoader()
	{
		super();
	}

	private void radio2Authors()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", "babylon")).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			doc.append("authors", Lists.newArrayList("Carlo Pastore"));
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
			LOGGER.info(id + " Babylon authors added");
		}

		i = docs.find(Filters.eq("source", "musicalbox")).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			doc.append("authors", Lists.newArrayList("Raffaele Costantino"));
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
			LOGGER.info(id + " Musicalbox authors added");
		}
	}

	//	private void patchBabylonTracks()
	//	{
	//		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", "babylon")).noCursorTimeout(true).iterator();
	//		while(i.hasNext())
	//		{
	//			org.bson.Document doc = i.next();
	//			String id = doc.getString("id");
	//
	//			doc.append("artistid", null).append("albumid", null);
	//			List<org.bson.Document> tracks = doc.get("tracks", List.class);
	//			if(CollectionUtils.isNotEmpty(tracks))
	//			{
	//				for(org.bson.Document track : tracks)
	//				{
	//					track.append("artistid", null).append("albumid", null);
	//				}
	//			}
	//			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
	//			LOGGER.info(id + " tracks patched");
	//		}
	//	}

	//	private void blankReview()
	//	{
	//		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", OndarockLoader.SOURCE)).noCursorTimeout(true).iterator();
	//		while(i.hasNext())
	//		{
	//			org.bson.Document doc = i.next();
	//			String id = doc.getString("id");
	//			String review = doc.getString("review");
	//			if(StringUtils.isBlank(review) || review.trim().length() < 100)
	//			{
	//				docs.deleteOne(Filters.eq("id", id));
	//				LOGGER.info(id + " is blank");
	//			}
	//		}
	//	}

	//	private void findLongTracks()
	//	{
	//		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", MusicalboxLoader.SOURCE)).noCursorTimeout(true).iterator();
	//		while(i.hasNext())
	//		{
	//			org.bson.Document doc = i.next();
	//			String id = doc.getString("id");
	//			List<org.bson.Document> tracks = doc.get("tracks", List.class);
	//			if(CollectionUtils.isNotEmpty(tracks))
	//			{
	//				for(org.bson.Document track : tracks)
	//				{
	//					String title = track.getString("title");
	//					if(title.trim().length() > 200)
	//					{
	//						LOGGER.info(id + ": track " + title);
	//					}
	//				}
	//			}
	//		}
	//	}

	//	private void patchTracks()
	//	{
	//		MongoCursor<org.bson.Document> i = docs.find(Filters.eq("source", MusicalboxLoader.SOURCE)).noCursorTimeout(true).iterator();
	//		while(i.hasNext())
	//		{
	//			org.bson.Document doc = i.next();
	//			String id = doc.getString("id");
	//			List<org.bson.Document> tracks = doc.get("tracks", List.class);
	//			if(CollectionUtils.isNotEmpty(tracks))
	//			{
	//				List<org.bson.Document> toremove = Lists.newArrayList();
	//				for(org.bson.Document track : tracks)
	//				{
	//					String title = track.getString("title");
	//					if(StringUtils.isNotBlank(title))
	//					{
	//						for(String error : MusicalboxLoader.ERRORS)
	//						{
	//							if(title.startsWith(error))
	//							{
	//								title = title.substring(error.length()).trim();
	//								track.append("title", title);
	//								break;
	//							}
	//						}
	//					}
	//					if(StringUtils.isBlank(title))
	//					{
	//						toremove.add(track);
	//					}
	//				}
	//				tracks.removeAll(toremove);
	//			}
	//
	//			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
	//			LOGGER.info(id + " tracks patched");
	//		}
	//	}

	//	private void resetMBIds()
	//	{
	//		MongoCursor<org.bson.Document> i = docs.find().noCursorTimeout(true).iterator();
	//		while(i.hasNext())
	//		{
	//			org.bson.Document doc = i.next();
	//			String id = doc.getString("id");
	//			if(UNKNOWN.equals(doc.getString("artistid")))
	//			{
	//				doc.append("artistid", null);
	//			}
	//			if(UNKNOWN.equals(doc.getString("albumid")))
	//			{
	//				doc.append("albumid", null);
	//			}
	//			List<org.bson.Document> tracks = (List<org.bson.Document>)doc.get("tracks", List.class);
	//			if(CollectionUtils.isNotEmpty(tracks))
	//			{
	//				for(org.bson.Document track : tracks)
	//				{
	//					if(UNKNOWN.equals(track.getString("artistid")))
	//					{
	//						track.append("artistid", null);
	//					}
	//					if(UNKNOWN.equals(track.getString("albumid")))
	//					{
	//						track.append("albumid", null);
	//					}
	//				}
	//			}
	//			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
	//			LOGGER.info(id + " MB ids reset");
	//		}
	//	}
}
