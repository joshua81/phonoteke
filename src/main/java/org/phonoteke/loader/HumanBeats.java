package org.phonoteke.loader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public interface HumanBeats 
{
	public static final String MATCH1 = "[•*-]{0,1}(.{1,100}?),(.{1,100}?),(.{1,200})";
	public static final String MATCH2 = "[•*-]{0,1}(.{1,100}?),(.{1,100}?)[:–-](.{1,200})";
	public static final String MATCH3 = "[•*-]{0,1}(.{1,100}?)[\"“”](.{1,100}?)[\"“”](.{0,200})";
	public static final String MATCH4 = "[•*-]{0,1}(.{1,100}?)[‘’](.{1,100}?)[‘’](.{0,200})";
	public static final String MATCH5 = "[•*-]{0,1}(.{1,100}?)['](.{1,100}?)['](.{0,200})";
	public static final String MATCH6 = "[0-9]{1,2}[ ]{0,}[\\._)–-]{0,1}(.{1,100}?)[:–-](.{1,100})";
	public static final String MATCH7 = "[0-9]{1,2}[ ]{0,}[\\._)–-]{0,1}(.{1,100}?)[:–-](.{1,100}?)[:–-](.{1,200})";
	public static final String MATCH8 = "[0-9]{1,2}[ ]{0,}[\\._)–-]{0,1}(.{1,100}?)[:–-](.{1,100}?)\\(.{1,200}\\)";
	public static final String MATCH9 = "[•*-]{0,1}(.{1,100}?)[:–-](.{1,100})";
	public static final List<String> MATCHES = Lists.newArrayList(MATCH1, MATCH2, MATCH3, MATCH4, MATCH5, MATCH6, MATCH7, MATCH8, MATCH9);

	public static final String FEAT1 = "(?i)(.{1,100}?) feat[.]{0,1} (.{1,100})";
	public static final String FEAT2 = "(?i)(.{1,100}?) ft[.]{0,1} (.{1,100})";
	public static final String FEAT3 = "(?i)(.{1,100}?) featuring (.{1,100})";
	public static final String FEAT4 = "(.{1,100}?)[\\(\\[](.{1,100})";
	public static final String FEAT5 = "(.{1,100}?)([\\(\\[]{0,1}[0-9]{4}[\\)\\]]{0,1})";
	public static final List<String> FEAT_MATCH   = Lists.newArrayList(FEAT1, FEAT2, FEAT3, FEAT4, FEAT5);

	public static final String NA = "na";
	public static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	public static final int NUMBER_OF_CRAWLERS = 10;
	public static final String TRACKS_NEW_LINE = "_NEW_LINE_";
	public static final List<String> TRACKS_TRIM = Lists.newArrayList("100% Bellamusica ®", "PLAYLIST:", "PLAYLIST", "TRACKLIST:", "TRACKLIST", "PLAY:", "PLAY", "LIST:", "LIST", "TRACKS:", "TRACKS");
	public static final int SLEEP_TIME = 2000;
	public static final int THRESHOLD = 90;

	public enum TYPE {
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
			String subtask = args[0].split(":").length == 1 ? null : args[0].split(":")[1];
			if("mb".equals(task)) {
				new MusicbrainzLoader().load(subtask);
			}
			else if("sp".equals(task)) {
				new SpotifyLoader().load(subtask);
			}
			else if("tw".equals(task)) {
				new TwitterLoader().load(subtask);
			}
			else if("yt".equals(task)) {
				new YoutubeLoader().load(subtask);
			}
			else if("doc".equals(task)) {
				new OndarockLoader().load(subtask);
			}
			else if("pod".equals(task)) {
				new RadioRaiLoader().load(subtask);
				new SpreakerLoader().load(subtask);
			}
			else if("stats".equals(task)) {
				new StatsLoader().load(subtask);
			}
			else if("patch".equals(task)) {
				new PatchLoader().load(subtask);
			}
			else {
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
		}
	}

	public void load(String task);

	public static boolean isTrack(String title)
	{
		title = title.trim();
		for(String match : MATCHES)
		{
			if(title.matches(match))
			{
				return true;
			}
		}
		return false;
	}

	public static List<String[]> parseTrack(String track) 
	{
		List<String[]> matches = Lists.newArrayList();
		for(String match : MATCHES) 
		{
			Matcher m = Pattern.compile(match).matcher(track);
			if(m.matches()) {
				matches.add(parseArtistSong(m.group(1),  m.group(2)));
				matches.add(parseArtistSong(m.group(2),  m.group(1)));
			}
		}
		return matches;
	}

	public static String[] parseArtistSong(String artist, String song)
	{
		// artist
		artist = artist.split("/")[0];
		artist = artist.split("\\+")[0];
		artist = artist.split(",")[0];
		artist = artist.split(";")[0];
		artist = artist.split("&")[0];
		artist = artist.toLowerCase().split("\\band\\b")[0];
		artist = artist.toLowerCase().split("\\bwith\\b")[0];
		artist = artist.replaceAll("=", " ");
		for(String match2 : FEAT_MATCH) {
			Matcher m2 = Pattern.compile(match2).matcher(artist);
			if(m2.matches()) {
				artist = m2.group(1);
				break;
			}
		}

		// song
		for(String match2 : FEAT_MATCH) {
			Matcher m2 = Pattern.compile(match2).matcher(song);
			if(m2.matches()) {
				song = m2.group(1);
				break;
			}
		}

		return new String[]{artist, song};
	}
}
