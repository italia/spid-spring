package it.italia.developers.spid.integration.config;

public class SAMLConfig {

	private String idpEntityId;
	private String spEntityId;

	private String loginUrl;
	private String logoutUrl;

	private String alias;
	private String defaultBaseUrl;
	private String x509Certificate;

	public String getIdpEntityId() {
		return idpEntityId;
	}

	public void setIdpEntityId(final String idpEntityId) {
		this.idpEntityId = idpEntityId;
	}

	public String getX509Certificate() {
		return x509Certificate;
	}

	public void setX509Certificate(final String x509Certificate) {
		this.x509Certificate = x509Certificate;
	}

	public String getSpEntityId() {
		return spEntityId;
	}

	public void setSpEntityId(final String spEntityId) {
		this.spEntityId = spEntityId;
	}

	public String getDefaultBaseUrl() {
		return defaultBaseUrl;
	}

	public void setDefaultBaseUrl(final String defaultBaseUrl) {
		this.defaultBaseUrl = defaultBaseUrl;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(final String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(final String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getBaseUrl() {
		return defaultBaseUrl;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

}
