package org.humanbeats.model;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

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

	public Document toJson() {
		return new Document("titleOrig", titleOrig)
				.append("youtube", youtube);
	}

	public static HBTrack newInstance(String title)
	{
		if(StringUtils.isNotBlank(title)) {
			title = title.replaceAll("&nbsp;", " ");
			title = title.replaceAll("\"", "");
			title = title.trim();
		}
		return HBTrack.builder().titleOrig(title).build(); 
	}
}
