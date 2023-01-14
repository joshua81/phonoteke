package org.phonoteke.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PodcastLoader
{
	@Autowired
	private BBCRadioLoader bbc;
	
	@Autowired
	private RadioRaiLoader rai;
	
	@Autowired
	private SpreakerLoader spreaker;
	
	@Autowired
	private WorldWideFMLoader wwfm;

	@Autowired
	private RadioCapitalLoader capital;
	
	public void load(String... args) {
		bbc.load(args);
		rai.load(args);
		spreaker.load(args);
		wwfm.load(args);
		capital.load(args);
	}
}
