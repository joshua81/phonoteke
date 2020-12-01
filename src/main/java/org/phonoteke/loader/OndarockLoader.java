package org.phonoteke.loader;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;

public class OndarockLoader extends AbstractCrawler
{
	public static final String SOURCE = "ondarock";
	public static final String URL = "https://www.ondarock.it/";

	private static final Logger LOGGER = LogManager.getLogger(OndarockLoader.class);

	public static void main(String[] args) {
		//new OndarockLoader().load("https://www.ondarock.it/speciali/rockinonda_frenchtouch.htm");
		new OndarockLoader().load("https://www.ondarock.it/recensioni/2020-grantleephillips-lightningshowusyourstuff.htm");
	}

	@Override
	public void load(String url) 
	{
		url = url == null ? OndarockLoader.URL : url;
		crawl(url);
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return url.getURL().toLowerCase().startsWith(URL);
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
		switch (getType(url)) {
		case podcast:
			return null;
		default:
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
			removeIFrames(content);

			String review = content.html();
			if(StringUtils.isBlank(review) || review.trim().length() < 100)
			{
				throw new IllegalArgumentException("Empty review!");
			}
			return review;
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

	private void removeIFrames(Element node)
	{
		Elements elements = node.select("iframe");
		for(int i = 0; i < elements.size(); i++)
		{
			elements.get(i).remove();
		}
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
		case album:
			intestazioneElement = doc.select("div[id=intestazionerec]").first();
			bandElement = intestazioneElement.select("h1").first();
			band = bandElement.html().trim();
			return band;
		case artist:
		case concert:
		case interview:
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
			band = bandElement.html().trim();
			return band;
//		case podcast:
//			return getUrl(url).startsWith(URL + "speciali/blahblahblah") ? "Blah Blah Blah" :
//				getUrl(url).startsWith(URL + "speciali/rockinonda") ? "Rock in Onda" : null;
		default:
			return null;
		}
	}

	private Date getDate(String dateTxt)
	{
		String[] dates = dateTxt.replace("\\", "/").replace("-", "/").replace(")", "").replace("(", "").trim().split("/");
		int year = Integer.parseInt(dates[dates.length-1].trim());
		int month = Integer.parseInt(dates[dates.length-2].trim())-1;
		int day = Integer.parseInt(dates[dates.length-3].trim());
		Calendar date = Calendar.getInstance();
		date.set(year, month, day);
		return date.getTime();
	}

	@Override
	protected String getTitle(String url, Document doc) 
	{
		Element intestazioneElement = null;
		Element titleElement = null;
		String title = null;
		switch (getType(url)) {
		case album:
			intestazioneElement = doc.select("div[id=intestazionerec]").first();
			titleElement = intestazioneElement.select("h2").first();
			title = titleElement.html().trim();
			return title;
		case artist:
		case concert:
		case interview:
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
			title = titleElement.html().trim();
			return title;
		case podcast:
			intestazioneElement = doc.select("div[id=intestazione_int]").first();
			titleElement = intestazioneElement.select("h2").first();
			title = titleElement.html().trim();
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
		case album:
			descriptionElement = doc.select("meta[property=og:description]").first();
			description = descriptionElement.attr("content").trim();
			return description;
		case artist:
			descriptionElement = doc.select("meta[property=og:description]").first();
			description = descriptionElement.attr("content").trim();
			return description;
		case podcast:
			descriptionElement = doc.select("div[id=intestazione_int]").first();
			descriptionElement = descriptionElement.select("h3").first();
			description = descriptionElement.html().trim();
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
			case album:
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
			case concert:
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
			LOGGER.error("ERROR getCreationDate() "+ url + ": " + t.getMessage());
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
			case album:
				coverElement = doc.select("div[id=cover_rec]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
			case artist:
				coverElement = doc.select("div[id=col_right_mono]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
			case concert:
				coverElement = doc.select("div[class=fotolr]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
			case interview:
				coverElement = doc.select("div[class=article_foto_cont]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
			case podcast:
				coverElement = doc.select("meta[property=og:image]").first();
				cover = coverElement.attr("content").trim();
				return getUrl(cover);
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR getCover() "+ url + ": " + t.getMessage());
			return null;
		}
	}

	@Override
	protected List<String> getAuthors(String url, Document doc) 
	{
		Element authorElement = null;
		switch (getType(url)) {
		case album:
			authorElement = doc.select("div[class=recensorerec]").first();
			if(authorElement != null)
			{
				authorElement = authorElement.select("a[href]").first();
				if(authorElement != null)
				{
					return Lists.newArrayList(authorElement.html().trim().split(","));
				}
			}
		case artist:
		case concert:
		case interview:
		case podcast:
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
		case album:
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
		case album:
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
		case album:
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

	@Override
	protected List<org.bson.Document> getTracks(String url, Document doc) 
	{
		List<org.bson.Document> tracks = Lists.newArrayList();
		switch (getType(url)) {
		case album:
			Elements elements = doc.select("iframe");
			return getVideos(elements);
		case podcast:
			Element playlist = doc.select("div[id=boxdiscografia_head]").first();
			if("playlist".equalsIgnoreCase(playlist.text())) {
				Element content = doc.select("div[id=boxdiscografia_med]").first();
				return getTracks(content, SOURCE);
			}
			break;
		default:
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
			case album:
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
			LOGGER.error("ERROR getVote() "+ url + ": " + t.getMessage());
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
			return TYPE.album;
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
			return TYPE.artist;
		}
		else if(getUrl(url).startsWith(URL + "livereport"))
		{
			return TYPE.concert;
		}
		else if(getUrl(url).startsWith(URL + "interviste"))
		{
			return TYPE.interview;
		}
//		else if(getUrl(url).startsWith(URL + "speciali/blahblahblah") || 
//				getUrl(url).startsWith(URL + "speciali/rockinonda"))
//		{
//			return TYPE.podcast;
//		}
		return TYPE.unknown;
	}
}
