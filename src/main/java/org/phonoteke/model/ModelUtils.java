package org.phonoteke.model;

import java.util.Map;

import com.google.api.client.util.Maps;

public class ModelUtils 
{
	public static Map<String, String> newTrack(String title, String youtube)
	{
		Map<String, String> track = Maps.newHashMap();
		track.put("title", title);
		track.put("youtube", youtube);
		return track;
	}
}
