package org.humanbeats.crawler;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.humanbeats.model.HBDocument;
import org.humanbeats.model.HBTrack;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WWFMCrawler extends AbstractCrawler
{
	private static final String WWFM = "wwfm";
	private static final String URL = "https://www.worldwidefm.net/";
	private static final int TIMEOUT_SECONDS = 10;

	private static final String COCO_MARIA = "breakfast-club-coco";
	private static final String GILLES_PETERSON = "gilles-peterson";

	private WebDriver driver;
	private WebDriverWait wait;


	public WWFMCrawler() {
		this.type = WWFM;
	}

	/**
	 * Initialize WebDriver with proper configuration
	 */
	private void initializeWebDriver() {
		try {
			// Setup ChromeDriver automatically
			WebDriverManager.chromedriver().setup();

			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless=new"); // Use new headless mode
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--disable-gpu");
			options.addArguments("--disable-web-security");
			options.addArguments("--disable-features=VizDisplayCompositor");
			options.addArguments("--window-size=1920,1080");
			options.addArguments("--remote-allow-origins=*");
			options.addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

			// Additional options for stability
			options.addArguments("--disable-blink-features=AutomationControlled");
			options.addArguments("--disable-extensions");
			options.addArguments("--no-first-run");
			options.addArguments("--disable-default-apps");

			driver = new ChromeDriver(options);
			wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));

			log.debug("WebDriver initialized successfully");
		} catch (Exception e) {
			log.error("Failed to initialize WebDriver: " + e.getMessage());
			throw new RuntimeException("WebDriver initialization failed");
		}
	}

	/**
	 * Clean up WebDriver resources
	 */
	private void cleanupWebDriver() {
		try {
			if (driver != null) {
				driver.quit();
				driver = null;
				wait = null;
				log.debug("WebDriver cleaned up successfully");
			}
		} catch (Exception e) {
			log.error("Error during WebDriver cleanup: " + e.getMessage());
		}
	}

	/**
	 * Crawl a specific Worldwide FM episode URL and extract playlist
	 */
	@Override
	public HBDocument crawlDocument(String url, Document doc) {
		try {
			HBDocument episode = HBDocument.builder()
					.id(getId(url))
					.url(url)
					.source(source)
					.type(TYPE.podcast)
					.artist(artist)
					.authors(authors)
					.tracks(Lists.newArrayList()).build();
			initializeWebDriver();

			// Navigate to the episode page
			log.info("Crawling episode: " + url);
			driver.get(url);

			// Wait for page to load
			wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

			// Extract episode metadata
			extractEpisodeMetadata(episode);

			// Find and click the TRACKLIST button
			clickTracklistButton();

			// Wait for tracklist content to load and extract tracks
			extractPlaylistTracks(episode);

			// Find and click the LISTEN BACK button
			clickListenBackButton();

			// Wait for tracklist content to load and extract tracks
			extractAudio(episode);

			return episode;
		} catch (Exception e) {
			//log.error("Error crawling episode " + url + ": " + e.getMessage());
			throw new RuntimeException("Error crawling episode " + url + ": " + e.getMessage());
		} finally {
			cleanupWebDriver();
		}
	}

	@Override
	public HBDocument crawlDocument(String url, JsonObject doc) {
		throw new RuntimeException("Not implemented!!");
	}

	/**
	 * Extract episode metadata (title, date, etc.)
	 */
	private void extractEpisodeMetadata(HBDocument episode) {
		try {
			// Title
			WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(
					By.cssSelector("[class*='text-h7']")));
			if (titleElement != null) {
				episode.setTitle(titleElement.getText().trim());
				log.debug("Episode title: " + episode.getTitle());
			}
			Preconditions.checkArgument(StringUtils.isNotBlank(episode.getTitle()), "Empty title!");

			// Description (same as title)
			episode.setDescription(episode.getTitle());
			log.debug("Episode description: " + episode.getDescription());
			Preconditions.checkArgument(StringUtils.isNotBlank(episode.getDescription()), "Empty description!");

			// Date
			WebElement dateElement = driver.findElement(By.cssSelector("[class*='text-h8']"));
			if (dateElement != null) {
				// DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE dd.MM.yy", Locale.ENGLISH);
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
				LocalDate date = LocalDate.parse(dateElement.getText().substring(3).trim(), inputFormatter);
				episode.setDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				log.debug("Episode date: " + episode.getDate());
			}
			Preconditions.checkNotNull(episode.getDate(), "Empty date!");

			// Cover
			WebElement coverElement = wait.until(ExpectedConditions.presenceOfElementLocated(
					By.tagName("img")));
			if (coverElement != null) {
				episode.setCover(coverElement.getAttribute("src").trim());
				log.debug("Episode cover: " + episode.getCover());
			}
			Preconditions.checkArgument(StringUtils.isNotBlank(episode.getCover()), "Empty cover!");

			// Year
			int year = episode.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
			episode.setYear(year);
			Preconditions.checkNotNull(episode.getYear(), "Empty year!");
		}
		catch (Exception e) {
			//log.error("Could not extract all episode metadata: " + e.getMessage());
			throw new RuntimeException("Could not extract all episode metadata: " + e.getMessage());
		}
	}

	/**
	 * Find and click the TRACKLIST button
	 */
	private void clickTracklistButton() {
		log.debug("Attempting to find TRACKLIST button");

		// Try multiple selectors for the TRACKLIST button
		WebElement tracklistButton = null;
		List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(), 'Tracklist')]"));
		if (!elements.isEmpty()) {
			tracklistButton = elements.get(0);
		}

		if (tracklistButton != null) {
			log.debug("Found TRACKLIST button, clicking...");
			Actions actions = new Actions(driver);
			actions.moveToElement(tracklistButton).click().perform();
			log.debug("TRACKLIST button clicked successfully");
		}
	}

	/**
	 * Find and click the LISTEN BACK button
	 */
	private void clickListenBackButton() {
		log.debug("Attempting to find LISTEN BACK button");

		// Try multiple selectors for the LISTEN BACK button
		WebElement listenBackButton = null;
		List<WebElement> elements = driver.findElements(By.cssSelector("button"));
		if (!elements.isEmpty()) {
			listenBackButton = elements.get(5);
		}

		if (listenBackButton != null) {
			log.debug("Found LISTEN BACK button, clicking...");
			Actions actions = new Actions(driver);
			actions.moveToElement(listenBackButton).click().perform();
			log.debug("LISTEN BACK button clicked successfully");
		}
	}

	/**
	 * Extract playlist tracks from the loaded content
	 */
	private void extractPlaylistTracks(HBDocument episode) {
		try {
			log.debug("Extracting playlist tracks...");

			// Wait for tracklist content to appear
			Thread.sleep(3000);

			// Artist
			List<WebElement> artistElements = driver.findElements(By.cssSelector("[class*='w-[30%]'"));

			// Track
			List<WebElement> trackElements = driver.findElements(By.cssSelector("[class*='w-[70%]']"));

			// Extract tracks from structured elements
			for (WebElement artistElement : artistElements) {
				WebElement trackElement = trackElements.get(artistElements.indexOf(artistElement));
				String artistText = artistElement.getText().trim();
				String trackText = trackElement.getText().trim();
				HBTrack track = parseTrackText(artistText, trackText);
				episode.getTracks().add(track);
				log.debug("Extracted track: " + track.getTitleOrig());
			}
			log.debug("Extracted " + episode.getTracks().size() + " tracks from playlist");
		} 
		catch (Exception e) {
			log.error("Error extracting playlist tracks: " + e.getMessage());
		}

		Preconditions.checkArgument(CollectionUtils.isNotEmpty(episode.getTracks()), "Empty playlist!");
	}

	/**
	 * Extract audio from the loaded content
	 */
	private void extractAudio(HBDocument episode) {
		try {
			log.debug("Extracting audio...");

			// Wait for audio content to appear
			Thread.sleep(3000);

			// Artist
			List<WebElement> audioElements = driver.findElements(By.cssSelector("[title*='Mixcloud'"));

			// Extract audio from structured elements
			WebElement audioElement = audioElements.get(0);

			String audio = getUrlParameters(audioElement.getAttribute("src")).get("feed");
			episode.setAudio(audio);
			log.debug("Extracted audio: " + episode.getAudio());
		} 
		catch (Exception e) {
			log.error("Error extracting audio: " + e.getMessage());
		}

		Preconditions.checkNotNull(episode.getAudio(), "Audio not found!");
	}

	private Map<String, String> getUrlParameters(String url) {
		Map<String, String> params = new HashMap<>();
		String[] urlParts = url.split("\\?");
		if (urlParts.length > 1) {
			String query = urlParts[1];
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				String[] keyValue = pair.split("=", 2);
				if (keyValue.length > 1) {
					String key = keyValue[0];
					String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
					params.put(key, value);
				}
			}
		}
		return params;
	}

	/**
	 * Parse track text in "Artist - Title" format
	 */
	private HBTrack parseTrackText(String artist, String track) {
		Preconditions.checkArgument(StringUtils.isNotBlank(artist), "Empty artist!");
		Preconditions.checkArgument(StringUtils.isNotBlank(track), "Empty track!");

		return HBTrack.builder().titleOrig(artist + " - " + track).build();
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) {
		return url.getURL().contains("worldwidefm.net/episode") && (url.getURL().contains(COCO_MARIA) || url.getURL().contains(GILLES_PETERSON));
	}

	@Override
	protected String getBaseUrl() {
		return URL;
	}
}
