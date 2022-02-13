package it.italia.developers.spid.integration.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.AbstractReloadingMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;

public class AuthenticationInfoExtractor {

	private static final String SAML2_PROTOCOL = "urn:oasis:names:tc:SAML:2.0:protocol";
	private static final String SAML2_POST_BINDING = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
	private static final String SAML2_NAME_ID_POLICY = "urn:oasis:names:tc:SAML:2.0:nameid-format:transient";
	private static final String SAML2_PASSWORD_PROTECTED_TRANSPORT = "https://www.spid.gov.it/SpidL2";
	private static final String SAML2_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";
	private static final String SPID_SPRING_INTEGRATION_IDP_PREFIX = "spid.spring.integration.idp.";
	private static final String SPID_SPRING_INTEGRATION_IDP_KEYS = "spid.spring.integration.idp.keys";

	XPathFactory xPathfactory = XPathFactory.newInstance();

	AuthRequest authRequest = new AuthRequest();

	SPIDIntegrationUtil spidIntegrationUtil;

	public AuthenticationInfoExtractor(String entityId, SPIDIntegrationUtil spidIntegrationUtil, Integer assertionConsumerServiceIndex) throws IntegrationServiceException {

		try {
			this.spidIntegrationUtil = spidIntegrationUtil;

			String xmlResourcePath = retrieveXMLResourcePath(entityId);

			ClasspathResource resource = new ClasspathResource("/metadata/idp/" + xmlResourcePath);

			IDPSSODescriptor idpssoDescriptor = getIDPSSODescriptor(entityId, resource);
			List<SingleSignOnService> singleSignOnServices = idpssoDescriptor.getSingleSignOnServices();
			SingleSignOnService singleSignOnServiceUsed = null;
			for (SingleSignOnService singleSignOnService : singleSignOnServices) {
				if (singleSignOnService.getBinding().equals(SAML2_POST_BINDING)) {
					singleSignOnServiceUsed = singleSignOnService;
					break;
				}
			}

			String destination = singleSignOnServiceUsed.getLocation();

			resource = new ClasspathResource("/metadata/sp/metadata-sp.xml");
			EntityDescriptor spEntityDescriptor = getEntityDescriptor("https://spid.lecce.it", resource);
			String id = spEntityDescriptor.getID();

			// TODO utilizzare?
			X509Certificate certificate = null;

			List<KeyDescriptor> keyDescriptors = idpssoDescriptor.getKeyDescriptors();
			for (KeyDescriptor keyDescriptor : keyDescriptors) {
				KeyInfo keyInfo = keyDescriptor.getKeyInfo();
				if (keyInfo != null) {
					List<X509Data> x509Datas = keyInfo.getX509Datas();
					for (X509Data x509Data : x509Datas) {
						List<X509Certificate> x509Certificates = x509Data.getX509Certificates();
						if (x509Certificates != null && !x509Certificates.isEmpty()) {
							certificate = x509Certificates.get(0);
						}
					}
				}
			}

			String assertionConsumerServiceUrl = null;
			SPSSODescriptor spSsoDescriptor = spEntityDescriptor.getSPSSODescriptor(SAML2_PROTOCOL);
			List<AssertionConsumerService> assertionConsumerServices = spSsoDescriptor.getAssertionConsumerServices();
			if (assertionConsumerServices != null) {
				for (AssertionConsumerService assertionConsumerService : assertionConsumerServices) {
					if (assertionConsumerService.getIndex().equals(assertionConsumerServiceIndex)) {
						assertionConsumerServiceUrl = assertionConsumerService.getLocation();
						break;
					}
				}
			}

			// Caricamento IDP da entityID
			AuthnRequest buildAuthenticationRequest = buildAuthenticationRequest(assertionConsumerServiceUrl, assertionConsumerServiceIndex, spEntityDescriptor.getEntityID(), id, destination);
			String encodedAuthnRequest = spidIntegrationUtil.printAuthnRequest(buildAuthenticationRequest);

			// TODO caricare da metadati SP
			authRequest.setDestinationUrl(destination);
			authRequest.setXmlAuthRequest(encodedAuthnRequest);
		}
		catch (ResourceException | MetadataProviderException e) {
			throw new IntegrationServiceException(e);
		}
	}

	/**
	 * @param entityId
	 * @param resource
	 * @return
	 * @throws MetadataProviderException
	 */
	private IDPSSODescriptor getIDPSSODescriptor(String entityId, ClasspathResource resource) throws MetadataProviderException {
		EntityDescriptor entityDescriptor = getEntityDescriptor(entityId, resource);
		return entityDescriptor.getIDPSSODescriptor(SAML2_PROTOCOL);
	}

	/**
	 * @param entityId
	 * @param resource
	 * @return
	 * @throws MetadataProviderException
	 */
	private EntityDescriptor getEntityDescriptor(String entityId, ClasspathResource resource) throws MetadataProviderException {
		AbstractReloadingMetadataProvider abstractReloadingMetadataProvider = new ResourceBackedMetadataProvider(new Timer(), resource);
		BasicParserPool parser = new BasicParserPool();
		parser.setNamespaceAware(true);
		abstractReloadingMetadataProvider.setParserPool(parser);
		abstractReloadingMetadataProvider.initialize();
		return abstractReloadingMetadataProvider.getEntityDescriptor(entityId);
	}

	private String retrieveXMLResourcePath(String entityId) throws IntegrationServiceException {

		Properties properties = new Properties();
		try (InputStream propertiesInputStream = getClass().getResourceAsStream("/idplist.properties")) {
			properties.load(propertiesInputStream);
			String keysProperty = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_KEYS);
			String[] keys = keysProperty.split(",");
			for (String key : keys) {
				String entityIdFromProperties = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".entityId");
				if (entityId.equals(entityIdFromProperties)) {
					return properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".file");
				}

			}
		} catch (IOException e) {
			throw new IntegrationServiceException(e);
		}

		throw new IntegrationServiceException("Metadata file not found for the specified entityId.");
	}

	public AuthnRequest buildAuthenticationRequest(String assertionConsumerServiceUrl, Integer assertionConsumerServiceIndex, String issuerId, String id, String destination) {
		DateTime issueInstant = new DateTime();
		AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();

		AuthnRequest authRequestResponse = authRequestBuilder.buildObject(SAML2_PROTOCOL, "AuthnRequest", "samlp");
		authRequestResponse.setIsPassive(Boolean.FALSE);
		authRequestResponse.setIssueInstant(issueInstant);
		authRequestResponse.setProtocolBinding(SAML2_POST_BINDING);
		authRequestResponse.setAssertionConsumerServiceURL(assertionConsumerServiceUrl);
		authRequestResponse.setAssertionConsumerServiceIndex(assertionConsumerServiceIndex);
		authRequestResponse.setIssuer(buildIssuer(issuerId));
		authRequestResponse.setNameIDPolicy(buildNameIDPolicy());
		authRequestResponse.setRequestedAuthnContext(buildRequestedAuthnContext());
		authRequestResponse.setID(id);
		authRequestResponse.setVersion(SAMLVersion.VERSION_20);

		authRequestResponse.setAttributeConsumingServiceIndex(1);
		authRequestResponse.setDestination(destination);

		// firma la request
		authRequestResponse.setSignature(spidIntegrationUtil.getSignature());

		return authRequestResponse;
	}

	private RequestedAuthnContext buildRequestedAuthnContext() {

		// Create AuthnContextClassRef
		AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
		AuthnContextClassRef authnContextClassRef = authnContextClassRefBuilder.buildObject(SAML2_ASSERTION, "AuthnContextClassRef", "saml");
		authnContextClassRef.setAuthnContextClassRef(SAML2_PASSWORD_PROTECTED_TRANSPORT);

		// Create RequestedAuthnContext
		RequestedAuthnContextBuilder requestedAuthnContextBuilder = new RequestedAuthnContextBuilder();
		RequestedAuthnContext requestedAuthnContext = requestedAuthnContextBuilder.buildObject();
		requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
		requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);

		return requestedAuthnContext;
	}

	/**
	 * Costruisce lo issuer object
	 *
	 * @return Issuer object
	 */
	private Issuer buildIssuer(String issuerId) {
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject();
		issuer.setNameQualifier(issuerId);
		issuer.setFormat(SAML2_NAME_ID_POLICY);
		issuer.setValue(issuerId);
		return issuer;
	}

	/**
	 * Costruisce il NameIDPolicy object
	 *
	 * @return NameIDPolicy object
	 */
	private NameIDPolicy buildNameIDPolicy() {
		NameIDPolicy nameIDPolicy = new NameIDPolicyBuilder().buildObject();
		nameIDPolicy.setFormat(SAML2_NAME_ID_POLICY);
		return nameIDPolicy;
	}

	public AuthRequest getAuthenticationRequest() {
		return authRequest;
	}

}
