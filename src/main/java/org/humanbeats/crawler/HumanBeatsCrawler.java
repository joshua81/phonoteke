package org.humanbeats.crawler;

import org.humanbeats.repo.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HumanBeatsCrawler
{
	@Autowired
	private MongoRepository repo;

	private BBCRadioCrawler bbc = new BBCRadioCrawler(repo);
	private RadioRaiCrawler rai = new RadioRaiCrawler(repo);
	private SpreakerCrawler spreaker = new SpreakerCrawler(repo);
	private WWFMCrawler wwfm = new WWFMCrawler(repo);
	private RadioCapitalCrawler capital = new RadioCapitalCrawler(repo);
	private OndarockCrawler ondarock = new OndarockCrawler(repo);
	private NTSCrawler nts = new NTSCrawler(repo);
	private RadioRaheemCrawler raheem = new RadioRaheemCrawler(repo);


	public void crawlPodcasts(String... args) {
		bbc.load(args);
		rai.load(args);
		spreaker.load(args);
		wwfm.load(args);
		nts.load(args);
		raheem.load(args);
		capital.load(args);
	}

	public void crawlReviews(String... args) {
		ondarock.load(args);
	}
}
