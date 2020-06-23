package org.phonoteke.loader;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.parser.TikaHtmlParser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

public abstract class PhonotekeLoader extends WebCrawler
{
	protected static final Logger LOGGER = LogManager.getLogger(PhonotekeLoader.class);

	private static final String BATTITI_MATCH = "[\\*\\-]{0,1}(.{1,80}),(.{1,80}),[ ]{0,}da[ ]{0,}[\\“\"\\'\\‘\\’](.{1,80})[\\“\"\\'\\‘\\’](.{1,80})";
	private static final String BERTALLOT_MATCH = "[0-9]{1,2}[ ]{0,}[\\_\\)\\-\\–][ ]{0,}(.{1,80})[\\-\\–](.{1,80})\\([0-9]{4}\\)";
	private static final String BERTALLOT2_MATCH = "[0-9]{1,2}[ ]{0,}[\\_\\)\\-\\–][ ]{0,}(.{1,80})[\\-\\–](.{1,80})";
	private static final String INTHEMIX_MATCH = "[\\*\\-]{0,1}(.{1,80})[\\“\"\\'\\‘\\’](.{1,80})[\\“\"\\'\\‘\\’](.{1,80})";
	private static final String DEFAULT_MATCH = "[\\*\\-]{0,1}(.{1,80})[\\:\\-\\–](.{1,80})";

	protected static final String NA = "na";
	protected static final String CRAWL_STORAGE_FOLDER = "data/phonoteke";
	protected static final int NUMBER_OF_CRAWLERS = 10;
	protected static final List<String> TRACKS_MATCH = Lists.newArrayList(BATTITI_MATCH, BERTALLOT_MATCH, BERTALLOT2_MATCH, INTHEMIX_MATCH, DEFAULT_MATCH);
	protected static final String TRACKS_NEW_LINE = "_NEW_LINE_";
	protected static final List<String> TRACKS_TRIM = Lists.newArrayList("100% Bellamusica ®", "PLAYLIST:", "PLAYLIST", "TRACKLIST:", "TRACKLIST", "PLAY:", "PLAY", "LIST:", "LIST", "TRACKS:", "TRACKS");
	protected static final int SLEEP_TIME = 2000;
	protected static final int THRESHOLD = 90;

	protected MongoCollection<org.bson.Document> docs;
	protected MongoCollection<org.bson.Document> podcasts;

	protected enum TYPE {
		artist,
		album,
		concert,
		interview,
		podcast,
		unknown
	}

	public PhonotekeLoader()
	{
		try
		{
			MongoClientURI uri = new MongoClientURI(System.getenv("MONGO_URL"));
			MongoDatabase db = new MongoClient(uri).getDatabase(System.getenv("MONGO_DB"));
			docs = db.getCollection("docs");
			podcasts = db.getCollection("podcasts");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage(), t);
			throw new RuntimeException(t);
		}
	}

	protected void crawl(String url)
	{
		try
		{
			LOGGER.info("Crawling " + url);
			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			CrawlController controller = new CrawlController(config, pageFetcher, new PhonotekeParser(config), robotstxtServer);
			controller.addSeed(url);
			controller.start(getClass(), NUMBER_OF_CRAWLERS);
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error crawling " + url + ": " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return true;
	}

	@Override
	public void visit(Page page) 
	{
		if(page.getParseData() instanceof HtmlParseData) 
		{
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String html = htmlParseData.getHtml();
			String url = page.getWebURL().getURL();
			String source = getSource();
			TYPE type = getType(url);

			if(!url.endsWith(".htm") && !url.endsWith(".html") && !TYPE.unknown.equals(type))
			{
				return;
			}

			try
			{
				LOGGER.debug("Parsing page " + url);
				String id = getId(url);
				Document doc = Jsoup.parse(html);
				String artist = getArtist(url, doc);
				String title = getTitle(url, doc);

				org.bson.Document json = docs.find(Filters.and(Filters.eq("source", source), 
						Filters.eq("url", url))).iterator().tryNext();
				if(json == null)
				{
					switch(type)
					{
					case album:
					case podcast:
						if(type.equals(TYPE.podcast) || !docs.find(Filters.and(Filters.eq("source", source),
								Filters.eq("type", type.name()),
								Filters.eq("artist", artist),
								Filters.eq("title", title))).iterator().hasNext())
						{
							json = new org.bson.Document("id", id).
									append("url", getUrl(url)).
									append("type", type.name()).
									append("artist", artist).
									append("title", title).
									append("authors", getAuthors(url, doc)).
									append("cover", getCover(url, doc)).
									append("date", getDate(url, doc)).
									append("description", getDescription(url, doc)).
									append("genres", getGenres(url, doc)).
									append("label", getLabel(url, doc)).
									append("links", getLinks(url, doc)).
									append("review", getReview(url, doc)).
									append("source", getSource()).
									append("vote", getVote(url, doc)).
									append("year", getYear(url, doc)).
									append("tracks", getTracks(url, doc)).
									append("audio", getAudio(url, doc));
						}
						break;
					case artist:
						if(!docs.find(Filters.and(Filters.eq("source", source), 
								Filters.eq("type", type.name()),
								Filters.eq("artist", artist))).iterator().hasNext())
						{
							json = new org.bson.Document("id", id).
									append("url", getUrl(url)).
									append("type", type.name()).
									append("artist", artist).
									append("title", title).
									append("authors", getAuthors(url, doc)).
									append("cover", getCover(url, doc)).
									append("date", getDate(url, doc)).
									append("description", getDescription(url, doc)).
									append("links", getLinks(url, doc)).
									append("review", getReview(url, doc)).
									append("source", getSource());
						}
						break;
					case concert:
						if(!docs.find(Filters.and(Filters.eq("source", source), 
								Filters.eq("type", type.name()),
								Filters.eq("artist", artist),
								Filters.eq("title", title))).iterator().hasNext())
						{
							json = new org.bson.Document("id", id).
									append("url", getUrl(url)).
									append("type", type.name()).
									append("artist", artist).
									append("title", title).
									append("authors", getAuthors(url, doc)).
									append("cover", getCover(url, doc)).
									append("date", getDate(url, doc)).
									//									append("description", getDescription(url, doc)).
									append("links", getLinks(url, doc)).
									append("review", getReview(url, doc)).
									append("source", getSource());
						}
						break;
					case interview:
						if(!docs.find(Filters.and(Filters.eq("source", source), 
								Filters.eq("type", type.name()),
								Filters.eq("artist", artist),
								Filters.eq("title", title))).iterator().hasNext())
						{
							json = new org.bson.Document("id", id).
									append("url", getUrl(url)).
									append("type", type.name()).
									append("artist", artist).
									append("title", title).
									append("authors", getAuthors(url, doc)).
									append("cover", getCover(url, doc)).
									//									append("date", getDate(url, doc)).
									//									append("description", getDescription(url, doc)).
									append("links", getLinks(url, doc)).
									append("review", getReview(url, doc)).
									append("source", getSource());
						}
						break;
					default:
						break;
					}
					if(json != null)
					{
						docs.insertOne(json);
						LOGGER.info(json.getString("type") + " " + url + " added");
					}
				}
			}
			catch (Throwable t) 
			{
				LOGGER.error("Error parsing page " + url + ": " + t.getMessage(), t);
				throw new RuntimeException(t);
			}
		}
	}

	protected String getId(String url) 
	{
		return Hashing.sha256().hashString(getUrl(url), StandardCharsets.UTF_8).toString();
	}

	protected String getUrl(String url) 
	{
		try 
		{
			if(url.startsWith(".") || url.startsWith("/"))
			{
				url = new URL(new URL(getBaseUrl()), url).toString();
				url = url.replaceAll("\\.\\./", "");
			}
			return url.trim();
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error getUrl() "+ url + ": " + t.getMessage(), t);
			return null;
		} 
	}

	protected static boolean isTrack(String title)
	{
		for(String match : TRACKS_MATCH)
		{
			if(title.matches(match))
			{
				return true;
			}
		}
		return false;
	}

	protected static org.bson.Document newTrack(String title, String youtube)
	{
		return new org.bson.Document("title", title).
				append("youtube", youtube);
	}

	//---------------------------------
	// Methods to be overridden
	//---------------------------------
	protected String getBaseUrl()
	{
		return null;
	}

	protected String getSource() 
	{
		return null;
	}

	protected TYPE getType(String url) 
	{
		return TYPE.unknown;
	}

	protected String getArtist(String url, Document doc) {
		return null;
	}

	protected List<String> getAuthors(String url, Document doc) {
		return null;
	}

	protected String getCover(String url, Document doc) {
		return null;
	}

	protected Date getDate(String url, Document doc) {
		return null;
	}

	protected String getDescription(String url, Document doc) {
		return null;
	}

	protected List<String> getGenres(String url, Document doc) {
		return null;
	}

	protected String getLabel(String url, Document doc) {
		return null;
	}

	protected String getReview(String url, Document doc) {
		return null;
	}

	protected List<String> getLinks(String url, Document doc) {
		return null;
	}

	protected String getTitle(String url, Document doc) {
		return null;
	}

	protected List<org.bson.Document> getTracks(String url, Document doc) {
		return null;
	}

	protected Float getVote(String url, Document doc) {
		return null;
	}

	protected Integer getYear(String url, Document doc) {
		return null;
	}

	protected String getAudio(String url, Document doc) {
		return null;
	}

	private class PhonotekeParser extends Parser {
		public PhonotekeParser(CrawlConfig config) throws IllegalAccessException, InstantiationException {
			super(config, new PhonotekeHtmlParser(config));
		}
	}

	private class PhonotekeHtmlParser extends TikaHtmlParser {

		public PhonotekeHtmlParser(CrawlConfig config) throws InstantiationException, IllegalAccessException {
			super(config);
		}

		public HtmlParseData parse(Page page, String contextURL) throws ParseException {
			try {
				Document doc = Jsoup.parse(new URL(contextURL), 60000);
				HtmlParseData parsedData = new HtmlParseData();
				parsedData.setContentCharset("UTF-8");
				parsedData.setHtml(doc.html());
				parsedData.setText(doc.html());
				parsedData.setTitle(doc.title());
				parsedData.setOutgoingUrls(getOutgoingUrls(doc));
				parsedData.setMetaTags(Maps.newHashMap());
				return parsedData;
			} catch (IOException e) {
				LOGGER.error("Error parsing page " + contextURL + ": " + e.getMessage(), e);
				throw new ParseException();
			}
		}

		private Set<WebURL> getOutgoingUrls(Document doc) {
			Set<WebURL> urls = new HashSet<>();
			Elements elements = doc.select("a[href]");
			for(int i = 0; i < elements.size(); i++)
			{
				String link = getUrl(elements.get(i).attr("href"));
				if(link.startsWith(getBaseUrl()))
				{
					WebURL webURL = new WebURL();
					webURL.setURL(link);
					//					webURL.setTag(urlAnchorPair.getTag());
					//					webURL.setAnchor(urlAnchorPair.getAnchor());
					//					webURL.setAttributes(urlAnchorPair.getAttributes());
					urls.add(webURL);
				}
			}
			return urls;
		}
	}
}
