package org.phonoteke.loader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class RadioCapitalLoader extends PodcastLoader
{
	private static final String CAPITAL_URL = "https://www.capital.it/programmi/";
	private static final String BSIDE_URL = CAPITAL_URL + "b-side/";
	private static final String EXTRA_URL = CAPITAL_URL + "extra/";

	public static void main(String[] args) {
		new RadioCapitalLoader().load("casabertallot");
	}

	@Override
	public void load(String... args) 
	{
		if(args.length == 0 || "capital".equals(args[0])) {
			RadioCapitalLoader.url = BSIDE_URL + "puntate/";
			RadioCapitalLoader.artist = "B-Side";
			RadioCapitalLoader.source = "casabertallot";
			RadioCapitalLoader.authors = Lists.newArrayList("Alessio Bertallot");
			crawl(RadioCapitalLoader.url);
			updateLastEpisodeDate(RadioRaiLoader.source);

			RadioCapitalLoader.url = EXTRA_URL + "puntate/";
			RadioCapitalLoader.artist = "Extra";
			RadioCapitalLoader.source = "alexpaletta";
			RadioCapitalLoader.authors = Lists.newArrayList("Alex Paletta");
			crawl(RadioCapitalLoader.url);
			updateLastEpisodeDate(RadioRaiLoader.source);
		}
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) 
	{
		return page.getWebURL().getURL().startsWith(RadioCapitalLoader.url);
	}

	@Override
	public void visit(Page page) 
	{
		if(page.getWebURL().getURL().startsWith(BSIDE_URL + "puntate/b-side-del") ||
				page.getWebURL().getURL().startsWith(BSIDE_URL + "puntate/puntata-del") || 
				page.getWebURL().getURL().startsWith(EXTRA_URL + "puntate/extra-del") ||
				page.getWebURL().getURL().startsWith(EXTRA_URL + "puntate/puntata-del")) {
			super.visit(page);
		}
	}

	@Override
	protected String getBaseUrl()
	{
		return CAPITAL_URL;
	}

	@Override
	protected String getSource() 
	{
		return source;
	}

	@Override
	protected String getArtist(String url, Document doc) 
	{
		return artist;
	}

	@Override
	protected List<String> getAuthors(String url, Document doc) 
	{
		return authors;
	}

	@Override
	protected Date getDate(String url, Document doc) 
	{
		Date date = null;
		try 
		{
			Element content = doc.select("meta[property=article:published_time]").first();
			if(content != null)
			{
				date = new SimpleDateFormat("yyyy-MM-dd").parse(content.attr("content").trim().substring(0, 10));
			}
		}
		catch(ParseException e) 
		{
			// nothing to do
		}
		LOGGER.debug("date: " + date);
		return date;
	}

	@Override
	protected Integer getYear(String url, Document doc) 
	{
		Integer year = null;
		Date date = getDate(url, doc);
		if(date != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			year = cal.get(Calendar.YEAR);
		}
		return year;
	}

	@Override
	protected String getDescription(String url, Document doc) 
	{
		return getTitle(url, doc);
	}

	@Override
	protected String getTitle(String url, Document doc) 
	{
		String title = null;
		Element content = doc.select("meta[property=og:title]").first();
		if(content != null)
		{
			title = content.attr("content").trim();
		}
		LOGGER.debug("title: " + title);
		return title;
	}

	@Override
	protected List<org.bson.Document> getTracks(String url, Document doc) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		try {
			String date = new SimpleDateFormat("yyyy-MM-dd").format(getDate(url, doc));
			if(url.startsWith(BSIDE_URL)) {
				doc = Jsoup.connect(BSIDE_URL + "playlist/dettaglio/" + date).ignoreContentType(true).get();
			}
			else if(url.startsWith(EXTRA_URL)) {
				doc = Jsoup.connect(EXTRA_URL + "playlist/dettaglio/" + date).ignoreContentType(true).get();
			}
			else {
				throw new RuntimeException("Unknown url: " + url);
			}
			Elements content = doc.select("section.playlist-list").select("li");
			if(content != null && content.size() > 0) {
				Iterator<Element> i = content.iterator();
				while(i.hasNext()) {
					Element track = i.next();
					String title = track.select("span.author").text() + " - " + track.select("span.song").text();
					tracks.add(newTrack(title, null));
					LOGGER.debug("track: " + title);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return checkTracks(tracks);
	}

	@Override
	protected String getCover(String url, Document doc) 
	{
		String cover = null;
		Element content = doc.select("meta[property=og:image]").first();
		if(content != null)
		{
			cover = content.attr("content").trim();
		}
		LOGGER.debug("cover: " + cover);
		return cover;
	}

	@Override
	protected TYPE getType(String url) 
	{
		return TYPE.podcast;
	}

	@Override
	protected String getAudio(String url, Document doc) 
	{
		String audio = null;
		Element content = doc.select("iframe").first();
		if(content != null)
		{
			List<NameValuePair> params = URLEncodedUtils.parse(content.attr("src"), StandardCharsets.UTF_8);
			NameValuePair param = params.stream().filter(p -> p.getName().equals("file")).findFirst().orElse(null);
			audio = param.getValue();
		}
		LOGGER.debug("audio: " + audio);
		return audio;
	}
}
