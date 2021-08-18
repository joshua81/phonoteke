package org.phonoteke.loader;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.operation.OrderBy;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.uci.ics.crawler4j.crawler.WebCrawler;

public abstract class HumanBeats extends WebCrawler
{
	protected static final List<String> SEPARATORS = Lists.newArrayList(
			">", 
			":", 
			"–", 
			"-", 
			",", 
			";", 
			"\"", 
			"'", 
			"“", 
			"”", 
			"‘", 
			"’", 
			"/", 
			"&", 
			"\\+", 
			"\\band\\b", 
			"\\bwith\\b", 
			"\\be\\b",
			"\\by\\b",
			"\\bx\\b",
			"\\baka\\b",
			"\\bvs[.]{0,1}\\b");

	protected static final String MATCH1 = "([0-9]{0,2}[•*\\|]{0,1}[0-9]{0,2}[\\._)\\|-]{0,1}){0,1}(.{1,100})\\|(.{1,100})\\(.{1,200}\\)";
	protected static final String MATCH2 = "([0-9]{0,2}[•*\\|]{0,1}[0-9]{0,2}[\\._)\\|-]{0,1}){0,1}(.{1,100})\\|(.{1,200})";
	protected static final List<String> MATCHS = Lists.newArrayList(MATCH1, MATCH2);

	protected static final String FEAT1 = "(?i)(.{1,100}?) feat[.]{0,1} (.{1,200})";
	protected static final String FEAT2 = "(?i)(.{1,100}?) ft[.]{0,1} (.{1,200})";
	protected static final String FEAT3 = "(?i)(.{1,100}?) featuring (.{1,200})";
	protected static final String FEAT4 = "(.{1,100}?)[\\(\\[](.{1,200})";
	protected static final String FEAT5 = "(.{1,100}?)([\\(\\[]{0,1}[0-9]{4}[\\)\\]]{0,1})";
	protected static final String FEAT6 = "(.{1,100}?)[0-9]{0,2}’[0-9]{0,2}”";
	protected static final String FEAT7 = "(.{1,100}?) - ([\\(\\[]{0,1}[0-9]{4}[\\)\\]]{0,1})";
	protected static final String FEAT8 = "(.{1,100}?) - ([\\(\\[]{0,1}[0-9]{4}[\\)\\]]{0,1}) Remaster";
	protected static final List<String> FEAT = Lists.newArrayList(FEAT1, FEAT2, FEAT3, FEAT4, FEAT5, FEAT6, FEAT7, FEAT8);

	protected static final String NA = "na";
	protected static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	protected static final int NUMBER_OF_CRAWLERS = 1;
	protected static final String TRACKS_NEW_LINE = "_NEW_LINE_";
	protected static final List<String> TRACKS_TRIM = Lists.newArrayList(
			"100% Bellamusica ®", 
			"PLAYLIST:", 
			"PLAYLIST", 
			"TRACKLIST:", 
			"TRACKLIST", 
			"PLAY:", 
			"PLAY", 
			"LIST:", 
			"LIST", 
			"TRACKS:", 
			"TRACKS");
	protected static final int THRESHOLD = 90;
	protected static final int SCORE = 60;
	protected static final int TRACKS_SIZE = 6;

	protected MongoCollection<org.bson.Document> shows;
	protected MongoCollection<org.bson.Document> docs;
	protected MongoCollection<org.bson.Document> authors;

	protected enum TYPE {
		artist,
		album,
		concert,
		interview,
		podcast,
		unknown
	}

	public static void main(String[] args) {
		if(args.length > 0) {
			String task = args[0].split(":")[0];
			String[] subtask = Arrays.copyOfRange(args[0].split(":"), 1, args[0].split(":").length);
			if("mb".equals(task)) {
				new MusicbrainzLoader().load(subtask);
			}
			else if("sp".equals(task)) {
				new SpotifyLoader().load(subtask);
			}
			else if("tw".equals(task)) {
				new SocialNetworkLoader().load(subtask);
			}
			else if("yt".equals(task)) {
				new YoutubeLoader().load(subtask);
			}
			else if("doc".equals(task)) {
				new OndarockLoader().load(subtask);
			}
			else if("pod".equals(task)) {
				new PodcastLoader().load(subtask);
			}
			else if("stats".equals(task)) {
				new StatsLoader().load(subtask);
			}
			else if("patch".equals(task)) {
				new PatchLoader().load(subtask);
			}
			else {
				printHelp();
			}
		}
		else {
			printHelp();
		}
	}

	protected HumanBeats() {
		try
		{
			Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.ERROR);

			MongoClientURI uri = new MongoClientURI(System.getenv("MONGO_URL"));
			MongoDatabase db = new MongoClient(uri).getDatabase(System.getenv("MONGO_DB"));
			docs = db.getCollection("docs");
			shows = db.getCollection("shows");
			authors = db.getCollection("authors");
		} 
		catch (Throwable t) 
		{
			throw new RuntimeException(t);
		}
	}

	private static void printHelp() {
		System.out.println("Usage:");
		System.out.println("- compile (compiles sources)");
		System.out.println("- deploy (deploys to GCloud)");
		System.out.println("- test (deploys test)");
		System.out.println("- mb (loads Music Brainz)");
		System.out.println("- sp (loads Spotify)");
		System.out.println("- sp:playlist (loads Spotify playlists)");
		System.out.println("- tw (loads Twitter)");
		System.out.println("- yt (loads Youtube)");
		System.out.println("- doc (loads documents)");
		System.out.println("- pod (loads podcasts)");
		System.out.println("- stats (loads stats)");
		System.out.println("- patch:resetTracksTitle (patches db)");
		System.out.println("- patch:calculateScore (patches db)");
		System.out.println("- patch:resetTracks (patches db)");
		System.out.println("- patch:replaceSpecialChars (patches db)");
	}

	abstract void load(String... args);

	protected boolean isTrack(String title)
	{
		title = cleanText(title);
		for(String s : SEPARATORS) {
			title = title.replaceAll(s, "|");
		}
		for(String match : MATCHS) {
			if(title.matches(match)) {
				return true;
			}
		}
		return false;
	}

	protected Set<String> parseTrack(String track) 
	{
		track = cleanText(track);
		for(String s : SEPARATORS) {
			track = track.replaceAll(s, "|");
		}

		Set<String> matches = new LinkedHashSet<String>();
		for(String match : MATCHS) {
			Matcher m = Pattern.compile(match).matcher(track);
			if(m.matches()) {
				track = m.group(2)+ "|" + m.group(3);
				List<String> chunks = Arrays.asList(track.split("\\|"));
				for(int i = 1; i < chunks.size(); i++) {
					for(int k = 1; k <= i; k++) {
						String artist = String.join(" ", chunks.subList(0, k));
						for(int j = i+1; j <= chunks.size(); j++) {
							String song = String.join(" ", chunks.subList(i, j));
							if(StringUtils.isNotBlank(artist) && StringUtils.isNotBlank(song)) {
								matches.add(parseArtistSong(artist, song));
							}
						}
					}
				}
			}
		}
		return matches;
	}

	protected String parseArtistSong(String artist, String song)
	{
		// artist
		artist = cleanText(artist);
		for(String match : FEAT) {
			Matcher matcher = Pattern.compile(match).matcher(artist);
			if(matcher.matches()) {
				artist = matcher.group(1);
				break;
			}
		}

		// song
		song = cleanText(song);
		for(String match : FEAT) {
			Matcher matcher = Pattern.compile(match).matcher(song);
			if(matcher.matches()) {
				song = matcher.group(1);
				break;
			}
		}
		return cleanText(artist) + " " + cleanText(song);
	}

	protected String cleanText(String text) 
	{
		// replaces all the HTML and non-HTML white spaces
		text = text.replaceAll("&nbsp;", " ");
		text = text.replaceAll("\\s+", " ");
		// erases all the ASCII control characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		// removes non-printable characters from Unicode
		text = text.replaceAll("\\p{C}", "");
		return text.toLowerCase().trim();
	}

	protected String format(String title, Date date) {
		Preconditions.checkNotNull(title);
		return date == null ? title.trim() : (title.trim() + " Ep." + new SimpleDateFormat("yyyy.MM.dd").format(date));
	}

	protected void updateLastEpisodeDate(String source) {
		MongoCursor<org.bson.Document> i = docs.find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", source))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(1).iterator();
		Date date = i.next().get("date", Date.class);
		i = authors.find(Filters.eq("source", source)).limit(1).iterator();
		Document doc = i.next();
		doc.append("lastEpisodeDate", date);
		authors.updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
	}
}
