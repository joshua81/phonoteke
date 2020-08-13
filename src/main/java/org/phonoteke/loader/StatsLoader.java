package org.phonoteke.loader;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
		TreeMap<String, TreeMap<String, Integer>> topCharts = Maps.newTreeMap();
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).noCursorTimeout(true).iterator();
		int numPodcasts = 0;
		int numTracks = 0;
		while(i.hasNext()) 
		{
			numPodcasts++;
			Document page = i.next();
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			String podcast = page.getString("artist");
			Date date = page.getDate("date");
			podcast = podcast+ "-" + new SimpleDateFormat("yyyy").format(date);
			if(!topCharts.containsKey(podcast)) {
				topCharts.put(podcast, Maps.newTreeMap());
			}

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

					String artist = track.getString("spartistid");
					if(artist != null) {
						TreeMap<String, Integer> topChart = topCharts.get(podcast);
						if(!topChart.containsKey(artist)) {
							topChart.put(artist, 0);
						}
						topChart.put(artist, topChart.get(artist)+1);
					}
				}
			}
		}

		LOGGER.info("Total Podcasts: " + numPodcasts);
		LOGGER.info("Total Podcasts Tracks: " + numTracks);
		for(Integer key : stats.descendingKeySet()) {
			int num = stats.get(key);
			double perc = Double.valueOf(num)/Double.valueOf(numTracks);
			LOGGER.info("Podcasts Tracks score " + key + ": " + num + " (" + NumberFormat.getPercentInstance().format(perc) + ")" );
		}

		LOGGER.info("Total Top Charts: " + topCharts.keySet().size());
		for(String podcast : topCharts.descendingKeySet()) {
			TreeMap<String, Integer> topChart = topCharts.get(podcast);
			Map<String, Integer> sorted = topChart.entrySet().stream()
					.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			int j = 0;
			for(String album : sorted.keySet()) {
				LOGGER.info("Podcasts Top Charts " + podcast + ": " + album + " (" + sorted.get(album) + ")");
				j++;
				if(j== 10) {
					break;
				}
			}
			LOGGER.info("--------------------");
		}
	}
}
