package org.phonoteke.loader;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.phonoteke.loader.HumanBeatsUtils.TYPE;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WWFMCrawler extends AbstractCrawler
{
	private static final String WWFM = "wwfm";
	private static final String URL = "https://worldwidefm.net/";


	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", WWFM))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", WWFM), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			WWFMCrawler.url = show.getString("url");
			WWFMCrawler.artist = show.getString("title");
			WWFMCrawler.source = show.getString("source");
			WWFMCrawler.authors = show.get("authors", List.class);
			WWFMCrawler.page = args.length == 2 ? Integer.parseInt(args[1]) : 1;

			log.info("Crawling " + artist + " (" + page + " page)");
			crawl(WWFMCrawler.url);
		}
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return url.getURL().toLowerCase().contains("breakfast-club-coco");
	}

	@Override
	public void visit(Page page) 
	{
		if(page.getWebURL().getURL().contains("breakfast-club-coco")) {
			super.visit(page);
		}
	}

	@Override
	protected String getBaseUrl()
	{
		return URL;
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
		log.debug("date: " + date);
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
		try {
			org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(doc);
			XPath xpath = XPathFactory.newInstance().newXPath();
			Node result = (Node)xpath.evaluate("/html/body/main/div/div[1]/div[3]/div/div[2]/span[2]/span", w3cDoc, XPathConstants.NODE);

			if (result != null) {
				String title = result.getTextContent();
				log.debug("title: " + title);
				return title;
			}
			throw new RuntimeException("Title not found!");
		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	protected List<org.bson.Document> getTracks(String url, Document doc) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		Date date = getDate(url, doc);
		try {
			String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
			doc = Jsoup.connect(RadioCapitalCrawler.url + "playlist/dettaglio/" + dateStr).ignoreContentType(true).get();
			Elements content = doc.select("section.playlist-list").select("li");
			if(content != null && content.size() > 0) {
				Iterator<Element> i = content.iterator();
				while(i.hasNext()) {
					Element track = i.next();
					String title = track.select("span.author").text() + " - " + track.select("span.song").text();
					tracks.add(newTrack(title, null));
					log.debug("track: " + title);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		try {
			if(source.equals("alexpaletta") && date.after(new SimpleDateFormat("yyyy-MM-dd").parse("2024-11-30"))) {
				return tracks;
			}
		} catch (ParseException e) {
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
		log.debug("cover: " + cover);
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
		Date date = getDate(url, doc);
		String d1 = new SimpleDateFormat("yyyy/MM/dd").format(date);
		String d2 = new SimpleDateFormat("yyyyMMdd").format(date);
		String audio = null;
		if(source.equals("alexpaletta")) {
			audio = "https://media.capital.it/" + d1 + "/episodes/extra/extra_" + d2 + "_000000.mp3";
		}
		else if(source.equals("casabertallot")){
			audio = "https://media.capital.it/" + d1 + "/episodes/bertallot/bertallot_" + d2 + "_220000.mp3";
		}
		log.debug("audio: " + audio);
		return audio;
	}
}
