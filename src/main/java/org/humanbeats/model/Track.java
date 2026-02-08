package org.humanbeats.model;

import lombok.Builder;
import lombok.Data;

/**
 * Track information structure
 */
@Data
@Builder
public class Track {

	private String artist;
	private String title;
	private String fullTitle;
}
