package org.humanbeats;

import java.util.Arrays;

import org.humanbeats.crawler.HumanBeatsCrawler;
import org.humanbeats.indexer.DiscogsIndexer;
import org.humanbeats.indexer.MusicbrainzIndexer;
import org.humanbeats.indexer.SpotifyIndexer;
import org.humanbeats.indexer.YoutubeIndexer;
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
	private HumanBeatsCrawler crawler;

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
				crawler.crawlReviews(subtask);
				return;
			}
			else if("pod".equals(task)) {
				crawler.crawlPodcasts(subtask);
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

	private static void printHelp() {
		System.out.println("Usage:");
		System.out.println("- compile (compiles sources)");
		System.out.println("- deploy (deploys to GCloud)");
		System.out.println("- test (deploys test)");
		System.out.println("- mb (loads Music Brainz)");
		System.out.println("- sp (loads Spotify)");
		System.out.println("- sp:<token> (creates Spotify playlists)");
		System.out.println("- yt (loads Youtube)");
		System.out.println("- doc (loads documents)");
		System.out.println("- pod (loads podcasts)");
		System.out.println("- stats (loads stats)");
		System.out.println("- patch:calculateScore (patches db)");
		System.out.println("- patch:resetTracks:<year> (patches db)");
		System.out.println("- patch:replaceSpecialChars (patches db)");
		System.out.println("- patch:fixYoutube (patches db)");
	}
}