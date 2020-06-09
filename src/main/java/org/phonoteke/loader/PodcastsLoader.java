package org.phonoteke.loader;

import java.util.List;

import org.bson.Document;

import com.google.common.collect.Lists;


public class PodcastsLoader extends PhonotekeLoader
{
	public static void main(String[] args) {
		new PodcastsLoader().load("babylon", "https://www.raiplayradio.it/programmi/babylon/", "Babylon", null, Lists.newArrayList("Carlo Pastore"));
		new PodcastsLoader().load("battiti", "https://www.raiplayradio.it/programmi/battiti/", "Battiti", null, null);
		new PodcastsLoader().load("casabertallot", "https://www.spreaker.com/show/casa_bertallot", "Casa Bertallot", null, Lists.newArrayList("Alessio Bertallot"));
		new PodcastsLoader().load("inthemix", "https://www.raiplayradio.it/programmi/radio2inthemix/", "In The Mix", null, Lists.newArrayList("Lele Sacchi"));
		new PodcastsLoader().load("musicalbox", "https://www.raiplayradio.it/programmi/musicalbox/", "Musicalbox", null, Lists.newArrayList("Raffaele Costantino"));
		new PodcastsLoader().load("rolloverhangover", "https://www.spreaker.com/show/rollover-hangover", "Rollover Hangover", null, Lists.newArrayList("Alessio Bertallot"));
		new PodcastsLoader().load("seigradi", "https://www.raiplayradio.it/programmi/seigradi/", "Sei Gradi", null, null);
	}

	public PodcastsLoader() {
		super();
	}

	private void load(String source, String url, String title, String desc, List<String> authors) {
		Document json = new Document("source", source).
				append("url", getUrl(url)).
				append("title", title).
				append("description", desc).
				append("authors", authors).
				append("cover", getUrl("images/" + source + ".png"));
		podcasts.insertOne(json);
		LOGGER.info(json.getString("type") + " " + url + " added");
	}
}
