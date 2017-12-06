package it.italia.developers.spid.integration.model;

public class AuthRequest {

	private String xmlAuthRequest;
	private String destinationUrl;

	public String getXmlAuthRequest() {
		return xmlAuthRequest;
	}

	public void setXmlAuthRequest(final String xmlAuthRequest) {
		this.xmlAuthRequest = xmlAuthRequest;
	}

	public String getDestinationUrl() {
		return destinationUrl;
	}

	public void setDestinationUrl(final String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}
}
