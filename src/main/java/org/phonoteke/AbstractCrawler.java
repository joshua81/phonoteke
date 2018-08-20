package org.phonoteke;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.DBObject;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public abstract class AbstractCrawler extends WebCrawler
{
	public enum TYPE {
		MONOGRAPH,
		REVIEW
	}
	
	protected static final String UTF_8 = "UTF-8";
	protected static final Pattern FILTERS = Pattern.compile(".*(\\.(htm|html))$");

	protected static final String SQL_FIND_DOCUMENT = "SELECT * FROM document WHERE url = ?";
	protected static final String SQL_INSERT_DOCUMENT = "INSERT INTO document ("
			+ "id, "
			+ "url, "
			+ "type, "
			+ "band, "
			+ "album, "
			+ "content, "
			+ "creation_date, "
			+ "cover, "
			+ "author, "
			+ "genre, "
			+ "year, "
			+ "label, "
			+ "vote, "
			+ "milestone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	protected static Logger logger = LogManager.getLogger(AbstractCrawler.class.getName());
	protected static Connection db = null;
	protected static PreparedStatement queryDocumentFind = null;
	protected static PreparedStatement queryDocumentInsert = null;
	protected static MessageDigest sha256 = null;
	
	protected String url;
	protected Document doc;
	
	static 
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			db = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/musicdb", "musicdb", "musicdb");
			queryDocumentFind = db.prepareStatement(SQL_FIND_DOCUMENT);
			queryDocumentInsert = db.prepareStatement(SQL_INSERT_DOCUMENT);
			sha256 = MessageDigest.getInstance("SHA-256");
		} 
		catch (Throwable t) 
		{
			logger.error("Error connecting to MySQL db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	protected String currentPage;

	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://www.ics.uci.edu/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) 
	{
		String dest = url.getURL().toLowerCase();
		return FILTERS.matcher(dest).matches() && dest.startsWith(getBaseURL());
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
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

				// check if the page must be crawled
				String url = page.getWebURL().getURL();
				if(!shouldVisit())
				{
					return;
				}

				currentPage = getDocumentURL(url.toLowerCase());

				// check if the page was already crawled
				queryDocumentFind.setString(1, currentPage);
				res = queryDocumentFind.executeQuery();
				if(res.next())
				{
					return;
				}
				logger.debug("Code: " + currentPage);

				// insert into DOCUMENT
				queryDocumentInsert.setString(1, getSHA256(currentPage));
				queryDocumentInsert.setString(2, currentPage);
				queryDocumentInsert.setString(3, getDocumentType().name());
				queryDocumentInsert.setString(4, getDocumentBand());
				queryDocumentInsert.setString(5, getDocumentAlbum());
				queryDocumentInsert.setAsciiStream(6, getDocumentContent());
				queryDocumentInsert.setDate(7, getDocumentCreationDate());
				queryDocumentInsert.setString(8, getDocumentCover());
				queryDocumentInsert.setString(9, getDocumentAuthor());
				queryDocumentInsert.setString(10, getDocumentGenre());
				queryDocumentInsert.setInt(11, getDocumentYear());
				queryDocumentInsert.setString(12, getDocumentLabel());
				queryDocumentInsert.setFloat(13, getDocumentVote());
				queryDocumentInsert.setBoolean(14, getDocumentMilestone());
				queryDocumentInsert.executeUpdate();
				logger.info("Document " + currentPage + " added into DOCUMENT");
			} 
			catch (Throwable t) 
			{
				logger.error("Error parsing page " + page.getWebURL().getURL() + ": " + t.getMessage());
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

	protected abstract String getId();
	protected abstract String getBaseURL();
	protected abstract Boolean shouldVisit();
	protected abstract TYPE getDocumentType();
	protected abstract String getDocumentBand();
	protected abstract String getDocumentAlbum();
	protected abstract Date getDocumentCreationDate();
	protected abstract String getDocumentCover();
	protected abstract String getDocumentAuthor();
	protected abstract String getDocumentGenre();
	protected abstract Integer getDocumentYear();
	protected abstract String getDocumentLabel();
	protected abstract Float getDocumentVote();
	protected abstract Boolean getDocumentMilestone();

	/**
	 * Converts relative urls to absolute urls 
	 * 
	 * @param url
	 * @return
	 */
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
		catch (Throwable t) 
		{
			return null;
		} 
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	protected InputStream getDocumentContent() {
		try
		{
			// remove all images
			Element contentElement = doc.select("div[id=maintext]").first();
			Elements imgElements = contentElement.select("img");
			for(int i = 0; i < imgElements.size(); i++)
			{
				imgElements.get(i).remove();
			}

			// replace links with the associated code inside the review text
			Elements linkElements = contentElement.select("a[href]");
			for(int i = 0; i < linkElements.size(); i++)
			{
				String link = linkElements.get(i).attr("href");
				link = getDocumentURL(link);
				linkElements.get(i).attr("href", "javascript:loadDocument('" + getSHA256(link) + "')");
				logger.debug("Link: " + link);
			}

			InputStream contentIs =  new ByteArrayInputStream(contentElement.html().getBytes(UTF_8));
			return contentIs;
		}
		catch(Throwable t)
		{
			return null;
		}
	}

	private String getSHA256(String string) throws UnsupportedEncodingException
	{
		byte[] digest = sha256.digest(string.getBytes(UTF_8));
		String sha = String.format("%0" + (digest.length * 2) + 'x', new BigInteger(1, digest));
		return sha;
	}
}
