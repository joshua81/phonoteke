package org.phonoteke.loader;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;

public class WorldWideFMLoader extends PodcastLoader
{
	private static final String ID = "120803";
	private static final String URL = "https://worldwidefm.net/";
	private static final String ARTIST = "Gilles Peterson";
	private static final String SOURCE = "wwfm";
	private static final List<String> AUTHORS = Lists.newArrayList("Gilles Peterson");

	private static final String JSON_EPISODES = "{\"operationName\":\"getRelatedEpisodes\",\"variables\":{\"id\":\"$ID\",\"offset\":0,\"limit\":12},\"query\":\"query getRelatedEpisodes($id: [QueryArgument], $offset: Int, $limit: Int) {\\n  entries(\\n    section: \\\"episode\\\"\\n    episodeCollection: $id\\n    offset: $offset\\n    limit: $limit\\n  ) {\\n    id\\n    title\\n    ... on episode_episode_Entry {\\n      description\\n      uri\\n      thumbnail {\\n        url @transform(width: 1200, height: 1200, immediately: true)\\n        __typename\\n      }\\n      genreTags {\\n        title\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
	private static final String JSON_EPISODE = "{\"operationName\":\"getEpisode\",\"variables\":{\"slug\":\"$EPISODE\"},\"query\":\"query getEpisode($slug: [String]) {\\n  entry(section: \\\"episode\\\", slug: $slug) {\\n    id\\n    title\\n    postDate @formatDateTime(format: \\\"d.m.y\\\")\\n    ... on episode_episode_Entry {\\n      broadcastDate @formatDateTime(format: \\\"d.m.y\\\")\\n      description\\n      uri\\n      genreTags {\\n        title\\n        slug\\n        __typename\\n      }\\n      thumbnail {\\n        url @transform(width: 1200, height: 1200, immediately: true)\\n        __typename\\n      }\\n      player\\n      tracklist\\n      bodyText\\n      episodeCollection {\\n        id\\n        title\\n        uri\\n        ... on collectionCategories_Category {\\n          thumbnail {\\n            url @transform(width: 1200, height: 1200, immediately: true)\\n            __typename\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";

	public static void main(String[] args) {
		new WorldWideFMLoader().load(args);
	}

	@Override
	public void load(String... args) 
	{
		WorldWideFMLoader.url = URL;
		WorldWideFMLoader.artist = ARTIST;
		WorldWideFMLoader.source = SOURCE;
		WorldWideFMLoader.authors = AUTHORS;

		LOGGER.info("Crawling " + WorldWideFMLoader.url);
		crawl(WorldWideFMLoader.url);
		updateLastEpisodeDate(WorldWideFMLoader.source);
	}

	@Override
	protected void crawl(String url)
	{
		try
		{
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url + "/cached_api");
			httpPost.setEntity(new StringEntity(JSON_EPISODES.replace("$ID", ID)));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			CloseableHttpResponse response = client.execute(httpPost);
			JsonObject gson = new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);
			JsonArray results = gson.get("data").getAsJsonObject().get("entries").getAsJsonArray();

			results.forEach(item -> {
				try
				{
					JsonObject doc = (JsonObject)item;
					CloseableHttpClient client2 = HttpClients.createDefault();
					HttpPost httpPost2 = new HttpPost(url + "/api");
					httpPost2.setEntity(new StringEntity(JSON_EPISODE.replace("$EPISODE", doc.get("uri").getAsString().substring(8))));
					httpPost2.setHeader("Accept", "application/json");
					httpPost2.setHeader("Content-type", "application/json");

					CloseableHttpResponse response2 = client2.execute(httpPost2);
					JsonObject gson2 = new Gson().fromJson(new InputStreamReader(response2.getEntity().getContent()), JsonObject.class);
					doc = gson2.get("data").getAsJsonObject().get("entry").getAsJsonObject();

					String pageUrl = URL + doc.get("uri").getAsString();
					TYPE type = TYPE.podcast;

					LOGGER.debug("Parsing page " + pageUrl);
					String id = getId(pageUrl);
					String title = doc.get("title").getAsString();

					org.bson.Document json = docs.find(Filters.and(Filters.eq("source", source), 
							Filters.eq("url", pageUrl))).iterator().tryNext();
					if(json == null)
					{
						try {
							json = new org.bson.Document("id", id).
									append("url", getUrl(pageUrl)).
									append("type", type.name()).
									append("artist", artist).
									append("title", title).
									append("authors", authors).
									append("cover", doc.get("thumbnail").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString()).
									append("date", getDate(doc.get("broadcastDate").getAsString())).
									append("description", getDescription(doc)).
									append("genres", null).
									append("label", null).
									append("links", null).
									append("review", null).
									append("source", source).
									append("vote", null).
									append("year", getYear(doc.get("broadcastDate").getAsString())).
									append("tracks", getTracks(doc.get("tracklist").getAsString())).
									append("audio", getAudio(doc));

							docs.insertOne(json);
							LOGGER.info(json.getString("type") + " " + pageUrl + " added");
						}
						catch(Exception e) {
							LOGGER.error("ERROR parsing page " + pageUrl + ": " + e.getMessage());
						}
					}
				}
				catch (Throwable t) 
				{
					LOGGER.error("ERROR parsing page " + url + ": " + t.getMessage());
					t.printStackTrace();
					throw new RuntimeException(t);
				}
			});
			client.close();
		}
		catch (Throwable t) 
		{
			LOGGER.error("ERROR parsing page " + url + ": " + t.getMessage());
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	private Date getDate(String date) 
	{
		try {
			return new SimpleDateFormat("dd.MM.yy").parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	private Integer getYear(String date) 
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDate(date));
		return cal.get(Calendar.YEAR);
	}

	private String getDescription(JsonObject doc) {
		return cleanHTML(doc.get("description").getAsString());
	}

	private String getAudio(JsonObject doc) {
		return doc.get("player").isJsonNull() ? null : "https://www.mixcloud.com" + doc.get("player").getAsString();
	}

	private List<org.bson.Document> getTracks(String content) {
		List<org.bson.Document> tracks = Lists.newArrayList();

		String[] chunks = cleanHTML(content).split("\n");
		for(int i = 0; i < chunks.length; i++)
		{
			String title = chunks[i].trim();
			if(StringUtils.isNotBlank(title) && isTrack(title))
			{
				tracks.add(newTrack(title, null));
				LOGGER.debug("tracks: " + title);
			}
		}
		return checkTracks(tracks);
	}
}
