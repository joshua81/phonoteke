package org.humanbeats.crawler;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.humanbeats.model.HBDocument;
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.util.HumanBeatsUtils;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
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

	protected MongoRepository repo;
	protected String url;
	protected String id;
	protected String artist;
	protected String source;
	protected List<String> authors;
	protected Integer page;

	public AbstractCrawler(MongoRepository repo) 
	{
		this.repo = repo;
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
			try
			{
				this.url = page.getWebURL().getURL();
				this.id = getId(url);
				log.debug("Parsing page " + url);

				org.bson.Document json = repo.getDocs().find(Filters.and(Filters.eq("source", source), 
						Filters.eq("id", id))).iterator().tryNext();
				if(json == null) {
					Document doc = Jsoup.parse(((HtmlParseData)page.getParseData()).getHtml());
					json = crawlDocument(url, doc).toJson();
					insertDoc(json);
				}
			}
			catch (Throwable t) {
				throw new RuntimeException("ERROR parsing page " + url, t);
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

	protected void insertDoc(org.bson.Document json) {
		repo.getDocs().insertOne(json);
		log.info(json.getString("type") + " " + json.getString("url") + " added");

		// update last episode date
		if(TYPE.podcast.name().equals(json.getString("type"))) {
			MongoCursor<org.bson.Document> i = repo.getAuthors().find(Filters.eq("source", source)).limit(1).iterator();
			org.bson.Document doc = i.next();
			doc.append("lastEpisodeDate", json.getDate("date"));
			repo.getAuthors().updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
			log.info("lastEpisodeDate " + source + " updated");
		}
	}

	protected abstract String getBaseUrl();

	public abstract HBDocument crawlDocument(String url, Document doc);

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
}
