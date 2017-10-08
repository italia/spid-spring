package it.italia.developers.spid.spidspringrest.model;

public class ExtraInfo {

	private String title;
	private String url;

	public ExtraInfo() {
	}

	public ExtraInfo(final String title, final String url) {
		this.title = title;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(final String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(final String url) {
		this.url = url;
	}
}
