package org.phonoteke;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class OndarockCrawler extends AbstractCrawler
{
	public static final String ONDAROCK_URL = "http://www.ondarock.it/";

	@Override
	protected String getBaseURL() {
		return ONDAROCK_URL;
	}

	@Override
	protected String getDocumentBand() {
		Element intestazioneElement = null;
		Element bandElement = null;
		String band = null;
		switch (getDocumentType()) {
		case REVIEW:
			intestazioneElement = doc.select("div[id=intestazionerec]").first();
			bandElement = intestazioneElement.select("h1").first();
			band = bandElement.text().trim();
			logger.debug("Band: " + band);
			return band;
		case MONOGRAPH:
			intestazioneElement = doc.select("div[id=intestazione_OR3]").first();
			if(intestazioneElement == null)
			{
				intestazioneElement = doc.select("div[id=intestazione]").first();
			}
			bandElement = intestazioneElement.select("h2").first();
			band = bandElement.text().trim();
			logger.debug("Band: " + band);
			return band;
		default:
			return null;
		}
	}

	@Override
	protected String getDocumentAlbum() {
		Element intestazioneElement = null;
		Element albumElement = null;
		String album = null;
		switch (getDocumentType()) {
		case REVIEW:
			intestazioneElement = doc.select("div[id=intestazionerec]").first();
			albumElement = intestazioneElement.select("h2").first();
			album = albumElement.text().trim();
			logger.debug("Album: " + album);
			return album;
		case MONOGRAPH:
			intestazioneElement = doc.select("div[id=intestazione_OR3]").first();
			if(intestazioneElement == null)
			{
				intestazioneElement = doc.select("div[id=intestazione]").first();
			}
			albumElement = intestazioneElement.select("h3").first();
			album = albumElement.text().trim();
			logger.debug("Album: " + album);
			return album;
		default:
			return null;
		}
	}

	@Override
	protected Date getDocumentCreationDate() {
		try
		{
			Date reviewDate = null;
			switch (getDocumentType()) {
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
					java.util.Date date = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + getDocumentYear());
					reviewDate = new Date(date.getTime());
				}
				logger.debug("Review date: " + reviewDate);
				return reviewDate;
			case MONOGRAPH:
				reviewDate = new Date(Calendar.getInstance().getTime().getTime());
				logger.debug("Review date: " + reviewDate);
				return reviewDate;
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			return null;
		}
	}

	@Override
	protected String getDocumentCover() {
		try
		{
			Element coverElement = null;
			String cover = null;
			switch (getDocumentType()) {
			case REVIEW:
				coverElement = doc.select("div[id=cover_rec]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				logger.debug("Cover: " + cover);
				return getDocumentURL(cover);
			case MONOGRAPH:
				coverElement = doc.select("div[id=col_right_mono]").first();
				coverElement = coverElement.select("img[src]").first();
				cover = coverElement.attr("src");
				logger.debug("Cover: " + cover);
				return getDocumentURL(cover);
			default:
				return null;
			}
		}
		catch(Throwable t)
		{
			return null;
		}
	}

	@Override
	protected String getDocumentAuthor() {
		String author = null;
		Element authorElement = null;
		switch (getDocumentType()) {
		case REVIEW:
			authorElement = doc.select("div[class=recensorerec]").first();
			if(authorElement != null)
			{
				authorElement = authorElement.select("a[href]").first();
				author = authorElement.html();
			}
			logger.debug("Author: " + author);
			return author;
		case MONOGRAPH:
			authorElement = doc.select("span[class=recensore]").first();
			if(authorElement != null)
			{
				authorElement = authorElement.select("a[href]").first();
				author = authorElement.html();
			}
			logger.debug("Author: " + author);
			return author;
		default:
			return null;
		}
	}

	@Override
	protected String getDocumentGenre() {
		switch (getDocumentType()) {
		case REVIEW:
			Element datiElement = doc.select("div[id=dati]").first();
			String dati = datiElement.text();
			String genres = dati.split("\\|")[1].trim();
			logger.debug("Genres: " + genres);
			return genres;
		default:
			return null;
		}
	}

	@Override
	protected Integer getDocumentYear() {
		switch (getDocumentType()) {
		case REVIEW:
			Element datiElement = doc.select("div[id=dati]").first();
			String dati = datiElement.text();
			String yearStr = dati.split(" ")[0].trim();
			Integer year = Integer.parseInt(yearStr);
			logger.debug("Year: " + year);
			return year;
		default:
			return 0;
		}
	}

	@Override
	protected String getDocumentLabel() {
		switch (getDocumentType()) {
		case REVIEW:
			Element datiElement = doc.select("div[id=dati]").first();
			String dati = datiElement.text();
			dati = datiElement.text();
			dati = dati.split("\\(")[1].trim();
			String label = dati.split("\\)")[0].trim();
			logger.debug("Label: " + label);
			return label;
		default:
			return null;
		}
	}

	@Override
	protected Float getDocumentVote() {
		try
		{
			switch (getDocumentType()) {
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
				logger.debug("Vote: " + vote);
				return vote;
			default:
				return 0F;
			}
		}
		catch(Throwable t)
		{
			return 0F;
		}
	}

	@Override
	protected Boolean getDocumentMilestone() {
		switch (getDocumentType()) {
		case REVIEW:
			Boolean milestone = url.contains("pietremiliari");
			logger.debug("Milestone: " + milestone);
			return milestone;
		default:
			return false;
		}
	}

	@Override
	protected AbstractCrawler.TYPE getDocumentType() {
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
				url.startsWith(ONDAROCK_URL + "elettronica"))
		{
			return TYPE.MONOGRAPH;
		}
		return null;
	}

	@Override
	protected Boolean shouldVisit() {
		return getDocumentType() != null;
	}

	@Override
	protected String getId() {
		// TODO
		return null;
	}

	public static OndarockCrawler newInstance(String url, Document doc) {
		OndarockCrawler article = new OndarockCrawler();
		article.url = url;
		article.doc = doc;
		return article;
	}
}