package org.phonoteke.batch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("stats")
public class Stat {
	@Id
    private String id;
	
	private String name;
}
