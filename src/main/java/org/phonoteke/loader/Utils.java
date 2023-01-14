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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public abstract class Utils
{
	public static final List<String> SEPARATORS = Lists.newArrayList(
			">", "<", ":", "–", "-", ",", ";", "\"", "'", "“", "”", "‘", "’", "/", "&", "\\+",
			"\\band\\b", "\\bwith\\b", "\\be\\b", "\\by\\b", "\\bx\\b", "\\baka\\b", "\\bvs[.]{0,1}\\b");

	public static final String MATCH1 = "([0-9]{1,2}[\\._)•*-\\|]{0,1}){0,1}(.{1,100})\\|(.{1,100})\\(.{1,200}\\)";
	public static final String MATCH2 = "([0-9]{1,2}[\\._)•*-\\|]{0,1}){0,1}(.{1,100})\\|(.{1,200})";
	public static final List<String> MATCHS = Lists.newArrayList(MATCH1, MATCH2);

	public static final String FEAT1 = "(?i)(.{1,100}?) feat[.]{0,1} (.{1,200})";
	public static final String FEAT2 = "(?i)(.{1,100}?) ft[.]{0,1} (.{1,200})";
	public static final String FEAT3 = "(?i)(.{1,100}?) featuring (.{1,200})";
	public static final String FEAT4 = "(.{1,100}?)[\\(\\[](.{1,200})";
	public static final String FEAT5 = "(.{1,100}?)([\\(\\[]{0,1}[0-9]{4}[\\)\\]]{0,1})";
	public static final String FEAT6 = "(.{1,100}?)[0-9]{0,2}’[0-9]{0,2}”";
	public static final String FEAT7 = "(.{1,100}?) - ([\\(\\[]{0,1}[0-9]{4}[\\)\\]]{0,1})";
	public static final String FEAT8 = "(.{1,100}?) - ([\\(\\[]{0,1}[0-9]{4}[\\)\\]]{0,1}) Remaster";
	public static final List<String> FEAT = Lists.newArrayList(FEAT1, FEAT2, FEAT3, FEAT4, FEAT5, FEAT6, FEAT7, FEAT8);

	public static final String NA = "na";
	public static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	public static final int NUMBER_OF_CRAWLERS = 1;
	public static final String TRACKS_NEW_LINE = "_NEW_LINE_";

	public static final int THRESHOLD = 90;
	public static final int SCORE = 80;
	public static final int TRACKS_SIZE = 6;

	public enum TYPE {
		artist,
		album,
		concert,
		interview,
		podcast,
		unknown
	}

	public static boolean isTrack(String title)
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

	public static Set<String> parseTrack(String track) 
	{
		track = cleanText(track);
		Set<String> matches = new LinkedHashSet<String>();
		matches.add(track);

		for(String s : SEPARATORS) {
			track = track.replaceAll(s, "|");
		}
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

	public static String parseArtistSong(String artist, String song)
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

	public static String cleanText(String text) 
	{
		// replaces all the HTML and non-HTML white spaces
		text = text.replaceAll("&nbsp;", " ");
		text = text.replaceAll("&amp;", "&");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("\\s+", " ");
		// erases all the ASCII control characters
		text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		// removes non-printable characters from Unicode
		text = text.replaceAll("\\p{C}", "");
		return text.toLowerCase().trim();
	}

	public static String format(String title, Date date) {
		Preconditions.checkNotNull(title);
		return date == null ? title.trim() : (title.trim() + " Ep." + new SimpleDateFormat("yyyy.MM.dd").format(date));
	}
}
