package org.phonoteke;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class PhonotekeLoader 
{
	private static final Logger LOGGER = LogManager.getLogger(PhonotekeLoader.class);

	public static final String MONGO_HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String MONGO_DB = "phonoteke";

	private DBCollection pages;
	private DBCollection articles;

	public enum TYPE {
		MONOGRAPH,
		REVIEW,
		UNKNOWN
	}

	public static void main(String[] args) 
	{
		PhonotekeLoader loader = new PhonotekeLoader();
		loader.loadAlbums();
		loader.loadSpotifyIds();
	}

	public PhonotekeLoader()
	{
		try 
		{
			DB db = new MongoClient(MONGO_HOST, MONGO_PORT).getDB(MONGO_DB);
			pages = db.getCollection("pages");
			articles = db.getCollection("articles");
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}
	
	private void loadSpotifyIds()
	{
		try 
		{
			DBCursor i = articles.find(BasicDBObjectBuilder.start().add("spotify", null).add("type", TYPE.REVIEW.name()).get());
			SpotifyLoader spotify = new SpotifyLoader();
			while(i.hasNext())
			{
				DBObject page = i.next();
				String id = (String)page.get("id");
				DBObject album = spotify.getId(id);
				if(album != null)
				{
					String spotifyId = (String)album.get("album");
					String cover = (String)album.get("image300");
					articles.findAndModify(BasicDBObjectBuilder.start().add("id", page.get("id")).get(), 
							BasicDBObjectBuilder.start().add("spotify", spotifyId).add("cover", cover).get());
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error closing BufferedReader: " + e.getMessage());
		}
	}

	private void loadAlbums()
	{
		//		DBCursor i = pages.find(BasicDBObjectBuilder.start().add("url", "http://www.ondarock.it/recensioni/2018-makai-thecomfortzone.htm").get());
		DBCursor i = pages.find();
		while(i.hasNext())
		{
			DBObject page = i.next();
			Number _id = (Number)page.get("_id");
			String url = (String)page.get("url");
			String html = (String)page.get("page");

			// check if the article was already crawled
			DBObject articleDB = articles.findOne(_id);
			if(articleDB == null)
			{
				try
				{
					Document doc = Jsoup.parse(html);
					TYPE type = getType(url, doc);
					if(type != TYPE.UNKNOWN)
					{
						String id = getId(url, doc);
						String spotify = getSpotify(url, doc);
						Set<String> youtube = getYoutube(url, doc);
						Date creationDate = getCreationDate(url, doc);
						Set<String> links = getLinks(url, doc);
						String band = getBand(url, doc);
						String album = getAlbum(url, doc);
						String cover = getCover(url, doc);
						Set<String> authors = getAuthors(url, doc);
						Set<String> genres = getGenres(url, doc);
						Integer year = getYear(url, doc);
						String label = getLabel(url, doc);
						Float vote = getVote(url, doc);
						Boolean milestone = getMilestone(url, doc);

						String content = getContent(url, doc);
						String source = "ondarock";

						// insert DOCUMENT
						DBObject json = BasicDBObjectBuilder.start().
								add("_id", _id).
								add("id", id).
								add("spotify", spotify).
								add("youtube", youtube).
								add("url", url).
								add("type", type.name()).
								add("content", content).
								add("band", band).
								add("album", album).
								add("creationDate", creationDate).
								add("cover", cover).
								add("authors", authors).
								add("genres", genres).
								add("label", label).
								add("year", year).
								add("vote", vote).
								add("milestone", milestone).
								add("source", source).
								add("links", links).get();
						articles.insert(json);
						LOGGER.info("Document " + url + " added");
					} 
				}
				catch (Throwable t) 
				{
					LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
				}
			}
		}
	}

	private String getId(String url, Document doc) {
		String id  = "ondarock";
		String[] chunks = getUrl(url, doc).split("/");
		for(int i = 3; i < chunks.length; i++)
		{
			id += ":" + chunks[i].split("\\.")[0].replaceAll("_", ":").toLowerCase();
		}
		return id;
	}

	private String getUrl(String url, Document doc) {
		try 
		{
			if(url.startsWith(".") || url.startsWith("/"))
			{
				url = new URL(new URL(OndarockCrawler.ONDAROCK_URL), url).toString();
				url = url.replaceAll("\\.\\./", "");
			}
			return url.trim();
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error getUrl() "+ url + ": " + t.getMessage());
			return null;
		} 
	}

	private String getContent(String url, Document doc) {
		try
		{
			Element content = doc.select("div[id=maintext]").first();
			removeComments(content);
			removeImages(content);
			removeScripts(content);
			removeDivs(content);
			removeLinks(content, doc);

			InputStream is =  new ByteArrayInputStream(content.html().getBytes(StandardCharsets.UTF_8));
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getContent() "+ url + ": " + t.getMessage());
			return null;
		}
	}

	private void removeLinks(Element node, Document doc) {
		Elements elements = node.select("a[href]");
		for(int i = 0; i < elements.size(); i++)
		{
			elements.get(i).unwrap();
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
		for(int i = 1; i < elements.size(); i++)
		{
			elements.get(i).unwrap();
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

	private Set<String> getLinks(String url, Document doc) {
		Set<String> links = Sets.newHashSet();
		Element content = doc.select("div[id=maintext]").first();
		Elements elements = content.select("a[href]");
		for(int i = 0; i < elements.size(); i++)
		{
			String link = getUrl(elements.get(i).attr("href"), doc);
			links.add(link);
		}
		return links;
	}

	private String getBand(String url, Document doc) {
		Element intestazioneElement = null;
		Element bandElement = null;
		String band = null;
		switch (getType(url, doc)) {
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

	private String getAlbum(String url, Document doc) {
		Element intestazioneElement = null;
		Element albumElement = null;
		String album = null;
		switch (getType(url, doc)) {
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

	private Date getCreationDate(String url, Document doc) {
		try
		{
			Date reviewDate = null;
			switch (getType(url, doc)) {
			case REVIEW:
				Element reviewElement = doc.select("div[id=maintext]").first();
				Element reviewDateElement = reviewElement.select("p[style]").last();
				if(reviewDateElement != null)
				{
					Date date = new SimpleDateFormat("(dd/MM/yyyy)").parse(reviewDateElement.text());
					reviewDate = new Date(date.getTime());
				}
				if(reviewDate == null && getYear(url, doc) != null)
				{
					Date date = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + getYear(url, doc));
					reviewDate = new Date(date.getTime());
				}
				return reviewDate;
			case MONOGRAPH:
				//				reviewDate = new Date(Calendar.getInstance().getTime().getTime());
				return null;
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getCreationDate() "+ url + ": " + t.getMessage());
			return null;
		}
	}

	private String getCover(String url, Document doc) {
		try
		{
			Element coverElement = null;
			String cover = null;
			switch (getType(url, doc)) {
			case REVIEW:
				coverElement = doc.select("div[id=cover_rec]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover, doc);
			case MONOGRAPH:
				coverElement = doc.select("div[id=col_right_mono]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover, doc);
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getCover() "+ url + ": " + t.getMessage());
			return null;
		}
	}

	private Set<String> getAuthors(String url, Document doc) {
		Set<String> authors = null;
		Element authorElement = null;
		switch (getType(url, doc)) {
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

	private Set<String> getGenres(String url, Document doc) {
		switch (getType(url, doc)) {
		case REVIEW:
			Element datiElement = doc.select("div[id=dati]").first();
			String dati = datiElement.text();
			Set<String> genres =  Sets.newHashSet(dati.split("\\|")[1].trim().split(","));
			return genres;
		default:
			return null;
		}
	}

	private Integer getYear(String url, Document doc) {
		switch (getType(url, doc)) {
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

	private String getLabel(String url, Document doc) {
		switch (getType(url, doc)) {
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

	private String getSpotify(String url, Document doc) {
		Elements elements = doc.select("iframe");
		for(int i = 0; i < elements.size(); i++)
		{
			String src = elements.get(i).attr("src");
			if(src != null && src.contains("spotify.com")) 
			{
				if(src.startsWith("https://open.spotify.com/embed/album/"))
				{
					int ix = "https://open.spotify.com/embed/album/".length();
					return src.substring(ix, ix+22);
				}
				else if(src.startsWith("https://open.spotify.com/album/"))
				{
					int ix = "https://open.spotify.com/album/".length();
					return src.substring(ix, ix+22);
				}
				else if(src.startsWith("https://embed.spotify.com/?uri=spotify:album:"))
				{
					int ix = "https://embed.spotify.com/?uri=spotify:album:".length();
					return src.substring(ix, ix+22);
				}
				else if(src.startsWith("https://embed.spotify.com/?uri=spotify%3Aalbum%3A"))
				{
					int ix = "https://embed.spotify.com/?uri=spotify%3Aalbum%3A".length();
					return src.substring(ix, ix+22);
				}
				else if(src.startsWith("https://embed.spotify.com/?uri=https://open.spotify.com/album/"))
				{
					int ix = "https://embed.spotify.com/?uri=https://open.spotify.com/album/".length();
					return src.substring(ix, ix+22);
				}
			}
		}
		return null;
	}

	private Set<String> getYoutube(String url, Document doc) {
		Set<String> youtube = Sets.newHashSet();

		Elements elements = doc.select("iframe");
		for(int i = 0; i < elements.size(); i++)
		{
			String src = elements.get(i).attr("src");
			if(src != null && src.contains("youtube.com")) 
			{
				if(src.startsWith("https://www.youtube.com/embed/"))
				{
					int ix = "https://www.youtube.com/embed/".length();
					youtube.add(src.substring(ix));
				}
				else if(src.startsWith("//www.youtube.com/embed/"))
				{
					int ix = "//www.youtube.com/embed/".length();
					youtube.add(src.substring(ix));
				}
			}
		}
		return youtube;
	}

	private Float getVote(String url, Document doc) {
		try
		{
			switch (getType(url, doc)) {
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
					if(!"".equals(voteStr.trim()))
					{
						vote = format.parse(voteStr).floatValue();
					}
				}
				return vote;
			default:
				return 0F;
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getVote() "+ url + ": " + t.getMessage());
			return 0F;
		}
	}

	private Boolean getMilestone(String url, Document doc) {
		switch (getType(url, doc)) {
		case REVIEW:
			Boolean milestone = url.contains("pietremiliari");
			return milestone;
		default:
			return false;
		}
	}

	private TYPE getType(String url, Document doc) {
		if(url.startsWith(OndarockCrawler.ONDAROCK_URL + "pietremiliari") || 
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "recensioni"))
		{
			return TYPE.REVIEW;
		}
		else if(url.startsWith(OndarockCrawler.ONDAROCK_URL + "songwriter") || 
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "popmuzik") || 
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "altrisuoni") || 
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "rockedintorni") ||
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "dark") ||
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "italia") ||
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "jazz") ||
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "elettronica"))
		{
			return TYPE.MONOGRAPH;
		}
		return TYPE.UNKNOWN;
	}
}
