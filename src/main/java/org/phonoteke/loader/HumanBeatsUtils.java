package org.phonoteke.loader;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class HumanBeatsUtils
{
	public static final List<String> SEPARATORS = Lists.newArrayList(
			"→", ">", "<", ":", "–", "-", ",", ";", "\"", "'", "“", "”", "‘", "’", "/", "&", "\\+",
			"\\band\\b", "\\bwith\\b", "\\be\\b", "\\by\\b", "\\bx\\b", "\\baka\\b", "\\bvs[.]{0,1}\\b",
			"\\feat[.]{0,1}\\b", "\\ft[.]{0,1}\\b", "\\featuring\\b");

	public static final String MATCH1 = "([0-9]{1,2}[\\._)•*-\\|]{0,1}){0,1}(.{1,100})\\|(.{1,100})\\(.{1,200}\\)";
	public static final String MATCH2 = "([0-9]{1,2}[\\._)•*-\\|]{0,1}){0,1}(.{1,100})\\|(.{1,200})";
	public static final List<String> MATCHS = Lists.newArrayList(MATCH1, MATCH2);

	public static final String NA = "na";
	public static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	public static final int NUMBER_OF_CRAWLERS = 1;
	public static final String TRACKS_NEW_LINE = "_NEW_LINE_";

	public static final int THRESHOLD = 90;
	public static final int SCORE = 80;
	public static final int TRACKS_SIZE = 5;

	public enum TYPE {
		album,
		podcast,
		unknown
	}

	public static void main(String[] args) {
		String track = "KHALAB & M'BERRA ENSEMBLE - Reste À L'Ombre";
		Set<String> tracks = parseTitle(track);
		tracks.forEach(t -> System.out.println(t));
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

	public static Set<String> parseTitle(String title) 
	{
		title = cleanText(title);
		Set<String> matches = Sets.newHashSet();
		matches.add(title);

		for(String s : SEPARATORS) {
			title = title.replaceAll(s, "|");
		}
		for(String match : MATCHS) {
			Matcher m = Pattern.compile(match).matcher(title);
			if(m.matches()) {
				title = m.group(2)+ "|" + m.group(3);
				Set<String> chunks = Sets.newLinkedHashSet(Arrays.asList(title.split("\\|")));
				Sets.powerSet(chunks).stream().forEach(subset -> {
					if(subset.size() > 1) {
						matches.add(cleanChunks(subset));
					}
				});
			}
		}
		// sort strings from bigger to smaller
		return matches.stream()
				.sorted(Comparator.comparingInt(String::length).reversed())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private static String cleanChunks(Set<String> chunks)
	{
		List<String> cleanChunks = Lists.newArrayList();
		chunks.forEach(c -> {
			cleanChunks.add(cleanText(c));

		});
		return String.join(" ", cleanChunks);
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
}
