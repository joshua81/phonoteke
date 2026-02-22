package org.humanbeats.crawler;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.humanbeats.model.HBDocument;
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.util.HumanBeatsUtils;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCrawler extends WebCrawler
{
	protected static final String USER_AGENT = "HumanBeats" + Long.toString(Calendar.getInstance().getTimeInMillis());

	public static MongoRepository repo;

	public static String type;
	public static String url;
	public static String id;
	public static String artist;
	public static String source;
	public static List<String> authors;
	public static Integer page;


	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", type))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", type), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			AbstractCrawler.id = show.getString("id");
			AbstractCrawler.url = show.getString("url");
			AbstractCrawler.artist = show.getString("title");
			AbstractCrawler.source = show.getString("source");
			AbstractCrawler.authors = show.get("authors", List.class);
			AbstractCrawler.page = args.length == 2 ? Integer.parseInt(args[1]) : 1;
			log.info("Crawling " + artist + " (" + page + " page)");
			crawl(url);
		}
	}

	protected void crawl(String url)
	{
		try
		{
			log.info("Crawling " + url);
			CrawlConfig config = new CrawlConfig();
			config.setCrawlStorageFolder(HumanBeatsUtils.CRAWL_STORAGE_FOLDER);
			config.setUserAgentString(USER_AGENT);
			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			robotstxtConfig.setUserAgentName(USER_AGENT);
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			CrawlController controller = new CrawlController(config, pageFetcher, new HumanBeatsParser(config), robotstxtServer);
			controller.addSeed(url);
			controller.start(getClass(), HumanBeatsUtils.NUMBER_OF_CRAWLERS);
		} 
		catch (Throwable t) 
		{
			log.error("ERROR crawling " + url + ": " + t.getMessage());
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
			String url = page.getWebURL().getURL();
			String id = getId(url);
			log.debug("Parsing page " + url);

			try
			{
				org.bson.Document json = repo.getDocs().find(Filters.eq("id", id)).iterator().tryNext();
				if(json == null) {
					Document doc = Jsoup.parse(((HtmlParseData)page.getParseData()).getHtml());
					HBDocument hbdoc = crawlDocument(url, doc);
					insertDoc(hbdoc);
				}
			}
			catch (Throwable t) {
				log.error("ERROR parsing page " + url + ": " + t.getMessage());
				throw new RuntimeException("ERROR parsing page " + url + ": " + t.getMessage());
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
			log.error("ERROR getUrl() "+ url + ": " + t.getMessage());
			return null;
		} 
	}

	protected void insertDoc(HBDocument doc) {
		try {
			validateDocument(doc);

			org.bson.Document json = doc.toJson();
			repo.getDocs().insertOne(json);
			log.info(doc.getType() + " " + doc.getUrl() + " added");

			// update last episode date
			if(TYPE.podcast.equals(doc.getType())) {
				try {
					MongoCursor<org.bson.Document> i = repo.getAuthors().find(Filters.eq("source", source)).limit(1).iterator();
					json = i.next();
					json.append("lastEpisodeDate", doc.getDate());
					repo.getAuthors().updateOne(Filters.eq("source", source), new org.bson.Document("$set", json));
					log.info("lastEpisodeDate " + source + " updated");
				}
				catch(Exception e) {
					log.error("ERROR updating lastEpisodeDate: " + e.getMessage(), e);
				}
			}
		}
		catch(Exception e) {
			log.error("ERROR inserting document " + doc.getUrl() + ": " + e.getMessage());
		}
	}

	private void validateDocument(HBDocument doc) {
		// podcast
		if(TYPE.podcast.equals(doc.getType())) {
			Preconditions.checkNotNull(doc, "Episode cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getId()), "Episode id cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getUrl()), "Episode url cannot be null");
			Preconditions.checkNotNull(doc.getType(), "Episode type cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getSource()), "Episode source cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getArtist()), "Episode artist cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getTitle()), "Episode title cannot be null");
			Preconditions.checkArgument(CollectionUtils.isNotEmpty(doc.getAuthors()), "Episode authors cannot be empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getDescription()), "Episode description cannot be null");
			Preconditions.checkNotNull(doc.getDate(), "Episode date cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getCover()), "Episode cover cannot be null");
			Preconditions.checkArgument(AbstractCrawler.type.equals(BBCRadioCrawler.BBC) || 
					StringUtils.isNotBlank(doc.getAudio()), "Episode audio cannot be null");
			Preconditions.checkNotNull(doc.getYear(), "Episode year cannot be null");
			Preconditions.checkArgument("alexpaletta".equals(doc.getSource()) || "musicalbox".equals(doc.getSource()) || 
					(CollectionUtils.isNotEmpty(doc.getTracks()) && doc.getTracks().size() >= HumanBeatsUtils.TRACKS_SIZE), "Episode tracks less than " + HumanBeatsUtils.TRACKS_SIZE);
		}
		// album
		else {
			Preconditions.checkNotNull(doc, "Album cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getId()), "Album id cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getUrl()), "Album url cannot be null");
			Preconditions.checkNotNull(doc.getType(), "Album type cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getSource()), "Album source cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getArtist()), "Album artist cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getTitle()), "Album title cannot be null");
			Preconditions.checkArgument(CollectionUtils.isNotEmpty(doc.getAuthors()), "Album authors cannot be empty");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getDescription()), "Album description cannot be null");
			Preconditions.checkNotNull(doc.getDate(), "Album date cannot be null");
			Preconditions.checkArgument(StringUtils.isNotBlank(doc.getCover()), "Album cover cannot be null");
			//Preconditions.checkArgument(StringUtils.isNotBlank(doc.getAudio()), "Album audio cannot be null");
			Preconditions.checkNotNull(doc.getYear(), "Album year cannot be null");
			//Preconditions.checkArgument(CollectionUtils.isNotEmpty(doc.getTracks()) 
			//&& doc.getTracks().size() >= HumanBeatsUtils.TRACKS_SIZE, "Album tracks less than " + HumanBeatsUtils.TRACKS_SIZE);

			Preconditions.checkArgument(CollectionUtils.isNotEmpty(doc.getGenres()), "Album geners cannot be empty");
			Preconditions.checkNotNull(StringUtils.isNotBlank(doc.getLabel()), "Album label cannot be null");
			Preconditions.checkNotNull(StringUtils.isNotBlank(doc.getReview()), "Album review cannot be null");
			Preconditions.checkNotNull(doc.getVote(), "Album vote cannot be null");
		}
	}

	private class HumanBeatsParser extends Parser {
		public HumanBeatsParser(CrawlConfig config) throws IllegalAccessException, InstantiationException {
			super(config, new HumanBeatsHtmlParser(config));
		}
	}

	private class HumanBeatsHtmlParser extends TikaHtmlParser {

		public HumanBeatsHtmlParser(CrawlConfig config) throws InstantiationException, IllegalAccessException {
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
				log.debug("ERROR parsing page " + contextURL + ": " + e.getMessage());
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
					//webURL.setTag(urlAnchorPair.getTag());
					//webURL.setAnchor(urlAnchorPair.getAnchor());
					//webURL.setAttributes(urlAnchorPair.getAttributes());
					urls.add(webURL);
				}
			}
			return urls;
		}
	}

	protected abstract String getBaseUrl();
	public abstract HBDocument crawlDocument(String url, Document doc);
	public abstract HBDocument crawlDocument(String url, JsonObject doc);
}
