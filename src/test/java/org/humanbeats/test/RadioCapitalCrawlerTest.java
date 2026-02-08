package org.humanbeats.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.CollectionUtils;
import org.humanbeats.crawler.RadioCapitalCrawler;
import org.humanbeats.model.HBDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Test class for RadioCapitalCrawler
 * Tests the core functionality of crawling Radio Capital episodes and extracting playlists
 */
@Slf4j
public class RadioCapitalCrawlerTest {

	private static final String TEST_EPISODE_URL = "https://www.capital.it/programmi/extra/puntate/extra-del-07-02-2026/";

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
			Document doc = Jsoup.connect(TEST_EPISODE_URL).ignoreContentType(true).get();
			RadioCapitalCrawler crawler = new RadioCapitalCrawler(null);
			crawler.setSource("alexpaletta");
			HBDocument result = crawler.crawlDocument(TEST_EPISODE_URL, doc);

			// Verify basic structure
			assertNotNull(result, "Playlist data should not be null");
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