package org.humanbeats.model;

import java.util.Date;
import java.util.List;

import org.humanbeats.util.HumanBeatsUtils.TYPE;

import lombok.Builder;
import lombok.Data;

/**
 * Playlist data structure to hold extracted track information
 */
@Data
@Builder
public class Document {

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
	private List<Track> tracks;
}
