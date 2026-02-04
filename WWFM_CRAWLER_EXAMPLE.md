# Worldwide FM Crawler - Usage Example

## Quick Start

The Worldwide FM Crawler has been successfully implemented and can extract music playlists from Worldwide FM episode pages by interacting with the dynamic TRACKLIST button.

## Example Usage

### 1. Basic Crawling

```java
import org.phonoteke.loader.WWFMCrawler;
import org.phonoteke.loader.WWFMCrawler.PlaylistData;
import org.phonoteke.loader.WWFMCrawler.TrackInfo;

public class Example {
    public static void main(String[] args) {
        WWFMCrawler crawler = new WWFMCrawler();
        
        try {
            // Crawl the specified episode
            PlaylistData playlist = crawler.crawlEpisode(
                "https://www.worldwidefm.net/episode/breakfast-club-coco-06012026"
            );
            
            // Display results
            System.out.println("Episode: " + playlist.getEpisodeTitle());
            System.out.println("Date: " + playlist.getEpisodeDate());
            System.out.println("Total tracks: " + playlist.getTracks().size());
            
            // List all tracks
            for (int i = 0; i < playlist.getTracks().size(); i++) {
                TrackInfo track = playlist.getTracks().get(i);
                System.out.println((i + 1) + ". " + track.getFullTitle());
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

### 2. Command Line Usage

```bash
# Compile the project
mvn clean compile

# Run the test main class
java -cp "target/classes:target/lib/*" org.phonoteke.loader.WWFMCrawlerMain

# Or with a specific episode URL
java -cp "target/classes:target/lib/*" org.phonoteke.loader.WWFMCrawlerMain \
  "https://www.worldwidefm.net/episode/your-episode-url"
```

### 3. Integration with Existing System

```java
// The crawler extends AbstractCrawler for compatibility
WWFMCrawler crawler = new WWFMCrawler();

// Legacy method - integrates with existing database storage
crawler.load("https://www.worldwidefm.net/episode/breakfast-club-coco-06012026");
```

## Key Features Implemented

✅ **Dynamic Content Handling**: Automatically clicks the TRACKLIST button and waits for content to load  
✅ **Robust Error Handling**: Handles network issues, missing elements, and browser failures gracefully  
✅ **Browser Resource Management**: Proper WebDriver initialization and cleanup  
✅ **Headless Operation**: Runs without GUI for server environments  
✅ **Structured Data Output**: Returns well-organized playlist and track information  
✅ **Multiple Extraction Strategies**: Uses various methods to find and parse track information  

## Testing

Run the unit tests to verify functionality:

```bash
mvn test -Dtest=WWFMCrawlerTest
```

The tests verify:
- Data structure creation and manipulation
- Basic crawler functionality
- Error handling
- Track parsing logic

## Requirements Satisfied

The implementation fully satisfies the task requirements:

1. ✅ **Java Class**: Complete Java implementation in `WWFMCrawler.java`
2. ✅ **URL Crawling**: Successfully navigates to the specified Worldwide FM episode URL
3. ✅ **Dynamic Interaction**: Finds and clicks the TRACKLIST button using Selenium WebDriver
4. ✅ **Playlist Extraction**: Extracts structured music playlist information
5. ✅ **Error Handling**: Comprehensive error handling and resource management
6. ✅ **Browser Management**: Proper headless browser operation with cleanup

## Technical Implementation

- **Selenium WebDriver 4.16.1** for browser automation
- **Chrome headless mode** for server compatibility  
- **WebDriverManager** for automatic ChromeDriver setup
- **Multiple selector strategies** for robust element finding
- **Retry mechanisms** for handling dynamic content loading
- **Pattern matching** for extracting artist and track information

## Next Steps

The crawler is ready for production use. Consider these enhancements:

1. **Configuration**: Add external config for timeouts and selectors
2. **Caching**: Implement result caching to avoid repeated crawling
3. **Monitoring**: Add logging and metrics for production monitoring
4. **Batch Processing**: Support for crawling multiple episodes

The implementation provides a solid foundation for extracting music playlist data from Worldwide FM episodes with proper error handling and resource management.