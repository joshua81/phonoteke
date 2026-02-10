package org.humanbeats;

import java.util.Arrays;

import org.humanbeats.crawler.AbstractCrawler;
import org.humanbeats.crawler.BBCRadioCrawler;
import org.humanbeats.crawler.NTSCrawler;
import org.humanbeats.crawler.OndarockCrawler;
import org.humanbeats.crawler.RadioCapitalCrawler;
import org.humanbeats.crawler.RadioRaheemCrawler;
import org.humanbeats.crawler.RadioRaiCrawler;
import org.humanbeats.crawler.SpreakerCrawler;
import org.humanbeats.crawler.WWFMCrawler;
import org.humanbeats.indexer.DiscogsIndexer;
import org.humanbeats.indexer.MusicbrainzIndexer;
import org.humanbeats.indexer.SpotifyIndexer;
import org.humanbeats.indexer.YoutubeIndexer;
import org.humanbeats.repo.MongoRepository;
import org.humanbeats.service.PatchService;
import org.humanbeats.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class HumanBeats implements CommandLineRunner {

	@Autowired
	private MongoRepository repo;

	@Autowired
	private YoutubeIndexer youtubeLoader;

	@Autowired
	private MusicbrainzIndexer musicbrainzLoader;

	@Autowired
	private DiscogsIndexer discogsLoader;

	@Autowired
	private SpotifyIndexer spotifyLoader;

	@Autowired
	private StatsService statsLoader;

	@Autowired
	private PatchService patchLoader;


	public static void main(String[] args) {
		SpringApplication.run(HumanBeats.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("                    #*++              **++                     ");
		log.info("                   *##+++++++    ******++++                    ");
		log.info("                  #####+++++*####*****++++++                   ");
		log.info("                 *####################*++++++                  ");
		log.info("                *##########################*++                 ");
		log.info("                 ############################                  ");
		log.info("                   ########################                    ");
		log.info("                     ####################                      ");
		log.info("                       ################                        ");
		log.info("                        ##############                         ");
		log.info("                          ##########                           ");
		log.info("                            ######                             ");
		log.info("                              ##                               ");
		log.info(" ------------------------------------------------------------- ");
		log.info("| HUMAN BEATS - music designed by humans, assembled by robots |");
		log.info(" ------------------------------------------------------------- ");

		if(args.length > 0) {
			String task = args[0].split(":")[0];
			String[] subtask = Arrays.copyOfRange(args[0].split(":"), 1, args[0].split(":").length);
			if("mb".equals(task)) {
				musicbrainzLoader.load(subtask);
				return;
			}
			else if("dg".equals(task)) {
				discogsLoader.load(subtask);
				return;
			}
			else if("sp".equals(task)) {
				spotifyLoader.load(subtask);
				return;
			}
			else if("yt".equals(task)) {
				youtubeLoader.load(subtask);
				return;
			}
			else if("doc".equals(task)) {
				crawlReviews(subtask);
				return;
			}
			else if("pod".equals(task)) {
				crawlPodcasts(subtask);
				return;
			}
			else if("stats".equals(task)) {
				statsLoader.load(subtask);
				return;
			}
			else if("patch".equals(task)) {
				patchLoader.load(subtask);
				return;
			}
		}
		printHelp();
	}

	private void crawlPodcasts(String... args) {
		AbstractCrawler.repo = repo;
		new BBCRadioCrawler().load(args);
		new RadioRaiCrawler().load(args);
		new SpreakerCrawler().load(args);
		new WWFMCrawler().load(args);
		new RadioCapitalCrawler().load(args);
		new NTSCrawler().load(args);
		new RadioRaheemCrawler().load(args);
	}

	private void crawlReviews(String... args) {
		AbstractCrawler.repo = repo;
		new OndarockCrawler().load(args);
	}

	private static void printHelp() {
		log.info("Usage:");
		log.info("- compile (compiles sources)");
		log.info("- deploy (deploys to GCloud)");
		log.info("- test (deploys test)");
		log.info("- mb (loads Music Brainz)");
		log.info("- sp (loads Spotify)");
		log.info("- sp:<token> (creates Spotify playlists)");
		log.info("- yt (loads Youtube)");
		log.info("- doc (loads documents)");
		log.info("- pod (loads podcasts)");
		log.info("- stats (loads stats)");
		log.info("- patch:calculateScore (patches db)");
		log.info("- patch:resetTracks:<year> (patches db)");
		log.info("- patch:replaceSpecialChars (patches db)");
		log.info("- patch:fixYoutube (patches db)");
	}
}