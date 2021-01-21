package org.phonoteke.loader;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class PodcastLoader implements HumanBeats 
{
	private MongoCollection<org.bson.Document> shows = new MongoDB().getShows();

	public static void main(String[] args) {
		new PodcastLoader().load();
	}

	public void initShows() {
//		// BBC
//		initShow("bbc", "https://www.bbc.co.uk/programmes/b01fm4ss/episodes/guide", 
//				"Gilles Peterson at Radio6", "bbcradio6gillespeterson", Lists.newArrayList("Gilles Peterson"), 
//				Lists.newArrayList());
//
//		initShow("bbc", "https://www.bbc.co.uk/programmes/m000r6g5/episodes/guide", 
//				"Tearjerker with Jorja Smith", "bbcradio3jorjasmith", Lists.newArrayList("Jorja Smith"), 
//				Lists.newArrayList());
//
//		initShow("bbc", "https://www.bbc.co.uk/programmes/p093cs9d/episodes/guide", 
//				"Arlo Parks at Radio6", "bbcradio6arloparks", Lists.newArrayList("Arlo Parks"), 
//				Lists.newArrayList());
//
//		initShow("bbc", "https://www.bbc.co.uk/programmes/p08w4x7g/episodes/guide", 
//				"Loyle Carner at Radio6", "bbcradio6loylecarner", Lists.newArrayList("Loyle Carner"), 
//				Lists.newArrayList());
//
//		// RAI
//		initShow("rai", "https://www.raiplayradio.it/programmi/musicalbox/", 
//				"Musicalbox", "musicalbox", Lists.newArrayList("Raffaele Costantino"), 
//				Lists.newArrayList("@rairadio2", "@raffacostantino", "@_musicalbox", "#musicalboxradio2"));
//
//		initShow("rai", "https://www.raiplayradio.it/programmi/babylon/", 
//				"Babylon", "babylon", Lists.newArrayList("Carlo Pastore"), 
//				Lists.newArrayList("@rairadio2", "@carlopastore", "#babylonradio2"));
//
//		initShow("rai", "https://www.raiplayradio.it/programmi/radio2inthemix/", 
//				"Inthemix", "inthemix", Lists.newArrayList("Lele Sacchi"), 
//				Lists.newArrayList("@rairadio2", "@djlelesacchi", "#inthemixradio2"));
//
//		initShow("rai", "https://www.raiplayradio.it/programmi/battiti/", 
//				"Battiti", "battiti", Lists.newArrayList("Nicola Catalano", "Ghighi Di Paola", "Giovanna Scandale", "Antonia Tessitore"), 
//				Lists.newArrayList("@radio3tweet", "#battitiradio3"));
//
//		initShow("rai", "https://www.raiplayradio.it/programmi/seigradi/", 
//				"Sei Gradi", "seigradi", Lists.newArrayList("Luca Damiani"), 
//				Lists.newArrayList("@radio3tweet", "#seigradiradio3"));
//
//		initShow("rai", "https://www.raiplayradio.it/programmi/stereonotte/", 
//				"Stereonotte", "stereonotte", Lists.newArrayList("Francesco Adinolfi", "Max De Tomassi", "Lele Sacchi", "Luca Sapio", "Mauro Zanda"), 
//				Lists.newArrayList("@radio1rai", "@stereonotte", "#stereonotteradio1"));
//
//		// Spreaker
//		initShow("spreaker", "https://api.spreaker.com/show/896299/episodes", 
//				"Casa Bertallot", "casabertallot", Lists.newArrayList("Alessio Bertallot"), 
//				Lists.newArrayList("@bertallot", "#casabertallot"));
//
//		initShow("spreaker", "https://api.spreaker.com/show/1977676/episodes", 
//				"Rollover Hangover", "rolloverhangover", Lists.newArrayList("Rocco Fusco"), 
//				Lists.newArrayList("@bertallot", "#rolloverhangover"));
//
//		initShow("spreaker", "https://api.spreaker.com/show/2071330/episodes", 
//				"Black A Lot", "blackalot", Lists.newArrayList("Michele Gas"), 
//				Lists.newArrayList("@bertallot", "#blackalot"));
//
//		initShow("spreaker", "https://api.spreaker.com/show/1501820/episodes", 
//				"Cassa Bertallot", "cassabertallot", Lists.newArrayList("Albi Scotti", "Marco Rigamonti"), 
//				Lists.newArrayList("@bertallot", "@albiscotti", "#cassabertallot"));
//
//		initShow("spreaker", "https://api.spreaker.com/show/2013495/episodes", 
//				"Reset Refresh", "resetrefresh", Lists.newArrayList("Federica Linke"), 
//				Lists.newArrayList("@bertallot", "@flikkarina", "#resetrefresh"));
//
//		initShow("spreaker", "https://api.spreaker.com/show/2708156/episodes", 
//				"The Tuesday Tapes", "thetuesdaytapes", Lists.newArrayList("Fabio De Luca"), 
//				Lists.newArrayList("@bertallot", "@thetuesdaytapes", "#thetuesdaytapes"));
//
//		initShow("spreaker", "https://api.spreaker.com/show/4380252/episodes", 
//				"Jazz Tracks", "jazztracks", Lists.newArrayList("Danilo Di Termini"), 
//				Lists.newArrayList("@daniloddt", "#jazztracks"));
	}

	private void initShow(String type, String url, String title, String source, List<String> authors, List<String> twitter) {
		Document show = new Document();
		show.append("type", type);
		show.append("url", url);
		show.append("title", title);
		show.append("source", source);
		show.append("authors", authors);
		show.append("twitter", twitter);
		shows.insertOne(show);
	}

	@Override
	public void load(String... args) {
		new BBCRadioLoader().load(args);
		new RadioRaiLoader().load(args);
		new SpreakerLoader().load(args);
	}
}
