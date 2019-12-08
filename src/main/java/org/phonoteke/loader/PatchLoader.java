package org.phonoteke.loader;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.util.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class PatchLoader extends Radio2Loader
{
	private static final Logger LOGGER = LogManager.getLogger(PatchLoader.class);

	public static void main(String[] args) 
	{
		new PatchLoader().matchingTracks();
	}

	public PatchLoader()
	{
		super();
	}

	private void matchingTracks()
	{
		MongoCursor<org.bson.Document> i = docs.find(Filters.not(Filters.eq("source", "ondarock"))).noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			List<org.bson.Document> tracks = doc.get("tracks", List.class);
			List<org.bson.Document> toremove = Lists.newArrayList();
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					String title = track.getString("title");
					if(!isTrack(title))
					{
						//						LOGGER.info(id + ": " + title);
						toremove.add(track);
					}
				}
				tracks.removeAll(toremove);
			}
			if(CollectionUtils.isEmpty(tracks))
			{
//				docs.deleteOne(Filters.eq("id", id));
				LOGGER.info(id + ": deleted");
			}
			else if(CollectionUtils.isNotEmpty(toremove))
			{
//				doc.append("tracks", tracks);
//				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
				LOGGER.info(id + ": tracks removed " + toremove.size());
			}
		}
	}

	private void unknown()
	{
		MongoCursor<org.bson.Document> i = docs.find().noCursorTimeout(true).iterator();
		while(i.hasNext())
		{
			org.bson.Document doc = i.next();
			String id = doc.getString("id");
			String albumid = doc.getString("albumid");
			String artistid = doc.getString("artistid");
			String spalbumid = doc.getString("spalbumid");
			String spartistid = doc.getString("spartistid");
			if("UNKNOWN".equals(albumid) || "UNKNOWN".equals(artistid))
			{
				LOGGER.info(id + ": UNKNOWN albumid or artistid");
				doc.append("albumid", null);
				doc.append("artistid", null);
			}
			if("UNKNOWN".equals(spalbumid) || "UNKNOWN".equals(spartistid))
			{
				LOGGER.info(id + ": UNKNOWN spalbumid or spartistid");
				doc.append("spalbumid", null);
				doc.append("spartistid", null);
			}
			List<org.bson.Document> tracks = doc.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					albumid = track.getString("albumid");
					artistid = track.getString("artistid");
					track.append("spalbumid", null);
					track.append("spartistid", null);
					track.remove("album");
					track.remove("artist");
					if("UNKNOWN".equals(albumid) || "UNKNOWN".equals(artistid))
					{
						LOGGER.info(id + ": UNKNOWN track albumid or artistid");
						track.append("albumid", null);
						track.append("artistid", null);
					}
				}
			}
			docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", doc));
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
