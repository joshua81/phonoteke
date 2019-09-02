package org.phonoteke.model;

public class Track 
{
	private String title;
	private String youtube;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getYoutube() {
		return youtube;
	}
	public void setYoutube(String youtube) {
		this.youtube = youtube;
	}
	
	public static Track newInstance(String title, String youtube)
	{
		Track t = new Track();
		t.setTitle(title);
		t.setYoutube(youtube);
		return t;
	}
}
