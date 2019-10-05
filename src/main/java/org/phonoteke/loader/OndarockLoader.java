package org.phonoteke.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;

public class OndarockLoader extends PhonotekeLoader
{
	public static final String URL = "https://www.ondarock.it/";
	public static final String SOURCE = "ondarock";


	public OndarockLoader()
	{
		super();
	}

	@Override
	protected String getBaseUrl()
	{
		return URL;
	}

	@Override
	protected String getSource() 
	{
		return SOURCE;
	}

	@Override
	protected String getReview(String url, Document doc) 
	{
		try
		{

			Element content = doc.select("div[id=maintext]").first();
			if(content == null)
			{
				content = doc.select("div[id=maintext2]").first();
			}
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
			LOGGER.error("Error getContent() "+ url + ": " + t.getMessage(), t);
			return null;
		}
	}

	@Override
	protected List<String> getLinks(String url, Document doc) 
	{
		Set<String> links = Sets.newHashSet();
		Element node = doc.select("div[id=maintext]").first();
		if(node == null)
		{
			node = doc.select("div[id=maintext2]").first();
		}
		Elements elements = node.select("a[href]");
		for(int i = 0; i < elements.size(); i++)
		{
			String link = getUrl(elements.get(i).attr("href"));
			if(link.startsWith(URL))
			{
				links.add(getId(link));
			}
			elements.get(i).unwrap();
		}
		return Lists.newArrayList(links);
	}

	private void removeLinks(Element node)
	{
		Elements elements = node.select("a");
		for(int i = 0; i < elements.size(); i++)
		{
			elements.get(i).remove();
		}
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

	@Override
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
		case CONCERT:
			intestazioneElement = doc.select("div[id=intestazione_OR3]").first();
			if(intestazioneElement == null)
			{
				intestazioneElement = doc.select("div[id=intestazione]").first();
			}
			if(intestazioneElement == null)
			{
				intestazioneElement = doc.select("div[id=intestazione_int]").first();
			}
			bandElement = intestazioneElement.select("h2").first();
			band = bandElement.text().trim();
			return band;
		default:
			return null;
		}
	}

	private Date getDate(String dateTxt)
	{
		String[] dates = dateTxt.replace("-", "/").replace(")", "").replace("(", "").trim().split("/");
		int y = Integer.parseInt(dates[dates.length-1].trim());
		int m = Integer.parseInt(dates[dates.length-2].trim());
		int d = Integer.parseInt(dates[dates.length-3].trim());
		Calendar date = Calendar.getInstance();
		date.set(y, m, d);
		return date.getTime();
	}

	@Override
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
		case CONCERT:
			intestazioneElement = doc.select("div[id=intestazione_OR3]").first();
			if(intestazioneElement == null)
			{
				intestazioneElement = doc.select("div[id=intestazione]").first();
			}
			if(intestazioneElement == null)
			{
				intestazioneElement = doc.select("div[id=intestazione_int]").first();
			}
			titleElement = intestazioneElement.select("h3").first();
			title = titleElement.text().trim();
			return title;
		default:
			return null;
		}
	}

	@Override
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

	@Override
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
					reviewDate = getDate(reviewDateElement.text());
				}
				if(reviewDate == null && getYear(url, doc) != null)
				{
					reviewDate = getDate("01/01/" + getYear(url, doc));
				}
				return reviewDate;
			case CONCERT:
				reviewElement = doc.select("div[id=intestazione_OR3]").first();
				if(reviewElement == null)
				{
					reviewElement = doc.select("div[id=intestazione]").first();
				}
				if(reviewElement == null)
				{
					reviewElement = doc.select("div[id=intestazione_int]").first();
				}
				reviewElement = reviewElement.select("h4").first();
				reviewDate = getDate(reviewElement.text());
				return reviewDate;
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getCreationDate() "+ url + ": " + t.getMessage(), t);
			return null;
		}
	}

	@Override
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
			case CONCERT:
				coverElement = doc.select("div[class=fotolr]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("Error getCover() "+ url + ": " + t.getMessage(), t);
			return null;
		}
	}

	@Override
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
		case CONCERT:
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

	@Override
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

	@Override
	protected Integer getYear(String url, Document doc) 
	{
		switch (getType(url)) {
		case ALBUM:
			Element datiElement = doc.select("div[id=dati]").first();
			if(datiElement != null)
			{
				String yearStr = datiElement.text().split(" ")[0].trim();
				Integer year = Integer.parseInt(yearStr);
				return year;
			}
		default:
			return 0;
		}
	}

	@Override
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

	@Override
	protected List<org.bson.Document> getTracks(String url, Document doc) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		switch (getType(url)) {
		case ALBUM:
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
						tracks.add(newTrack(null, youtube));
						LOGGER.debug("tracks: youtube: " + youtube);
					}
					else if(src.startsWith("//www.youtube.com/embed/"))
					{
						int ix = "//www.youtube.com/embed/".length();
						youtube = src.substring(ix);
						tracks.add(newTrack(null, youtube));
						LOGGER.debug("tracks: youtube: " + youtube);
					}
				}
			}
			break;
		case CONCERT:
			Element content = doc.select("div[id=boxdiscografia_med]").first();
			if(content != null && content.children() != null)
			{
				Iterator<Element> i = content.children().iterator();
				while(i.hasNext())
				{
					String title = i.next().text().trim();
					if(StringUtils.isNoneBlank(title))
					{
						tracks.add(newTrack(title, null));
						LOGGER.debug("tracks: " + title + ", youtube: " + null);
					}
				}
			}
			break;
		}
		return tracks;
	}

	@Override
	protected Float getVote(String url, Document doc) 
	{
		try
		{
			switch (getType(url)) {
			case ALBUM:
				if(url.contains("pietremiliari"))
				{
					return 10F;
				}

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
			LOGGER.error("Error getVote() "+ url + ": " + t.getMessage(), t);
			return 0F;
		}
	}

	@Override
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
		else if(getUrl(url).startsWith(URL + "livereport"))
		{
			return TYPE.CONCERT;
		}
		return TYPE.UNKNOWN;
	}
}
