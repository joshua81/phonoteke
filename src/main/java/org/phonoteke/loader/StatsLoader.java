package org.phonoteke.loader;

import java.text.NumberFormat;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.api.client.util.Maps;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class StatsLoader extends PhonotekeLoader
{
	private static final Logger LOGGER = LogManager.getLogger(StatsLoader.class);

	public static void main(String[] args)
	{
		new StatsLoader().calculateStats();
	}

	public StatsLoader()
	{
		super();
	}

	private void calculateStats()
	{
		LOGGER.info("Calculating stats...");
		TreeMap<Integer, Integer> stats = Maps.newTreeMap();
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).noCursorTimeout(true).iterator();
		int numPodcasts = 0;
		int numTracks = 0;
		while(i.hasNext()) 
		{
			numPodcasts++;
			Document page = i.next();
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					numTracks++;
					Integer score = track.getInteger("score");
					if(score == null) {
						score = -1;
					}
					if(!stats.containsKey(score)) {
						stats.put(score, 0);
					}
					stats.put(score, stats.get(score)+1);
				}
			}
		}
		
		LOGGER.info("Stats total Podcasts: " + numPodcasts);
		LOGGER.info("Stats total Tracks: " + numTracks);
		for(Integer key : stats.descendingKeySet()) {
			int num = stats.get(key);
			double perc = Double.valueOf(num)/Double.valueOf(numTracks);
			LOGGER.info("Stats Tracks score " + key + ": " + num + " (" + NumberFormat.getPercentInstance().format(perc) + ")" );
		}
	}
}
