package org.humanbeats.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.collections4.CollectionUtils;
import org.humanbeats.crawler.OndarockCrawler;
import org.humanbeats.model.HBDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Test class for OndarockCrawler
 * Tests the core functionality of crawling Ondarock albums
 */
@Slf4j
public class OndarockCrawlerTest {

	private static final String TEST_ALBUM_URL = "https://www.ondarock.it/recensioni/2025-natalialafourcade-cancionera.htm";

	@BeforeEach
	void setUp() {
		// nothing to do
	}

	@AfterEach
	void tearDown() {
		// Cleanup is handled automatically by the crawler
	}

	@Test
	void testCrawlAlbum() {
		try {
			Document doc = Jsoup.connect(TEST_ALBUM_URL).ignoreContentType(true).get();
			OndarockCrawler crawler = new OndarockCrawler();
			HBDocument result = crawler.crawlDocument(TEST_ALBUM_URL, doc);

			// Verify basic structure
			assertNotNull(result, "Album data should not be null");
			assertNotNull(result.getId(), "Album id should not be null");
			assertTrue(CollectionUtils.isNotEmpty(result.getTracks()), "Tracks list should not be null");
			assertEquals(TEST_ALBUM_URL, result.getUrl(), "Album URL should match");

			// Verify episode metadata
			assertNotNull(result.getId(), "Album id should not be null");
			assertNotNull(result.getUrl(), "Album url should not be null");
			assertNotNull(result.getTitle(), "Album title should not be null");
			assertNotNull(result.getDescription(), "Album description should not be null");
			assertNotNull(result.getDate(), "Album date should not be null");
			assertNotNull(result.getCover(), "Album cover should not be null");
//			assertNotNull(result.getAudio(), "Album audio should not be null");
			assertNotNull(result.getYear(), "Album year should not be null");
			assertTrue(CollectionUtils.isNotEmpty(result.getTracks()), "Episode tracks should not be empty");

			log.info("Album Id: " + result.getId());
			log.info("Album Url: " + result.getUrl());
			log.info("Album Title: " + result.getTitle());
			log.info("Album Description: " + result.getDescription());
			log.info("Album Date: " + result.getDate());
			log.info("Album Cover: " + result.getCover());
//			log.info("Album Audio: " + result.getAudio());
			log.info("Album Year: " + result.getYear());
			log.info("Number of tracks: " + result.getTracks().size());

		} catch (Exception e) {
			log.error("Test failed with exception: " + e.getMessage(), e);
		}
	}
}