package org.phonoteke.loader;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.phonoteke.loader.HumanBeatsUtils.TYPE;

import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OndarockCrawler extends AbstractCrawler
{
	private static final String SOURCE = "ondarock";
	private static final String URL = "https://www.ondarock.it/";


	public void load(String... args) 
	{
		crawl(URL);
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		return url.getURL().toLowerCase().startsWith(URL);
	}

	@Override
	public void visit(Page page) 
	{
		if(page.getWebURL().getURL().endsWith(".htm") || page.getWebURL().getURL().endsWith(".html")) {
			super.visit(page);
		}
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
		case unknown:
			return null;
		default:
			Element content = doc.select("div[class=main_text]").first();
			if(content == null)
			{
				content = doc.select("div[class=main_text2]").first();
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
		Element node = doc.select("div[class=main_text]").first();
		if(node == null)
		{
			node = doc.select("div[class=main_text2]").first();
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
	protected String getArtist(String url, Document doc) 
	{
		Element intestazioneElement = null;
		Element bandElement = null;
		String band = null;
		switch (getType(url)) {
		case album:
			intestazioneElement = doc.select("div[class=titolo]").first();
			//<h1>artist</h1><h2>title</h2>
			if(intestazioneElement.select("h2").first() != null && 
					!intestazioneElement.select("h2").first().html().trim().startsWith(":")) {
				bandElement = intestazioneElement.select("h1").first();
				band = bandElement.html().trim();
				return band;
			}
			//<h1>artist - title</h1>
			else {
				bandElement = intestazioneElement.select("h1").first();
				band = bandElement.html().trim();
				return band.split("-")[0].trim();
			}
		default:
			return null;
		}
	}

	@Override
	protected String getTitle(String url, Document doc) 
	{
		Element intestazioneElement = null;
		Element titleElement = null;
		String title = null;
		switch (getType(url)) {
		case album:
			intestazioneElement = doc.select("div[class=titolo]").first();
			// <h1>artist</h1><h2>title</h2>
			if(intestazioneElement.select("h2").first() != null &&
					!intestazioneElement.select("h2").first().html().trim().startsWith(":")) {
				titleElement = intestazioneElement.select("h2").first();
				title = titleElement.html().trim();
				return title;
			}
			//<h1>artist - title</h1>
			else {
				titleElement = intestazioneElement.select("h1").first();
				title = titleElement.html().trim();
				return title.split("-")[1].trim();
			}
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
				Element reviewElement = doc.select("div[class=main_text]").first();
				Element reviewDateElement = reviewElement.select("p[class=data_recensione]").last();
				if(reviewDateElement != null)
				{
					reviewDate = getDate(reviewDateElement.text());
				}
				if(reviewDate == null && getYear(url, doc) != null)
				{
					reviewDate = getDate("01/01/" + getYear(url, doc));
				}
				return reviewDate;
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			log.error("ERROR getCreationDate() "+ url + ": " + t.getMessage());
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
				coverElement = doc.select("div[class=copertina]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				return getUrl(cover);
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			log.error("ERROR getCover() "+ url + ": " + t.getMessage());
			return null;
		}
	}

	@Override
	protected List<String> getAuthors(String url, Document doc) 
	{
		Element authorElement = null;
		switch (getType(url)) {
		case album:
			authorElement = doc.select("span[class=nome_recensore]").first();
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
			Element datiElement = doc.select("div[class=genere cell small-6]").first();
			String dati = datiElement.text();
			return Lists.newArrayList(dati.trim().split(","));
		default:
			return null;
		}
	}

	@Override
	protected Integer getYear(String url, Document doc) 
	{
		switch (getType(url)) {
		case album:
			Element datiElement = doc.select("div[class=anno_etichetta cell small-6]").first();
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
			Element datiElement = doc.select("div[class=anno_etichetta cell small-6]").first();
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
				if(url.contains("pietremiliari")) {
					return 10F;
				}

				DecimalFormatSymbols symbols = new DecimalFormatSymbols();
				symbols.setDecimalSeparator('.');
				DecimalFormat format = new DecimalFormat("##.#");
				format.setDecimalFormatSymbols(symbols);

				Float vote = 0F;
				Element voteElement = doc.select("span[class=voto]").first();
				if(voteElement == null) {
					voteElement = doc.select("span[class=voto red]").first();
				}
				if(voteElement != null) {
					String voteStr = voteElement.text();
					vote = format.parse(voteStr).floatValue();
				}
				return vote;
			default:
				return 0F;
			}
		}
		catch(Throwable t)
		{
			log.error("ERROR getVote() "+ url + ": " + t.getMessage());
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
		return TYPE.unknown;
	}
}
