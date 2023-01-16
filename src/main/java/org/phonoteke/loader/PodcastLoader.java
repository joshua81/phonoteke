package org.phonoteke.loader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PodcastLoader
{
	@Autowired
	private MongoRepository repo;

	private BBCRadioLoader bbc = new BBCRadioLoader();
	private RadioRaiLoader rai = new RadioRaiLoader();
	private SpreakerLoader spreaker = new SpreakerLoader();
	private WorldWideFMLoader wwfm = new WorldWideFMLoader();
	private RadioCapitalLoader capital = new RadioCapitalLoader();

	@PostConstruct
	public void init() {
		BBCRadioLoader.repo = repo;
		RadioRaiLoader.repo = repo;
		SpreakerLoader.repo = repo;
		WorldWideFMLoader.repo = repo;
		RadioCapitalLoader.repo = repo;
	}

	public void load(String... args) {
		bbc.load(args);
		rai.load(args);
		spreaker.load(args);
		wwfm.load(args);
		capital.load(args);
	}
}
