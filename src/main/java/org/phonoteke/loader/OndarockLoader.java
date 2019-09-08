package org.phonoteke.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.phonoteke.SpotifyLoader;
import org.phonoteke.model.ModelUtils;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

public class OndarockLoader extends PhonotekeLoader
{
	private static final String URL = "https://www.ondarock.it/";
	private static final String SOURCE = "ondarock";


	public OndarockLoader()
	{
		super();
	}

	protected String getBaseUrl()
	{
		return URL;
	}

	protected String getSource() 
	{
		return SOURCE;
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

	protected String getReview(String url, Document doc) 
	{
		try
		{
			Element content = doc.select("div[id=maintext]").first();
			removeComments(content);
			removeImages(content);
			removeScripts(content);
			removeDivs(content);
			//			getLinks(content, doc);

			InputStream is =  new ByteArrayInputStream(content.html().getBytes(StandardCharsets.UTF_8));
			return IOUtils.toString(is, StandardCharsets.UTF_8);
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getContent() "+ url + ": " + t.getMessage());
			return null;
		}
	}

	protected List<Map<String, String>> getLinks(String url, Document doc) 
	{
		List<Map<String, String>> links = Lists.newArrayList();
		Element node = doc.select("div[id=maintext]").first();
		Elements elements = node.select("a[href]");
		for(int i = 0; i < elements.size(); i++)
		{
			String link = getUrl(elements.get(i).attr("href"));
			if(url.startsWith(URL))
			{
				Map<String, String> map = Maps.newHashMap();
				map.put("source", "phonoteke");
				map.put("label", null);
				switch(getType(url))
				{
				case ARTIST:
					map.put("url", "/artists/" + getId(link));
					links.add(map);
					break;
				case ALBUM:
					map.put("url", "/albums/" + getId(link));
					links.add(map);
					break;
				default:
					break;
				}
			}
			elements.get(i).unwrap();
		}
		return links;
	}

	private void removeImages(Element node)
	{
		Elements elements = node.select("img");
		for(int i = 0; i < elements.size(); i++)
		{
			elements.get(i).remove();
		}
	}

	private void removeScripts(Element node) 
	{
		Elements elements = node.select("script");
		for(int i = 0; i < elements.size(); i++)
		{
			elements.get(i).remove();
		}
	}

	private void removeDivs(Element node) 
	{
		Elements elements = node.select("div");
		for(int i = 1; i < elements.size(); i++)
		{
			elements.get(i).unwrap();
		}
	}

	private void removeComments(Node node) 
	{
		for (int i = 0; i < node.childNodeSize(); i++) {
			Node child = node.childNode(i);
			if (child.nodeName().equals("#comment"))
				child.remove();
			else {
				removeComments(child);
			}
		}
	}

	protected String getArtist(String url, Document doc) 
	{
		Element intestazioneElement = null;
		Element bandElement = null;
		String band = null;
		switch (getType(url)) {
		case ALBUM:
			intestazioneElement = doc.select("div[id=intestazionerec]").first();
			bandElement = intestazioneElement.select("h1").first();
			band = bandElement.text().trim();
			return band;
		case ARTIST:
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

	protected String getTitle(String url, Document doc) 
	{
		Element intestazioneElement = null;
		Element titleElement = null;
		String title = null;
		switch (getType(url)) {
		case ALBUM:
			intestazioneElement = doc.select("div[id=intestazionerec]").first();
			titleElement = intestazioneElement.select("h2").first();
			title = titleElement.text().trim();
			return title;
		case ARTIST:
			intestazioneElement = doc.select("div[id=intestazione_OR3]").first();
			if(intestazioneElement == null)
			{
				intestazioneElement = doc.select("div[id=intestazione]").first();
			}
			titleElement = intestazioneElement.select("h3").first();
			title = titleElement.text().trim();
			return title;
		default:
			return null;
		}
	}

	protected String getDescription(String url, Document doc) 
	{
		Element descriptionElement = null;
		String description = null;
		switch (getType(url)) {
		case ALBUM:
			descriptionElement = doc.select("meta[property=og:description]").first();
			description = descriptionElement.attr("content").trim();
			return description;
		case ARTIST:
			descriptionElement = doc.select("meta[property=og:description]").first();
			description = descriptionElement.attr("content").trim();
			return description;
		default:
			return null;
		}
	}

	protected Date getDate(String url, Document doc) 
	{
		try
		{
			Date reviewDate = null;
			switch (getType(url)) {
			case ALBUM:
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
			case ARTIST:
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

	protected String getCover(String url, Document doc) 
	{
		try
		{
			Element coverElement = null;
			String cover = null;
			switch (getType(url)) {
			case ALBUM:
				coverElement = doc.select("div[id=cover_rec]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
			case ARTIST:
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

	protected List<String> getAuthors(String url, Document doc) 
	{
		Element authorElement = null;
		switch (getType(url)) {
		case ALBUM:
			authorElement = doc.select("div[class=recensorerec]").first();
			if(authorElement != null)
			{
				authorElement = authorElement.select("a[href]").first();
				if(authorElement != null)
				{
					return Lists.newArrayList(authorElement.html().trim().split(","));
				}
			}
		case ARTIST:
			authorElement = doc.select("span[class=recensore]").first();
			if(authorElement != null)
			{
				authorElement = authorElement.select("a[href]").first();
				if(authorElement != null)
				{
					return Lists.newArrayList(authorElement.html().trim().split(","));
				}
			}
		default:
			return null;
		}
	}

	protected List<String> getGenres(String url, Document doc) 
	{
		switch (getType(url)) {
		case ALBUM:
			Element datiElement = doc.select("div[id=dati]").first();
			String dati = datiElement.text();
			return Lists.newArrayList(dati.split("\\|")[1].trim().split(","));
		default:
			return null;
		}
	}

	protected Integer getYear(String url, Document doc) 
	{
		switch (getType(url)) {
		case ALBUM:
			Element datiElement = doc.select("div[id=dati]").first();
			String dati = datiElement.text();
			String yearStr = dati.split(" ")[0].trim();
			Integer year = Integer.parseInt(yearStr);
			return year;
		default:
			return 0;
		}
	}

	protected String getLabel(String url, Document doc) 
	{
		switch (getType(url)) {
		case ALBUM:
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

	private String getSpotify(String url, Document doc) 
	{
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

	protected List<Map<String, String>> getTracks(String url, Document doc) 
	{
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
					tracks.add(ModelUtils.newTrack(null, youtube));
					LOGGER.debug("tracks: youtube: " + youtube);
				}
				else if(src.startsWith("//www.youtube.com/embed/"))
				{
					int ix = "//www.youtube.com/embed/".length();
					youtube = src.substring(ix);
					tracks.add(ModelUtils.newTrack(null, youtube));
					LOGGER.debug("tracks: youtube: " + youtube);
				}
			}
		}
		return tracks;
	}

	protected Float getVote(String url, Document doc) 
	{
		try
		{
			switch (getType(url)) {
			case ALBUM:
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

	protected Boolean getMilestone(String url, Document doc) 
	{
		switch (getType(url)) {
		case ALBUM:
			return url.contains("pietremiliari");
		default:
			return false;
		}
	}

	protected TYPE getType(String url) 
	{
		if(getUrl(url).startsWith(URL + "pietremiliari") || 
				getUrl(url).startsWith(URL + "recensioni") ||
				getUrl(url).startsWith(URL + "jazz/recensioni"))
		{
			return TYPE.ALBUM;
		}
		else if(getUrl(url).startsWith(URL + "songwriter") || 
				getUrl(url).startsWith(URL + "popmuzik") || 
				getUrl(url).startsWith(URL + "altrisuoni") || 
				getUrl(url).startsWith(URL + "rockedintorni") ||
				getUrl(url).startsWith(URL + "dark") ||
				getUrl(url).startsWith(URL + "italia") ||
				getUrl(url).startsWith(URL + "jazz") ||
				getUrl(url).startsWith(URL + "elettronica"))
		{
			return TYPE.ARTIST;
		}
		return TYPE.UNKNOWN;
	}
}
