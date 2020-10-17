package org.phonoteke.loader;

import java.text.NumberFormat;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.google.api.client.util.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class StatsLoader implements HumanBeats
{
	private static final Logger LOGGER = LogManager.getLogger(StatsLoader.class);
	
	private MongoCollection<org.bson.Document> docs = new MongoDB().getDocs();


	@Override
	public void load(String task)
	{
		LOGGER.info("Calculating stats...");
		TreeMap<Integer, Integer> scoreStats = Maps.newTreeMap();
		//		TreeMap<String, TreeMap<String, Integer>> topCharts = Maps.newTreeMap();
		MongoCursor<Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"))).iterator();
		int numPodcasts = 0;
		int numTracks = 0;
		int numMusicBrainz = 0;
		int numMusicBrainzNA = 0;
		while(i.hasNext()) 
		{
			numPodcasts++;
			Document page = i.next();
			List<org.bson.Document> tracks = page.get("tracks", List.class);
			//			String podcast = page.getString("artist");
			//			Date date = page.getDate("date");
			//			podcast = podcast+ "-" + new SimpleDateFormat("yyyy").format(date);
			//			if(!topCharts.containsKey(podcast)) {
			//				topCharts.put(podcast, Maps.newTreeMap());
			//			}

			if(CollectionUtils.isNotEmpty(tracks))
			{
				for(org.bson.Document track : tracks)
				{
					numTracks++;
					Integer score = track.getInteger("score");
					if(score == null) {
						score = -1;
					}
					if(!scoreStats.containsKey(score)) {
						scoreStats.put(score, 0);
					}
					scoreStats.put(score, scoreStats.get(score)+1);

					//					String artistSp = track.getString("spartistid");
					//					if(artistSp != null) {
					//						TreeMap<String, Integer> topChart = topCharts.get(podcast);
					//						if(!topChart.containsKey(artistSp)) {
					//							topChart.put(artistSp, 0);
					//						}
					//						topChart.put(artistSp, topChart.get(artistSp)+1);
					//					}
					String artistMb = track.getString("artistid");
					if(artistMb != null) {
						if(!artistMb.equals(NA)) {
							numMusicBrainz++;
						}
						else {
							numMusicBrainzNA++;
						}
					}
				}

				if(tracks.size() < 5) {
					LOGGER.info(page.get("source") + ": " + page.get("id") + " (tracks " + tracks.size() + ")");
				}
			}
		}

		LOGGER.info("Podcasts Episodes: " + numPodcasts);
		LOGGER.info("Podcasts Tracks: " + numTracks);
		double perc = Double.valueOf(numMusicBrainz)/Double.valueOf(numTracks);
		LOGGER.info("Podcasts MusicBrainz: " + numMusicBrainz + " (" + NumberFormat.getPercentInstance().format(perc) + ")");
		perc = Double.valueOf(numMusicBrainzNA)/Double.valueOf(numTracks);
		LOGGER.info("Podcasts MusicBrainz NA: " + numMusicBrainzNA + " (" + NumberFormat.getPercentInstance().format(perc) + ")");
		for(Integer key : scoreStats.descendingKeySet()) {
			int num = scoreStats.get(key);
			perc = Double.valueOf(num)/Double.valueOf(numTracks);
			LOGGER.info("Podcasts Tracks score " + key + ": " + num + " (" + NumberFormat.getPercentInstance().format(perc) + ")");
		}
		//		LOGGER.info("--------------------");
		//
		//		LOGGER.info("Total Top Charts: " + topCharts.keySet().size());
		//		for(String podcast : topCharts.descendingKeySet()) {
		//			TreeMap<String, Integer> topChart = topCharts.get(podcast);
		//			Map<String, Integer> sorted = topChart.entrySet().stream()
		//					.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
		//					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		//			int j = 0;
		//			for(String album : sorted.keySet()) {
		//				LOGGER.info("Podcasts Top Charts " + podcast + ": " + album + " (" + sorted.get(album) + ")");
		//				j++;
		//				if(j== 10) {
		//					break;
		//				}
		//			}
		//			LOGGER.info("--------------------");
		//		}
	}
}
