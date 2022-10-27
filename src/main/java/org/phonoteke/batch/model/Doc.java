package org.phonoteke.batch.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("docs")
public class Doc {
	@Id
	private String id;

	private String url;
	private String type;
	private String artist;
	private String title;
	private List<String> authors;
	private String cover;
	private Date date;
	private String description;
	private List<String> genres;
	private String label;
	private List<String> links;
	private String review;
	private String source;
	private Number vote;
	private Integer year;
	private List<org.bson.Document> tracks;
	private String audio;
	private String spalbumid;
	private String spartistid;
	private String coverL;
	private String coverM;
	private String coverS;
	private Number score;
	private String artistid;
	private Boolean dirty;
}
