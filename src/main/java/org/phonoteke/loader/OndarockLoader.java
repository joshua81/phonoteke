package org.phonoteke.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.phonoteke.SpotifyLoader;
import org.phonoteke.crawler.OndarockCrawler;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class OndarockLoader extends PhonotekeLoader
{
	public enum TYPE {
		MONOGRAPH,
		REVIEW,
		UNKNOWN
	}

	public OndarockLoader()
	{
		super();
	}
	
	public void load() 
	{
		delete();
		loadAlbumsAndArtists();
		loadSpotifyIds();
	}

	private void loadSpotifyIds()
	{
		try 
		{
			SpotifyLoader spotify = new SpotifyLoader();
			MongoCursor<org.bson.Document> i = albums.find(Filters.eq("spotify", null)).iterator();
			while(i.hasNext())
			{
				org.bson.Document page = i.next();
				String id = page.get("id", String.class);
				org.bson.Document album = spotify.getId(id);
				if(album != null)
				{
					String spotifyId = album.get("album", String.class);
					String cover = album.get("image300", String.class);
					albums.findOneAndUpdate(Filters.eq("id", page.get("id")), 
							new org.bson.Document("spotify", spotifyId).append("cover", cover));
				}
			}
		}
		catch (Exception e) 
		{
			LOGGER.error("Error closing BufferedReader: " + e.getMessage());
		}
	}

	private void delete()
	{
		MongoCursor<org.bson.Document> i = albums.find(Filters.eq("content", "")).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			albums.findOneAndDelete(Filters.eq("url", url));
			pages.findOneAndDelete(Filters.eq("url", url));
			LOGGER.info("Deleted page " + url);
		}
	}

	private void loadAlbumsAndArtists()
	{
		//		DBCursor i = pages.find(BasicDBObjectBuilder.start().add("url", "https://www.ondarock.it/jazz/recensioni/1961_billevanstrio.htm").get());
		MongoCursor<org.bson.Document> i = pages.find(Filters.eq("source", "ondarock")).iterator();
		while(i.hasNext())
		{
			org.bson.Document page = i.next();
			String url = page.get("url", String.class);
			String html = page.get("page", String.class);
			Document doc = Jsoup.parse(html);
			TYPE type = getType(url, doc);

			try
			{
				// Album
				if(TYPE.REVIEW.equals(type))
				{
					MongoCursor<org.bson.Document> j = albums.find(Filters.and(
							Filters.eq("source", "ondarock"),
							Filters.eq("url", getUrl(url)))).iterator();
					if(!j.hasNext())
					{
						String id = getId(url);
						String idsptf = getSpotify(url, doc);
						String artist = getArtist(url, doc);
						String title = getTitle(url, doc);
						List<Map<String,String>> tracks = getTracks(url, doc);
						Date date = getDate(url, doc);
						String cover = getCover(url, doc);
						Set<String> authors = getAuthors(url, doc);
						Set<String> genres = getGenres(url, doc);
						Integer year = getYear(url, doc);
						String label = getLabel(url, doc);
						Float vote = getVote(url, doc);
						Boolean milestone = getMilestone(url, doc);
						String review = getContent(url, doc);

						if(StringUtils.isNotEmpty(review))
						{
							org.bson.Document json = new org.bson.Document("id", id).
									append("idsptf", idsptf).
									append("url", url).
									append("artist", artist).
									append("title", title).
									append("tracks", tracks).
									append("date", date).
									append("cover", cover).
									append("authors", authors).
									append("genres", genres).
									append("label", label).
									append("year", year).
									append("vote", vote).
									append("milestone", milestone).
									append("review", review).
									append("source", "ondarock");
							albums.insertOne(json);
							LOGGER.info("Album " + url + " added");
						}
					}
				}
				else if(TYPE.MONOGRAPH.equals(type))
				{
					MongoCursor<org.bson.Document> j = artists.find(Filters.and(
							Filters.eq("source", "ondarock"),
							Filters.eq("url", getUrl(url)))).iterator();
					if(!j.hasNext())
					{
						String id = getId(url);
						String artist = getArtist(url, doc);
						String desc = getTitle(url, doc);
						String cover = getCover(url, doc);
						Set<String> authors = getAuthors(url, doc);
						String review = getContent(url, doc);

						if(StringUtils.isNotEmpty(review))
						{
							org.bson.Document json = new org.bson.Document("id", id).
									append("url", url).
									append("review", review).
									append("artist", artist).
									append("description", desc).
									append("cover", cover).
									append("authors", authors).
									append("source", "ondarock");
							artists.insertOne(json);
							LOGGER.info("Artist " + url + " added");
						}
					}
				}
			}
			catch (Throwable t) 
			{
				LOGGER.error("Error parsing page " + url + ": " + t.getMessage());
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
				url = new URL(new URL(OndarockCrawler.URL), url).toString();
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
			if(url.startsWith(OndarockCrawler.URL))
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

	private String getArtist(String url, Document doc) {
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

	private String getTitle(String url, Document doc) {
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

	private Date getDate(String url, Document doc) {
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

	private List<Map<String, String>> getTracks(String url, Document doc) {
		List<Map<String, String>> tracks = Lists.newArrayList();
		Elements elements = doc.select("iframe");
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
				}
				else if(src.startsWith("//www.youtube.com/embed/"))
				{
					int ix = "//www.youtube.com/embed/".length();
					youtube = src.substring(ix);
				}
				LOGGER.info("tracks: youtube: " + youtube);
				Map<String, String> map = Maps.newHashMap();
				map.put("youtube", youtube);
				tracks.add(map);
			}
		}
		return tracks;
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
		if(url.startsWith(OndarockCrawler.URL + "pietremiliari") || 
				url.startsWith(OndarockCrawler.URL + "recensioni") ||
				url.startsWith(OndarockCrawler.URL + "jazz/recensioni"))
		{
			return TYPE.REVIEW;
		}
		else if(url.startsWith(OndarockCrawler.URL + "songwriter") || 
				url.startsWith(OndarockCrawler.URL + "popmuzik") || 
				url.startsWith(OndarockCrawler.URL + "altrisuoni") || 
				url.startsWith(OndarockCrawler.URL + "rockedintorni") ||
				url.startsWith(OndarockCrawler.URL + "dark") ||
				url.startsWith(OndarockCrawler.URL + "italia") ||
				url.startsWith(OndarockCrawler.URL + "jazz") ||
				url.startsWith(OndarockCrawler.URL + "elettronica"))
		{
			return TYPE.MONOGRAPH;
		}
		return TYPE.UNKNOWN;
	}
}
