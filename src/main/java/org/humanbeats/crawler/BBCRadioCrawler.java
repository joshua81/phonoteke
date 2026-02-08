package org.humanbeats.crawler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.humanbeats.model.HBDocument;
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BBCRadioCrawler extends AbstractCrawler
{
	private static final String URL = "https://www.bbc.co.uk/";
	private static final String BBC = "bbc";

	public BBCRadioCrawler(MongoRepository repo) {
		super(repo);
	}

	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", BBC))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", BBC), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			this.url = show.getString("url");
			this.artist = show.getString("title");
			this.source = show.getString("source");
			this.authors = show.get("authors", List.class);
			this.page = args.length == 2 ? Integer.parseInt(args[1]) : 1;

			log.info("Crawling " + artist + " (" + page + " page)");
			crawl(url + "?page=" + page);
		}
	}

	@Override
	public org.bson.Document crawlDocument(String url, Document doc) {
		HBDocument playlistData = HBDocument.builder()
				.id(id)
				.url(url)
				.source(source)
				.type(TYPE.podcast)
				.artist(artist)
				.authors(authors)
				.date(getDate(doc))
				.year(getYear(doc))
				.description(getDescription(doc))
				.title(getTitle(doc))
				.cover(getCover(doc))
				.tracks(getTracks(doc)).build();
		return playlistData.toJson();
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) 
	{
		return page.getWebURL().getURL().startsWith(this.url);
	}

	@Override
	protected String getBaseUrl()
	{
		return URL;
	}

	private Date getDate(Document doc) 
	{
		Date date = null;
		try
		{
			Element content = doc.select("div.broadcast-event__time").first();
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

	private Integer getYear(Document doc) 
	{
		Integer year = null;
		Date date = getDate(doc);
		if(date != null)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			year = cal.get(Calendar.YEAR);
		}
		return year;
	}

	private String getDescription(Document doc) 
	{
		String desc = null;
		Element content = doc.select("meta[property=og:description]").first();
		if(content != null)
		{
			desc = content.attr("content").trim();
		}
		log.debug("description: " + desc);
		return desc;
	}

	private String getTitle(Document doc) 
	{
		String title = null;
		Element content = doc.select("meta[property=og:title]").first();
		if(content != null)
		{
			title = content.attr("content").trim();
		}
		log.debug("title: " + title);
		return title;
	}

	private List<org.bson.Document> getTracks(Document doc) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		Elements content = doc.select("div.segment__track");
		if(content != null && content.size() > 0) {
			Iterator<Element> i = content.iterator();
			while(i.hasNext()) {
				Element track = i.next();
				String title = track.select("h3").first() != null ? track.select("h3").first().text() : track.select("h4").first().text();
				title += " - " + track.select("p").first().text();
				tracks.add(newTrack(title, null));
				log.debug("track: " + title);
			}
		}
		return checkTracks(tracks);
	}

	private String getCover(Document doc) 
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
}
