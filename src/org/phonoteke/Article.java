package org.phonoteke;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

public abstract class Article implements Serializable
{
	public static String SHA256 = "SHA-256";
	
	public static String UTF8 = "UTF-8";
	
	public enum TYPE 
	{
		MONOGRAPH,
		REVIEW
	}
	
	protected static Logger logger = LogManager.getLogger(Article.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = -7542577847645372670L;

	private String id;

	private String url;
	
	private String musicbrainzId;
	
	private String spotifyId;

	private TYPE type;

	private String band;

	private String album;

	private String cover;

	private String genre;

	private Integer year;

	private String label;

	private String content;

	private Float vote;

	private Boolean milestone;

	private Calendar creationDate;

	private String author;

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
	
	public String getMusicbrainzId() {
		return musicbrainzId;
	}

	public void setMusicbrainzId(String musicbrainzId) {
		this.musicbrainzId = musicbrainzId;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
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

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	//------------------------------------------------

	protected String getDocumentId(String url)
	{
		try 
		{
			MessageDigest sha256 = MessageDigest.getInstance(SHA256);
			byte[] digest = sha256.digest(url.getBytes(UTF8));
			return String.format("%0" + (digest.length * 2) + 'x', new BigInteger(1, digest));
		} 
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e) 
		{
			return null;
		} 
	}
	
	protected String getDocumentURL(String url)
	{
		try 
		{
			if(!url.startsWith(getBaseURL()))
			{
				url = new URL(new URL(getBaseURL()), url).toString();
			}
			url = url.replaceAll("\\.\\./", "");
			return url;
		} 
		catch (MalformedURLException e)
		{
			return null;
		} 
	}
	
	protected abstract String getBaseURL();
	protected abstract Boolean shouldVisit();
	protected abstract TYPE getDocumentType(String url);
	protected abstract String getDocumentBand(String url, Document doc);
	protected abstract String getDocumentAlbum(String url, Document doc);
	protected abstract String getDocumentMusicbrainzId(String url, Document doc);
	protected abstract String getDocumentSpotifyId(String url, Document doc);
	protected abstract String getDocumentContent(String url, Document doc);
	protected abstract Calendar getDocumentCreationDate(String url, Document doc);
	protected abstract String getDocumentCover(String url, Document doc);
	protected abstract String getDocumentAuthor(String url, Document doc);
	protected abstract String getDocumentGenre(String url, Document doc);
	protected abstract Integer getDocumentYear(String url, Document doc);
	protected abstract String getDocumentLabel(String url, Document doc);
	protected abstract Float getDocumentVote(String url, Document doc);
	protected abstract Boolean getDocumentMilestone(String url, Document doc);
	
}
