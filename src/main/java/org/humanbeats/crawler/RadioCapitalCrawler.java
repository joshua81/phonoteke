package org.humanbeats.crawler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.humanbeats.model.HBDocument;
import org.humanbeats.model.HBTrack;
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.Jsoup;
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
public class RadioCapitalCrawler extends AbstractCrawler
{
	private static final String CAPITAL = "capital";
	private static final String URL = "https://www.capital.it/programmi/";

	public RadioCapitalCrawler(MongoRepository repo) {
		super(repo);
	}

	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", CAPITAL))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", CAPITAL), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			this.url = show.getString("url");
			this.artist = show.getString("title");
			this.source = show.getString("source");
			this.authors = show.get("authors", List.class);

			log.info("Crawling " + artist);
			crawl(url);
		}
	}

	@Override
	public HBDocument crawlDocument(String url, Document doc) {
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
				.audio(getAudio(doc))
				.tracks(getTracks(doc)).build();
		return playlistData;
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) 
	{
		return page.getWebURL().getURL().startsWith(this.url);
	}

	@Override
	public void visit(Page page) 
	{
		if(page.getWebURL().getURL().startsWith(url + "puntate/b-side-del") ||
				page.getWebURL().getURL().startsWith(url + "puntate/extra-del") ||
				page.getWebURL().getURL().startsWith(url + "puntate/puntata-del")) {
			super.visit(page);
		}
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
		return getTitle(doc);
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

	private List<HBTrack> getTracks(Document doc) 
	{
		List<HBTrack> tracks = Lists.newArrayList();
		Date date = getDate(doc);
		try {
			String show = null;
			if("alexpaletta".equals(source)) {
				show = "extra";
			}
			else if("casabertallot".equals(source)){
				show = "bertallot";
			}
			String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
			doc = Jsoup.connect(URL + show + "/playlist/dettaglio/" + dateStr).ignoreContentType(true).get();

			Elements content = doc.select("section.playlist-list").select("li");
			if(content != null && content.size() > 0) {
				Iterator<Element> i = content.iterator();
				while(i.hasNext()) {
					Element track = i.next();
					String title = track.select("span.author").text() + " - " + track.select("span.song").text();
					tracks.add(HBTrack.builder().titleOrig(title).build());
					log.debug("track: " + title);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return tracks;
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

	private String getAudio(Document doc) 
	{
		Date date = getDate(doc);
		String d1 = new SimpleDateFormat("yyyy/MM/dd").format(date);
		String d2 = new SimpleDateFormat("yyyyMMdd").format(date);
		String audio = null;
		if("alexpaletta".equals(source)) {
			audio = "https://media.capital.it/" + d1 + "/episodes/extra/extra_" + d2 + "_000000.mp3";
		}
		else if("casabertallot".equals(source)){
			audio = "https://media.capital.it/" + d1 + "/episodes/bertallot/bertallot_" + d2 + "_220000.mp3";
		}
		log.debug("audio: " + audio);
		return audio;
	}
}
