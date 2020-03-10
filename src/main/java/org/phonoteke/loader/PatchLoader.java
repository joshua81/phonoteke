package org.phonoteke.loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.wrapper.spotify.model_objects.specification.Album;
import com.wrapper.spotify.model_objects.specification.Artist;

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
		SpotifyLoader loader = new SpotifyLoader();
		MongoCursor<Document> i = docs.find(Filters.and(
				Filters.eq("type", TYPE.album.name()), 
				Filters.ne("spalbumid", null), 
				Filters.exists("coverL", false))).noCursorTimeout(true).iterator(); 
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id");
			String spalbumid = page.getString("spalbumid"); 
			Album spotify = loader.getAlbum(spalbumid);
			if(spotify != null)
			{
				loader.getImages(page, spotify.getImages());
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
				LOGGER.info("Album " + spalbumid + " updated");
			}
		}

		i = docs.find(Filters.and(Filters.and(
				Filters.ne("type", TYPE.album.name()), 
				Filters.ne("type", TYPE.podcast.name())), 
				Filters.ne("spartistid", null),
				Filters.exists("coverL", false))).noCursorTimeout(true).iterator(); 
		while(i.hasNext()) 
		{ 
			Document page = i.next();
			String id = page.getString("id"); 
			String spartistid = page.getString("spartistid"); 
			Artist spotify = loader.getArtist(spartistid);
			if(spotify != null)
			{
				loader.getImages(page, spotify.getImages());
				docs.updateOne(Filters.eq("id", id), new org.bson.Document("$set", page));
				LOGGER.info("Artist " + spartistid + " updated");
			}
		}
	}
}
