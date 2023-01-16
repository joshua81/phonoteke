package org.phonoteke.loader;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.phonoteke.loader.HumanBeatsUtils.TYPE;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.internal.operation.OrderBy;

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
	private static final String USER_AGENT = "HumanBeats" + Long.toString(Calendar.getInstance().getTimeInMillis());

	public static MongoRepository repo;
	public static String url;
	public static String id;
	public static String artist;
	public static String source;
	public static List<String> authors;	

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
			CrawlController controller = new CrawlController(config, pageFetcher, new PhonotekeParser(config), robotstxtServer);
			controller.addSeed(url);
			controller.start(getClass(), HumanBeatsUtils.NUMBER_OF_CRAWLERS);
		} 
		catch (Throwable t) 
		{
			log.error("ERROR crawling " + url + ": " + t.getMessage());
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

			if(!TYPE.unknown.equals(type))
			{
				try
				{
					log.debug("Parsing page " + url);
					String id = getId(url);
					Document doc = Jsoup.parse(html);
					String artist = getArtist(url, doc);
					String title = getTitle(url, doc);

					org.bson.Document json = repo.getDocs().find(Filters.and(Filters.eq("source", source), 
							Filters.eq("url", url))).iterator().tryNext();
					if(json == null)
					{
						switch(type)
						{
						case album:
						case podcast:
							if(type.equals(TYPE.podcast) || !repo.getDocs().find(Filters.and(Filters.eq("source", source),
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
							if(!repo.getDocs().find(Filters.and(Filters.eq("source", source), 
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
							if(!repo.getDocs().find(Filters.and(Filters.eq("source", source), 
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
							if(!repo.getDocs().find(Filters.and(Filters.eq("source", source), 
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
							repo.getDocs().insertOne(json);
							log.info(json.getString("type") + " " + url + " added");
						}
					}
				}
				catch (Throwable t) 
				{
					log.error("ERROR parsing page " + url + ": " + t.getMessage());
					throw new RuntimeException(t);
				}
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

	protected List<org.bson.Document> getVideos(Elements elements) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		for(int i = 0; i < elements.size(); i++)
		{
			String src = elements.get(i).attr("src");
			if(src != null && src.contains("youtube.com")) 
			{
				String youtube = null;
				if(src.startsWith("https://www.youtube.com/embed/"))
				{
					int ix = "https://www.youtube.com/embed/".length();
					youtube = src.substring(ix);
					tracks.add(newTrack(null, youtube));
					log.debug("tracks: youtube: " + youtube);
				}
				else if(src.startsWith("//www.youtube.com/embed/"))
				{
					int ix = "//www.youtube.com/embed/".length();
					youtube = src.substring(ix);
					tracks.add(newTrack(null, youtube));
					log.debug("tracks: youtube: " + youtube);
				}
			}
		}
		return tracks;
	}

	protected List<org.bson.Document> getTracks(Element content, String source) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		if(content != null)
		{
			content.select("br").after(HumanBeatsUtils.TRACKS_NEW_LINE);
			content.select("p").after(HumanBeatsUtils.TRACKS_NEW_LINE);
			content.select("li").after(HumanBeatsUtils.TRACKS_NEW_LINE);
			content.select("h1").after(HumanBeatsUtils.TRACKS_NEW_LINE);
			content.select("h2").after(HumanBeatsUtils.TRACKS_NEW_LINE);
			content.select("h3").after(HumanBeatsUtils.TRACKS_NEW_LINE);
			content.select("div").after(HumanBeatsUtils.TRACKS_NEW_LINE);

			String[] chunks = content.text().replace("||", HumanBeatsUtils.TRACKS_NEW_LINE).split(HumanBeatsUtils.TRACKS_NEW_LINE);
			for(int i = 0; i < chunks.length; i++)
			{
				String title = chunks[i].trim();
				if(StringUtils.isNotBlank(title) && HumanBeatsUtils.isTrack(title))
				{
					tracks.add(newTrack(title, null));
					log.debug("tracks: " + title);
				}
			}
		}
		return checkTracks(tracks);
	}

	protected static org.bson.Document newTrack(String title, String youtube)
	{
		if(StringUtils.isNoneEmpty(title)) {
			title = title.replaceAll("&nbsp;", " ");
			title = title.trim();
		}
		return new org.bson.Document("titleOrig", title).
				append("title", title).
				append("youtube", youtube);
	}

	protected List<org.bson.Document> checkTracks(List<org.bson.Document> tracks)
	{
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(tracks) && tracks.size() >= HumanBeatsUtils.TRACKS_SIZE, "Number of tracks less than " + HumanBeatsUtils.TRACKS_SIZE);
		return tracks;
	}

	protected void updateLastEpisodeDate(String source) {
		MongoCursor<org.bson.Document> i = repo.getDocs().find(Filters.and(Filters.eq("type", "podcast"), Filters.eq("source", source))).sort(new BasicDBObject("date", OrderBy.DESC.getIntRepresentation())).limit(1).iterator();
		Date date = i.next().get("date", Date.class);
		i = repo.getAuthors().find(Filters.eq("source", source)).limit(1).iterator();
		org.bson.Document doc = i.next();
		doc.append("lastEpisodeDate", date);
		repo.getAuthors().updateOne(Filters.eq("source", source), new org.bson.Document("$set", doc));
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

	protected String cleanHTML(String html) {
		return Jsoup.parse(html).wholeText();
	}

	//-------------------------------------------

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
				log.error("ERROR parsing page " + contextURL + ": " + e.getMessage());
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
