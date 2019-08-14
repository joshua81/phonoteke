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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class OndarockLoader 
{
	private static final Logger LOGGER = LogManager.getLogger(OndarockLoader.class);

	public static final String MONGO_HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String MONGO_DB = "phonoteke";

	private MongoCollection<org.bson.Document> pages;
	private MongoCollection<org.bson.Document> articles;

	public enum TYPE {
		MONOGRAPH,
		REVIEW,
		UNKNOWN
	}

	public static void main(String[] args) 
	{
		OndarockLoader loader = new OndarockLoader();
		loader.deleteAlbums();
		loader.loadAlbums();
		loader.loadSpotifyIds();
	}

	public OndarockLoader()
	{
		try
		{
			MongoDatabase db = new MongoClient(MONGO_HOST, MONGO_PORT).getDatabase(MONGO_DB);
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
			SpotifyLoader spotify = new SpotifyLoader();
			MongoCursor<org.bson.Document> i = articles.find(Filters.and(Filters.eq("type", TYPE.REVIEW.name()), Filters.eq("spotify", null))).iterator();
			while(i.hasNext())
			{
				org.bson.Document page = i.next();
				String id = page.get("id", String.class);
				org.bson.Document album = spotify.getId(id);
				if(album != null)
				{
					String spotifyId = album.get("album", String.class);
					String cover = album.get("image300", String.class);
					articles.findOneAndUpdate(Filters.eq("id", page.get("id")), 
							new org.bson.Document("spotify", spotifyId).append("cover", cover));
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error closing BufferedReader: " + e.getMessage());
		}
	}

	private void deleteAlbums()
	{
		MongoCursor<org.bson.Document> i = articles.find(Filters.eq("content", "")).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			articles.findOneAndDelete(Filters.eq("url", url));
			pages.findOneAndDelete(Filters.eq("url", url));
			LOGGER.info("Deleted page " + url);
		}
	}

	private void loadAlbums()
	{
		//		DBCursor i = pages.find(BasicDBObjectBuilder.start().add("url", "https://www.ondarock.it/jazz/recensioni/1961_billevanstrio.htm").get());
		MongoCursor<org.bson.Document> i = pages.find(Filters.eq("source", "ondarock")).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			String html = page.get("page", String.class);

			// check if the article was already crawled
			MongoCursor<org.bson.Document> j = articles.find(Filters.and(
					Filters.eq("source", "ondarock"),
					Filters.eq("url", getUrl(url)))).iterator();
			if(!j.hasNext())
			{
				try
				{
					Document doc = Jsoup.parse(html);
					TYPE type = getType(url, doc);
					if(type != TYPE.UNKNOWN)
					{
						String id = getId(url);
						String band = getBand(url, doc);
						String album = getAlbum(url, doc);
						String spotify = getSpotify(url, doc);
						Set<String> youtube = getYoutube(url, doc);
						Date creationDate = getCreationDate(url, doc);
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
						if(StringUtils.isNotEmpty(content))
						{
							org.bson.Document json = new org.bson.Document("id", id).
									append("spotify", spotify).
									append("youtube", youtube).
									append("url", url).
									append("type", type.name()).
									append("content", content).
									append("band", band).
									append("album", album).
									append("creationDate", creationDate).
									append("cover", cover).
									append("authors", authors).
									append("genres", genres).
									append("label", label).
									append("year", year).
									append("vote", vote).
									append("milestone", milestone).
									append("source", source);
							articles.insertOne(json);
							LOGGER.info("Document " + url + " added");
						}
					} 
				}
				catch (Throwable t) 
				{
					LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
				}
			}
		}
	}

	private String getId(String url) {
		url = getUrl(url);
		return Hashing.sha256().hashString(url, StandardCharsets.UTF_8).toString();
	}

	private String getUrl(String url) {
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
			replaceLinks(content, doc);

			InputStream is =  new ByteArrayInputStream(content.html().getBytes(StandardCharsets.UTF_8));
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getContent() "+ url + ": " + t.getMessage());
			return null;
		}
	}

	private void replaceLinks(Element node, Document doc) {
		Elements elements = node.select("a[href]");
		for(int i = 0; i < elements.size(); i++)
		{
			String url = getUrl(elements.get(i).attr("href"));
			if(url.startsWith(OndarockCrawler.ONDAROCK_URL))
			{
				switch(getType(url, doc))
				{
				case MONOGRAPH:
					elements.get(i).attr("href", "/artists/" + getId(url));
					break;
				case REVIEW:
					elements.get(i).attr("href", "/albums/" + getId(url));
					break;
				default:
					elements.get(i).unwrap();
					break;
				}
			}
			else
			{
				elements.get(i).unwrap();
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
				return getUrl(cover);
			case MONOGRAPH:
				coverElement = doc.select("div[id=col_right_mono]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
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
				if(authorElement != null)
				{
					authors = Sets.newHashSet(authorElement.html().trim().split(","));
				}
			}
			return authors;
		case MONOGRAPH:
			authorElement = doc.select("span[class=recensore]").first();
			if(authorElement != null)
			{
				authorElement = authorElement.select("a[href]").first();
				if(authorElement != null)
				{
					authors = Sets.newHashSet(authorElement.html().trim().split(","));
				}
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
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "recensioni") ||
				url.startsWith(OndarockCrawler.ONDAROCK_URL + "jazz/recensioni"))
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
