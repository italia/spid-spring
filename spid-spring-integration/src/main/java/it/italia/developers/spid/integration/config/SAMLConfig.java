package it.italia.developers.spid.integration.config;

import lombok.Data;

@Data
public class SAMLConfig {

	private String idpEntityId;
	private String spEntityId;

	private String loginUrl;
	private String logoutUrl;

	private String alias;
	private String defaultBaseUrl;
	private String x509Certificate;
}
