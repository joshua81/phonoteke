package org.phonoteke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PhonotekeConcert {

	protected static final Logger LOGGER = LogManager.getLogger(AbstractCrawler.class.getName());

	protected static final String SQL_FIND_BANDS = "SELECT DISTINCT bandId FROM musicdb.document WHERE bandId is not null";
	protected static final String SQL_ADD_EVENT = "INSERT INTO musicdb.event (bandId, name, date, location) VALUES (?, ?, ?, ?)";

	protected static Connection db = null;
	protected static PreparedStatement queryFindBands = null;
	protected static PreparedStatement queryAddEvent = null;

	static 
	{
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			db = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/musicdb", "musicdb", "musicdb");
			queryFindBands = db.prepareStatement(SQL_FIND_BANDS);
			queryAddEvent = db.prepareStatement(SQL_ADD_EVENT);
		} 
		catch (Throwable t) 
		{
			LOGGER.error("Error connecting to MySQL db: " + t.getMessage());
			throw new RuntimeException(t);
		}
	}

	public static void main(String[] args) {
		findReleases();
	}

	private static void findReleases()
	{
		BufferedReader rd = null;
		try
		{
			ResultSet bands = queryFindBands.executeQuery();
			while(bands.next())
			{
//				String artist = bands.getString("band");
				String mbid = bands.getString("bandId");
				String urlString = "http://api.songkick.com/api/3.0/artists/mbid:" + mbid + "/calendar.json?apikey=1hOiIfT9pFTkyVkg";
				try
				{
					String result = "";
					String line = null;
					HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
					rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
					while ((line = rd.readLine()) != null) 
					{
						result += line;
					}

					JsonNode json = new ObjectMapper().readTree(result);
					JsonNode events = json.get("resultsPage").get("results").get("event");
					if(events == null || events.size() == 0)
					{
						LOGGER.info(mbid + ": event not found");
					}
					else
					{
						for(int i = 0; i < events.size(); i++)
						{
							String name = events.get(i).get("displayName").textValue();
							String dateStr = events.get(i).get("start").get("date").textValue();
							Date date = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateStr).getTime());
							String location = events.get(i).get("location").get("city").textValue();
							
							queryAddEvent.setString(1, mbid);
							queryAddEvent.setString(2, name);
							queryAddEvent.setDate(3, date);
							queryAddEvent.setString(4, location);
							queryAddEvent.executeUpdate();
							LOGGER.info(mbid + ": " + name + "@" + location);
						}
					}
				}
				catch(IOException e)
				{
					LOGGER.error("ERROR searching for " + mbid + " event: " + e.getMessage());
				}

				// To prevent MusicBrainz '503 Service Unavailable error'
				Thread.sleep(5000);
			}
		}
		catch(Throwable t)
		{
			LOGGER.error("ERROR: " + t.getMessage());
		}
		finally
		{
			if(rd != null)
			{
				try 
				{
					rd.close();
				} 
				catch (Throwable t) 
				{
					// do nothing
				}
			}
		}
	}
}
