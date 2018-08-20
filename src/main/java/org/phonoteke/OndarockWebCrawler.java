package org.phonoteke;

import java.sql.ResultSet;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class OndarockWebCrawler extends WebCrawler
{
	public static final Pattern FILTERS = Pattern.compile(".*(\\.(htm|html))$");
	public static final String BASE_URL = "http://www.ondarock.it/";
	
	public static final String MONGO_HOST = "localhost";
	public static final int MONGO_PORT = 27017;
	public static final String MONGO_DB = "phonoteke";
	
	private static Logger logger = LogManager.getLogger(PhonotekeCrawler.class.getName());
	
	private DBCollection articles;
	
	public OndarockWebCrawler()
	{
		super();
		try 
		{
			DB db = new MongoClient(MONGO_HOST, MONGO_PORT).getDB(MONGO_DB);
			articles = db.getCollection("articles");
		} 
		catch (Throwable t) 
		{
			logger.error("Error connecting to Mongo db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}
	
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
		return FILTERS.matcher(dest).matches() && dest.startsWith(BASE_URL);
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
				String url = page.getWebURL().getURL();
				
				// check if the page must be crawled
				OndarockCrawler article = OndarockCrawler.newInstance(url, doc);
				if(!article.shouldVisit())
				{
					return;
				}

				// check if the article was already crawled
				DBObject articleDB = articles.findOne(article.getId());
				if(articleDB != null)
				{
					return;
				}

				// insert into DOCUMENT
				DBObject json = (DBObject)JSON.parse(new ObjectMapper().writeValueAsString(article));
				json.put("_id", article.getId());
				articles.insert(json);
				logger.info("Document " + article.getBaseURL() + " added");
			} 
			catch (Throwable t) 
			{
				logger.error("Error parsing page " + page.getWebURL().getURL());
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
}
