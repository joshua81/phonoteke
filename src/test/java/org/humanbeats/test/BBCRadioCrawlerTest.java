package org.humanbeats.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.collections4.CollectionUtils;
import org.humanbeats.crawler.BBCRadioCrawler;
import org.humanbeats.crawler.NTSCrawler;
import org.humanbeats.model.HBDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * Test class for BBCRadioCrawler
 * Tests the core functionality of crawling BBC episodes and extracting playlists
 */
@Slf4j
public class BBCRadioCrawlerTest {

	private static final String TEST_EPISODE_URL = "https://www.nts.live/api/v2/shows/coco-maria/episodes/coco-maria-29th-may-2025";

	@BeforeEach
	void setUp() {
		// nothing to do
	}

	@AfterEach
	void tearDown() {
		// Cleanup is handled automatically by the crawler
	}

	@Test
	void testCrawlEpisode() {
		try {
			HttpURLConnection con = (HttpURLConnection)new URL(TEST_EPISODE_URL).openConnection();
			JsonObject doc = new Gson().fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
			BBCRadioCrawler crawler = new BBCRadioCrawler();
			HBDocument result = crawler.crawlDocument(TEST_EPISODE_URL, doc);

			// Verify basic structure
			assertNotNull(result, "Episode data should not be null");
			assertNotNull(result.getId(), "Episode id should not be null");
			assertTrue(CollectionUtils.isNotEmpty(result.getTracks()), "Tracks list should not be null");
			assertEquals(TEST_EPISODE_URL, result.getUrl(), "Episode URL should match");

			// Verify episode metadata
			assertNotNull(result.getTitle(), "Episode title should not be null");
			assertNotNull(result.getDescription(), "Episode description should not be null");
			assertNotNull(result.getDate(), "Episode date should not be null");
			assertNotNull(result.getCover(), "Episode cover should not be null");
			assertNotNull(result.getAudio(), "Episode audio should not be null");
			assertNotNull(result.getYear(), "Episode year should not be null");
			assertTrue(CollectionUtils.isNotEmpty(result.getTracks()), "Episode tracks should not be empty");

			log.info("Episode Id: " + result.getId());
			log.info("Episode Url: " + result.getUrl());
			log.info("Episode Title: " + result.getTitle());
			log.info("Episode Description: " + result.getDescription());
			log.info("Episode Date: " + result.getDate());
			log.info("Episode Cover: " + result.getCover());
			log.info("Episode Audio: " + result.getAudio());
			log.info("Episode Year: " + result.getYear());
			log.info("Number of tracks: " + result.getTracks().size());

		} catch (Exception e) {
			log.error("Test failed with exception: " + e.getMessage(), e);
		}
	}
}