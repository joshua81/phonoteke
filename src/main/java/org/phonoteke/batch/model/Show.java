package org.phonoteke.batch.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("shows")
public class Show {
	@Id
    private String id;
	
	private String url;
	private String title;
	private String source;
	private List<String> authors;
}
