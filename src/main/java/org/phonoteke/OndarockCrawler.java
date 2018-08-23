package org.phonoteke;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class OndarockCrawler extends WebCrawler
{
	public static final String ONDAROCK_URL = "http://www.ondarock.it/";
	private static final Logger LOGGER = LogManager.getLogger(OndarockCrawler.class);
	private static final Pattern FILTERS = Pattern.compile(".*(\\.(htm|html))$");

	private static final String MONGO_HOST = "localhost";
	private static final int MONGO_PORT = 27017;
	private static final String MONGO_DB = "phonoteke";

	private DBCollection articles;


	public OndarockCrawler()
	{
		try 
		{
			DB db = new MongoClient(MONGO_HOST, MONGO_PORT).getDB(MONGO_DB);
			articles = db.getCollection("articles");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}


	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String dest = url.getURL().toLowerCase();
		return FILTERS.matcher(dest).matches() && dest.startsWith(ONDAROCK_URL);
	}

	@Override
	public void visit(Page page) {
		if (page.getParseData() instanceof HtmlParseData) 
		{
			ResultSet res = null;
			try 
			{
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				String html = htmlParseData.getHtml();
				Document doc = Jsoup.parse(html);
				String url = page.getWebURL().getURL();

				// check if the page must be crawled
				Article article = new Article(doc, url);
				if(article.getType() == null)
				{
					return;
				}

				// check if the article was already crawled
				DBObject articleDB = articles.findOne(article.getId());
				if(articleDB != null)
				{
					return;
				}

				// insert into DOCUMENT
				DBObject json = (DBObject)JSON.parse(new ObjectMapper().writeValueAsString(article));
				json.put("_id", article.getId());
				articles.insert(json);
				LOGGER.info("Document " + article + " added");
			} 
			catch (Throwable t) 
			{
				LOGGER.error("Error parsing page " + page.getWebURL().getURL());
			}
			finally
			{
				try
				{
					if(res != null)
					{
						res.close();
					}
				}
				catch(Throwable t)
				{
					// do nothing
				}
			}
		}
	}

	private enum TYPE {
		MONOGRAPH,
		REVIEW
	}

	private class Article
	{
		private String id;
		private String url;
		private TYPE type;
		private String content;
		private String band;
		private String album;
		private Date creationDate;
		private String cover;
		private Set<String> authors;
		private Set<String> genres;
		private Integer year;
		private String label;
		private Float vote;
		private Boolean milestone;
		private Set<String> links;
		private String source = "ondarock";

		public Article(Document doc, String url)
		{
			this.type = initType(url);
			if(getType() != null)
			{
				this.id = initId(url);
				this.url = initUrl(url);
				this.creationDate = initCreationDate(doc);
				this.content = initContentAndLinks(doc);
				this.band = initBand(doc);
				this.album = initAlbum(doc);
				this.cover = initCover(doc);
				this.authors = initAuthors(doc);
				this.genres = initGenres(doc);
				this.year = initYear(doc);
				this.label = initLabel(doc);
				this.vote = initVote(doc);
				this.milestone = initMilestone(url);
			}
		}

		private String initId(String url) {
			url = initUrl(url);
			String[] chunks = url.split("/");
			url = chunks[chunks.length-1].split("\\.")[0].replaceAll("_", "-").toLowerCase();
			return "ondarock:" + url;
		}

		private String initUrl(String url) {
			try 
			{
				if(url.startsWith(".") || url.startsWith("/"))
				{
					url = new URL(new URL(ONDAROCK_URL), url).toString();
					url = url.replaceAll("\\.\\./", "");
				}
				return url.trim();
			} 
			catch (Throwable t) 
			{
				LOGGER.error(t.getMessage());
				return null;
			} 
		}

		private String initContentAndLinks(Document doc) {
			try
			{
				Element content = doc.select("div[id=maintext]").first();
				removeComments(content);
				removeImages(content);
				removeScripts(content);
				removeDivs(content);
				removeLinks(content);

				InputStream is =  new ByteArrayInputStream(content.html().getBytes(StandardCharsets.UTF_8));
				return IOUtils.toString(is, StandardCharsets.UTF_8);
			}
			catch(Throwable t)
			{
				LOGGER.error(t.getMessage());
				return null;
			}
		}

		private void removeLinks(Element node) {
			links = Sets.newHashSet();

			Elements elements = node.select("a[href]");
			for(int i = 0; i < elements.size(); i++)
			{
				//				String link = initId(elements.get(i).attr("href"));
				String link = initUrl(elements.get(i).attr("href"));
				elements.get(i).unwrap();
				if(link != null)
				{
					links.add(link);
				}
			}
		}

		private void removeImages(Element node) {
			Elements elements = node.select("img");
			for(int i = 0; i < elements.size(); i++)
			{
				elements.get(i).remove();
			}
		}

		private void removeScripts(Element node) {
			Elements elements = node.select("script");
			for(int i = 0; i < elements.size(); i++)
			{
				elements.get(i).remove();
			}
		}

		private void removeDivs(Element node) {
			Elements elements = node.select("div");
			for(int i = 0; i < elements.size(); i++)
			{
				elements.get(i).remove();
			}
		}

		private void removeComments(Node node) {
			for (int i = 0; i < node.childNodeSize(); i++) {
				Node child = node.childNode(i);
				if (child.nodeName().equals("#comment"))
					child.remove();
				else {
					removeComments(child);
				}
			}
		}

		private String initBand(Document doc) {
			Element intestazioneElement = null;
			Element bandElement = null;
			String band = null;
			switch (type) {
			case REVIEW:
				intestazioneElement = doc.select("div[id=intestazionerec]").first();
				bandElement = intestazioneElement.select("h1").first();
				band = bandElement.text().trim();
				return band;
			case MONOGRAPH:
				intestazioneElement = doc.select("div[id=intestazione_OR3]").first();
				if(intestazioneElement == null)
				{
					intestazioneElement = doc.select("div[id=intestazione]").first();
				}
				bandElement = intestazioneElement.select("h2").first();
				band = bandElement.text().trim();
				return band;
			default:
				return null;
			}
		}

		private String initAlbum(Document doc) {
			Element intestazioneElement = null;
			Element albumElement = null;
			String album = null;
			switch (type) {
			case REVIEW:
				intestazioneElement = doc.select("div[id=intestazionerec]").first();
				albumElement = intestazioneElement.select("h2").first();
				album = albumElement.text().trim();
				return album;
			case MONOGRAPH:
				intestazioneElement = doc.select("div[id=intestazione_OR3]").first();
				if(intestazioneElement == null)
				{
					intestazioneElement = doc.select("div[id=intestazione]").first();
				}
				albumElement = intestazioneElement.select("h3").first();
				album = albumElement.text().trim();
				return album;
			default:
				return null;
			}
		}

		private Date initCreationDate(Document doc) {
			try
			{
				Date reviewDate = null;
				switch (type) {
				case REVIEW:
					Element reviewElement = doc.select("div[id=maintext]").first();
					Element reviewDateElement = reviewElement.select("p[style]").last();
					if(reviewDateElement != null)
					{
						java.util.Date date = new SimpleDateFormat("(dd/MM/yyyy)").parse(reviewDateElement.text());
						reviewDate = new Date(date.getTime());
					}
					if(reviewDate == null)
					{
						java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + year);
						reviewDate = new Date(date.getTime());
					}
					return reviewDate;
				case MONOGRAPH:
					reviewDate = new Date(Calendar.getInstance().getTime().getTime());
					return reviewDate;
				default:
					return null;
				}
			}
			catch(Throwable t)
			{
				LOGGER.error(t.getMessage());
				return null;
			}
		}

		private String initCover(Document doc) {
			try
			{
				Element coverElement = null;
				String cover = null;
				switch (type) {
				case REVIEW:
					coverElement = doc.select("div[id=cover_rec]").first();
					coverElement = coverElement.select("img[src]").first();
					cover = coverElement.attr("src");
					return initUrl(cover);
				case MONOGRAPH:
					coverElement = doc.select("div[id=col_right_mono]").first();
					coverElement = coverElement.select("img[src]").first();
					cover = coverElement.attr("src");
					return initUrl(cover);
				default:
					return null;
				}
			}
			catch(Throwable t)
			{
				LOGGER.error(t.getMessage());
				return null;
			}
		}

		private Set<String> initAuthors(Document doc) {
			Set<String> authors = null;
			Element authorElement = null;
			switch (type) {
			case REVIEW:
				authorElement = doc.select("div[class=recensorerec]").first();
				if(authorElement != null)
				{
					authorElement = authorElement.select("a[href]").first();
					authors = Sets.newHashSet(authorElement.html().trim().split(","));
				}
				return authors;
			case MONOGRAPH:
				authorElement = doc.select("span[class=recensore]").first();
				if(authorElement != null)
				{
					authorElement = authorElement.select("a[href]").first();
					authors = Sets.newHashSet(authorElement.html().trim().split(","));
				}
				return authors;
			default:
				return null;
			}
		}

		private Set<String> initGenres(Document doc) {
			switch (type) {
			case REVIEW:
				Element datiElement = doc.select("div[id=dati]").first();
				String dati = datiElement.text();
				Set<String> genres =  Sets.newHashSet(dati.split("\\|")[1].trim().split(","));
				return genres;
			default:
				return null;
			}
		}

		private Integer initYear(Document doc) {
			switch (type) {
			case REVIEW:
				Element datiElement = doc.select("div[id=dati]").first();
				String dati = datiElement.text();
				String yearStr = dati.split(" ")[0].trim();
				Integer year = Integer.parseInt(yearStr);
				return year;
			default:
				return 0;
			}
		}

		private String initLabel(Document doc) {
			switch (type) {
			case REVIEW:
				Element datiElement = doc.select("div[id=dati]").first();
				String dati = datiElement.text();
				dati = datiElement.text();
				dati = dati.split("\\(")[1].trim();
				String label = dati.split("\\)")[0].trim();
				return label;
			default:
				return null;
			}
		}

		private Float initVote(Document doc) {
			try
			{
				switch (type) {
				case REVIEW:
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setDecimalSeparator('.');
					DecimalFormat format = new DecimalFormat("##.#");
					format.setDecimalFormatSymbols(symbols);

					Float vote = 0F;
					Element intestazioneElement = doc.select("div[id=intestazionerec]").first();
					Element voteElement = intestazioneElement.select("img[src]").first();
					if(voteElement != null)
					{
						String voteStr = voteElement.attr("src");
						voteStr = voteStr.split("rate_")[1];
						voteStr = voteStr.substring(0, voteStr.length() - 4);
						vote = format.parse(voteStr).floatValue();
					}
					return vote;
				default:
					return 0F;
				}
			}
			catch(Throwable t)
			{
				LOGGER.error(t.getMessage());
				return 0F;
			}
		}

		private Boolean initMilestone(String url) {
			switch (type) {
			case REVIEW:
				Boolean milestone = url.contains("pietremiliari");
				return milestone;
			default:
				return false;
			}
		}

		private TYPE initType(String url) {
			if(url.startsWith(ONDAROCK_URL + "pietremiliari") || 
					url.startsWith(ONDAROCK_URL + "recensioni"))
			{
				return TYPE.REVIEW;
			}
			else if(url.startsWith(ONDAROCK_URL + "songwriter") || 
					url.startsWith(ONDAROCK_URL + "popmuzik") || 
					url.startsWith(ONDAROCK_URL + "altrisuoni") || 
					url.startsWith(ONDAROCK_URL + "rockedintorni") ||
					url.startsWith(ONDAROCK_URL + "dark") ||
					url.startsWith(ONDAROCK_URL + "italia") ||
					url.startsWith(ONDAROCK_URL + "jazz") ||
					url.startsWith(ONDAROCK_URL + "elettronica"))
			{
				return TYPE.MONOGRAPH;
			}
			return null;
		}

		@Override
		public String toString()
		{
			return "url: " + getUrl() + "\n" +
					"type: " + getType() + "\n" +
					"band: " + getBand() + "\n" +
					"album: " + getAlbum() + "\n" +
					"creationDate: " + getCreationDate() + "\n" +
					"cover: " + getCover() + "\n" +
					"authors: " + getAuthors() + "\n" +
					"genres: " + getGenres() + "\n" +
					"year: " + getYear() + "\n" +
					"label: " + getLabel() + "\n" +
					"vote: " + getVote() + "\n" +
					"milestone: " + getMilestone() + "\n" +
					"links: " + getLinks() + "\n" +
					"source: " + getSource();
		}

		//---------------------------------------

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public TYPE getType() {
			return type;
		}

		public void setType(TYPE type) {
			this.type = type;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getBand() {
			return band;
		}

		public void setBand(String band) {
			this.band = band;
		}

		public String getAlbum() {
			return album;
		}

		public void setAlbum(String album) {
			this.album = album;
		}

		public Date getCreationDate() {
			return creationDate;
		}

		public void setCreationDate(Date creationDate) {
			this.creationDate = creationDate;
		}

		public String getCover() {
			return cover;
		}

		public void setCover(String cover) {
			this.cover = cover;
		}

		public Set<String> getAuthors() {
			return authors;
		}

		public void setAuthor(Set<String> authors) {
			this.authors = authors;
		}

		public Set<String> getGenres() {
			return genres;
		}

		public void setGenre(Set<String> genres) {
			this.genres = genres;
		}

		public Integer getYear() {
			return year;
		}

		public void setYear(Integer year) {
			this.year = year;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Float getVote() {
			return vote;
		}

		public void setVote(Float vote) {
			this.vote = vote;
		}

		public Boolean getMilestone() {
			return milestone;
		}

		public void setMilestone(Boolean milestone) {
			this.milestone = milestone;
		}

		public Set<String> getLinks() {
			return links;
		}

		public void setLinks(Set<String> links) {
			this.links = links;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}
	}
}