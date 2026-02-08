package org.humanbeats.model;

import lombok.Builder;
import lombok.Data;

/**
 * Track information structure
 */
@Data
@Builder
public class Track {

	private String titleOrig;
	private String youtube;
}
