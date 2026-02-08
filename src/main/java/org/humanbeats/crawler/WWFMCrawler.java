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
import org.humanbeats.model.Document;
import org.humanbeats.model.Track;
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.util.HumanBeatsUtils.TYPE;
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
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

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
	private Document playlistData;


	public WWFMCrawler(MongoRepository repo) {
		super(repo);
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
			throw new RuntimeException("WebDriver initialization failed", e);
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
	public Document crawlEpisode(String url) {
		if(playlistData != null) {
			return playlistData;
		}

		try {
			playlistData = Document.builder()
					.tracks(Lists.newArrayList())
					.url(url).build();
			initializeWebDriver();

			// Navigate to the episode page
			log.info("Crawling episode: " + url);
			driver.get(url);

			// Wait for page to load
			wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

			// Extract episode metadata
			extractEpisodeMetadata(playlistData);

			// Find and click the TRACKLIST button
			clickTracklistButton();

			// Wait for tracklist content to load and extract tracks
			extractPlaylistTracks(playlistData);

			// Find and click the LISTEN BACK button
			clickListenBackButton();

			// Wait for tracklist content to load and extract tracks
			extractAudio(playlistData);

			return playlistData;
		} catch (Exception e) {
			log.error("Error crawling episode " + url + ": " + e.getMessage());
			throw new RuntimeException("Error crawling episode " + url + ": " + e.getMessage(), e);
		} finally {
			cleanupWebDriver();
		}
	}

	/**
	 * Extract episode metadata (title, date, etc.)
	 */
	private void extractEpisodeMetadata(Document playlistData) {
		try {
			// Title
			WebElement titleElement = wait.until(ExpectedConditions.presenceOfElementLocated(
					By.cssSelector("[class*='text-h7']")));
			if (titleElement != null) {
				playlistData.setTitle(titleElement.getText().trim());
				log.debug("Episode title: " + playlistData.getTitle());
			}
			Preconditions.checkArgument(StringUtils.isNotBlank(playlistData.getTitle()), "Empty title!");

			// Description (same as title)
			playlistData.setDescription(playlistData.getTitle());
			log.debug("Episode description: " + playlistData.getDescription());
			Preconditions.checkArgument(StringUtils.isNotBlank(playlistData.getDescription()), "Empty description!");

			// Date
			WebElement dateElement = driver.findElement(By.cssSelector("[class*='text-h8']"));
			if (dateElement != null) {
				// DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE dd.MM.yy", Locale.ENGLISH);
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
				LocalDate date = LocalDate.parse(dateElement.getText().substring(3).trim(), inputFormatter);
				playlistData.setDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				log.debug("Episode date: " + playlistData.getDate());
			}
			Preconditions.checkNotNull(playlistData.getDate(), "Empty date!");

			// Cover
			WebElement coverElement = wait.until(ExpectedConditions.presenceOfElementLocated(
					By.tagName("img")));
			if (coverElement != null) {
				playlistData.setCover(coverElement.getAttribute("src").trim());
				log.debug("Episode cover: " + playlistData.getCover());
			}
			Preconditions.checkArgument(StringUtils.isNotBlank(playlistData.getCover()), "Empty cover!");

			// Year
			int year = playlistData.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
			playlistData.setYear(year);
			Preconditions.checkNotNull(playlistData.getYear(), "Empty year!");
		}
		catch (Exception e) {
			log.error("Could not extract all episode metadata: " + e.getMessage());
			throw new RuntimeException("Could not extract all episode metadata: " + e.getMessage(), e);
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
	private void extractPlaylistTracks(Document playlistData) {
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
				Track track = parseTrackText(artistText, trackText);
				playlistData.getTracks().add(track);
				log.debug("Extracted track: " + track.getFullTitle());
			}
			log.debug("Extracted " + playlistData.getTracks().size() + " tracks from playlist");
		} 
		catch (Exception e) {
			log.error("Error extracting playlist tracks: " + e.getMessage());
		}

		Preconditions.checkArgument(CollectionUtils.isNotEmpty(playlistData.getTracks()), "Empty playlist!");
	}

	/**
	 * Extract audio from the loaded content
	 */
	private void extractAudio(Document playlistData) {
		try {
			log.debug("Extracting audio...");

			// Wait for audio content to appear
			Thread.sleep(3000);

			// Artist
			List<WebElement> audioElements = driver.findElements(By.cssSelector("[title*='Mixcloud'"));

			// Extract audio from structured elements
			WebElement audioElement = audioElements.get(0);

			String audio = getUrlParameters(audioElement.getAttribute("src")).get("feed");
			playlistData.setAudio(audio);
			log.debug("Extracted audio: " + playlistData.getAudio());
		} 
		catch (Exception e) {
			log.error("Error extracting audio: " + e.getMessage());
		}

		Preconditions.checkNotNull(playlistData.getAudio(), "Audio not found!");
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
	private Track parseTrackText(String artist, String track) {
		Preconditions.checkArgument(StringUtils.isNotBlank(artist), "Empty artist!");
		Preconditions.checkArgument(StringUtils.isNotBlank(track), "Empty track!");

		return Track.builder()
				.artist(artist)
				.title(track)
				.fullTitle(artist + " - " + track).build();
	}

	public void load(String... args) 
	{
		MongoCursor<org.bson.Document> i = args.length == 0 ? repo.getShows().find(Filters.and(Filters.eq("type", WWFM))).iterator() : 
			repo.getShows().find(Filters.and(Filters.eq("type", WWFM), Filters.eq("source", args[0]))).iterator();
		while(i.hasNext()) 
		{
			org.bson.Document show = i.next();
			this.url = show.getString("url");
			this.artist = show.getString("title");
			this.source = show.getString("source");
			this.authors = show.get("authors", List.class);
			this.page = args.length == 2 ? Integer.parseInt(args[1]) : 1;

			log.info("Crawling " + artist + " (" + page + " page)");
			crawl(url);
		}
	}

	@Override
	public boolean shouldVisit(Page page, WebURL url) {
		return url.getURL().contains("worldwidefm.net/episode") && (url.getURL().contains(COCO_MARIA) || url.getURL().contains(GILLES_PETERSON));
	}

	@Override
	public void visit(Page page) {
		super.visit(page);
		playlistData = null;
	}

	@Override
	protected String getBaseUrl() {
		return URL;
	}

	@Override
	protected String getSource() {
		return source;
	}

	@Override
	protected TYPE getType(String url) {
		return TYPE.podcast;
	}

	@Override
	protected String getArtist(String url, org.jsoup.nodes.Document doc) {
		return artist;
	}

	@Override
	protected List<String> getAuthors(String url, org.jsoup.nodes.Document doc) {
		return authors;
	}

	@Override
	protected Date getDate(String url, org.jsoup.nodes.Document doc) {
		Document playlist = crawlEpisode(url);
		return playlist.getDate();
	}

	@Override
	protected Integer getYear(String url, org.jsoup.nodes.Document doc) {
		Document playlist = crawlEpisode(url);
		return playlist.getYear();
	}

	@Override
	protected String getDescription(String url, org.jsoup.nodes.Document doc) {
		Document playlist = crawlEpisode(url);
		return playlist.getDescription();
	}

	@Override
	protected String getTitle(String url, org.jsoup.nodes.Document doc) {
		Document playlist = crawlEpisode(url);
		return playlist.getTitle();
	}

	@Override
	protected List<org.bson.Document> getTracks(String url, org.jsoup.nodes.Document doc) {
		Document playlist = crawlEpisode(url);
		List<org.bson.Document> tracks = Lists.newArrayList();
		playlist.getTracks().forEach(t -> {
			tracks.add(newTrack(t.getFullTitle(), null));
		});
		return tracks;
	}

	@Override
	protected String getCover(String url, org.jsoup.nodes.Document doc) {
		Document playlist = crawlEpisode(url);
		return playlist.getCover();
	}

	@Override
	protected String getAudio(String url, org.jsoup.nodes.Document doc) {
		Document playlist = crawlEpisode(url);
		return playlist.getAudio();
	}
}
