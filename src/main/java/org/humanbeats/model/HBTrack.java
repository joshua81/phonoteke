package org.humanbeats.model;

import lombok.Builder;
import lombok.Data;

/**
 * Track information structure
 */
@Data
@Builder
public class HBTrack {

	private String titleOrig;
	private String youtube;
}
