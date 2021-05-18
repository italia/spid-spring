package it.italia.developers.spid.integration.config;

import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.AbstractCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.springframework.security.saml.key.KeyManager;

public class IdpKeyManager extends AbstractCredentialResolver implements KeyManager {
	private String entityId;
	private Set<String> availableCredentials;
	private X509Certificate certificate;
	private BasicX509Credential credential;

	public IdpKeyManager(final String entityId, final String certificateStr) throws MetadataProviderException, CertificateException, UnsupportedEncodingException {
		super();
		this.entityId = entityId;
		availableCredentials = new HashSet<>();
		availableCredentials.add(entityId);

		certificate = X509Utils.generateX509Certificate(certificateStr);

		credential = new BasicX509Credential();
		credential.setEntityId(entityId);
		credential.setEntityCertificate(certificate);
	}

	@Override
	public Credential getCredential(final String key) {
		if (key != null && entityId != null && key.equals(entityId)) {
			return credential;
		} else {
			return null;
		}
	}

	@Override
	public Credential getDefaultCredential() {
		return getCredential(entityId);
	}

	@Override
	public String getDefaultCredentialName() {
		return entityId;
	}

	@Override
	public Set<String> getAvailableCredentials() {
		return availableCredentials;
	}

	@Override
	public Iterable<Credential> resolve(final CriteriaSet criteriaSet) throws SecurityException {
		return Arrays.asList(getCredential(criteriaSet.get(EntityIDCriteria.class).getEntityID()));
	}

	@Override
	public X509Certificate getCertificate(final String key) {
		if (key != null && entityId != null && key.equals(entityId)) {
			return certificate;
		} else {
			return null;
		}
}

}
