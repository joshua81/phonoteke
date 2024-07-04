package org.phonoteke.loader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HumanBeatsCrawler
{
	@Autowired
	private MongoRepository repo;

	private BBCRadioCrawler bbc = new BBCRadioCrawler();
	private RadioRaiCrawler rai = new RadioRaiCrawler();
	private SpreakerCrawler spreaker = new SpreakerCrawler();
	private WWFMCrawler wwfm = new WWFMCrawler();
	private RadioCapitalCrawler capital = new RadioCapitalCrawler();
	private OndarockCrawler ondarock = new OndarockCrawler();
	private NTSCrawler nts = new NTSCrawler();
	private RadioRaheemCrawler raheem = new RadioRaheemCrawler();

	@PostConstruct
	public void init() {
		BBCRadioCrawler.repo = repo;
		RadioRaiCrawler.repo = repo;
		SpreakerCrawler.repo = repo;
		WWFMCrawler.repo = repo;
		RadioCapitalCrawler.repo = repo;
		OndarockCrawler.repo = repo;
		NTSCrawler.repo = repo;
		RadioRaheemCrawler.repo = repo;
	}

	public void crawlPodcasts(String... args) {
		bbc.load(args);
		rai.load(args);
		spreaker.load(args);
		wwfm.load(args);
		capital.load(args);
		nts.load(args);
		raheem.load(args);
	}

	public void crawlReviews(String... args) {
		ondarock.load(args);
	}
}
