package org.humanbeats.model;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.bson.Document;
import org.humanbeats.util.HumanBeatsUtils.TYPE;

import lombok.Builder;
import lombok.Data;

/**
 * Playlist data structure to hold extracted track information
 */
@Data
@Builder
public class HBDocument {

	private String id;
	private TYPE type;
	private String source;
	private String artist;
	private String title;
	private String description;
	private String url;
	private Date date;
	private String cover;
	private String label;
	private String review;
	private Float vote;
	private String audio;
	private Integer year;
	private List<String> authors;
	private List<String> genres;
	private List<String> links;
	private List<HBTrack> tracks;

	public Document toJson() {
		return new Document("id", id).
				append("url", url).
				append("type", type.name()).
				append("artist", artist).
				append("title", title).
				append("authors", authors).
				append("cover", cover).
				append("date", date).
				append("description", description).
				append("genres", genres).
				append("label", label).
				append("links", links).
				append("review", review).
				append("source", source).
				append("vote", vote).
				append("year", year).
				append("tracks", toJson(tracks)).
				append("audio", audio);
	}

	protected List<Document> toJson(List<HBTrack> tracks) {
		List<Document> json = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(tracks)) {
			tracks.forEach(t -> {
				json.add(new Document("titleOrig", t.getTitleOrig()).
						append("title", t.getTitleOrig()).
						append("youtube", t.getYoutube()));
			});
		}
		return json;
	}
}
