package it.italia.developers.spid.integration.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoHelper;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;

/**
 * @author Gianluca Pindinelli
 *
 */
@Component
public class SPIDIntegrationUtil {

	private final Logger log = LoggerFactory.getLogger(SPIDIntegrationUtil.class.getName());

	@Value("${spid.spring.integration.keystore.certificate.alias}")
	private String certificateAliasName;

	@Value("${spid.spring.integration.keystore.path}")
	private String keystorePath;

	@Value("${spid.spring.integration.keystore.password}")
	private String keystorePassword;

	public SPIDIntegrationUtil() {
		try {
			DefaultBootstrap.bootstrap();
		}
		catch (ConfigurationException e) {
			log.error("SPIDIntegrationUtil :: " + e.getMessage(), e);
		}
	}

	/**
	 * Encode AuthNRequest.
	 *
	 * @param authnRequest
	 * @return
	 * @throws MarshallingException
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public String encodeAuthnRequest(AuthnRequest authnRequest, boolean compress) throws IntegrationServiceException {

		String requestMessage = printAuthnRequest(authnRequest);
		if(!compress) {
			return Base64.encodeBytes(requestMessage.getBytes(), Base64.DONT_BREAK_LINES);
		}
		Deflater deflater = new Deflater(Deflater.DEFLATED, true);
		ByteArrayOutputStream byteArrayOutputStream = null;
		DeflaterOutputStream deflaterOutputStream = null;

		String encodedRequestMessage;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);
			deflaterOutputStream.write(requestMessage.getBytes()); // compressing
			deflaterOutputStream.close();

			encodedRequestMessage = Base64.encodeBytes(byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);

			encodedRequestMessage = URLEncoder.encode(encodedRequestMessage, "UTF-8").trim(); // encoding string
		}
		catch (UnsupportedEncodingException e) {
			log.error("encodeAndPrintAuthnRequest :: " + e.getMessage(), e);
			throw new IntegrationServiceException(e);
		}
		catch (IOException e) {
			log.error("encodeAndPrintAuthnRequest :: " + e.getMessage(), e);
			throw new IntegrationServiceException(e);
		}

		return encodedRequestMessage;

	}

	/**
	 * Print AuthnRequest.
	 *
	 * @param authnRequest
	 * @return
	 * @throws MarshallingException
	 */
	public String printAuthnRequest(AuthnRequest authnRequest) throws IntegrationServiceException {

		Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(authnRequest); // object to DOM converter
		Element authDOM;
		try {
			authDOM = marshaller.marshall(authnRequest);
		}
		catch (MarshallingException e) {
			log.error("printAuthnRequest :: " + e.getMessage(), e);
			throw new IntegrationServiceException(e);
		}
		
		try {
			Signer.signObject(authnRequest.getSignature());
		} catch (SignatureException e) {
			log.error("There was an error while signing the request", e);
			throw new IntegrationServiceException(e);
		}
		
		// converting to a DOM
		StringWriter requestWriter = new StringWriter();
		requestWriter = new StringWriter();
		XMLHelper.writeNode(authDOM, requestWriter);
		String authnRequestString = requestWriter.toString(); // DOM to string

		return authnRequestString;

	}

	public Element xmlStringToElement(String xmlData) throws SAXException, IOException, ParserConfigurationException {
		InputStream xmlByteArrayInputStream = new ByteArrayInputStream(xmlData.getBytes());
		Element node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlByteArrayInputStream).getDocumentElement();

		return node;
	}

	public Credential getCredential() {

		KeyStore ks = getKeyStore();

		// Get Private Key Entry From Certificate
		KeyStore.PrivateKeyEntry pkEntry = null;
		try {
			pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(certificateAliasName, new KeyStore.PasswordProtection(keystorePassword.toCharArray()));
		}
		catch (NoSuchAlgorithmException e) {
			log.error("Failed to Get Private Entry From the keystore", e);
		}
		catch (UnrecoverableEntryException e) {
			log.error("Failed to Get Private Entry From the keystore", e);
		}
		catch (KeyStoreException e) {
			log.error("Failed to Get Private Entry From the keystore", e);
		}
		PrivateKey pk = pkEntry.getPrivateKey();

		X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
		BasicX509Credential credential = new BasicX509Credential();
		credential.setEntityCertificate(certificate);
		credential.setPrivateKey(pk);

		return credential;
	}

	public KeyStore getKeyStore() {

		KeyStore ks = null;
		char[] password = keystorePassword.toCharArray();

		// Get Default Instance of KeyStore
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
		}
		catch (KeyStoreException e) {
			log.error("Error while Intializing Keystore", e);
		}

		// Load KeyStore from input stream
		try (InputStream keystoreInputStream = getClass().getResourceAsStream(keystorePath)) {
			ks.load(keystoreInputStream, password);
		}
		catch (NoSuchAlgorithmException e) {
			log.error("Failed to Load the KeyStore:: ", e);
		}
		catch (CertificateException e) {
			log.error("Failed to Load the KeyStore:: ", e);
		}
		catch (IOException e) {
			log.error("Failed to Load the KeyStore:: ", e);
		}

		return ks;
	}

	/**
	 * @return
	 */
	public Signature getSignature() {

		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

		Signature signature = (Signature) builderFactory.getBuilder(Signature.DEFAULT_ELEMENT_NAME).buildObject(Signature.DEFAULT_ELEMENT_NAME);
		signature.setSigningCredential(getCredential());
		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		KeyInfo keyInfo = (KeyInfo) builderFactory.getBuilder(KeyInfo.DEFAULT_ELEMENT_NAME).buildObject(KeyInfo.DEFAULT_ELEMENT_NAME);

		KeyStore ks = getKeyStore();
		try {
			X509Certificate certificate = (X509Certificate) ks.getCertificate(certificateAliasName);
			KeyInfoHelper.addCertificate(keyInfo, certificate);
		}
		catch (CertificateEncodingException e) {
			log.error("buildAuthenticationRequest :: " + e.getMessage(), e);
		}
		catch (KeyStoreException e) {
			log.error("buildAuthenticationRequest :: " + e.getMessage(), e);
		}
		catch (IllegalArgumentException e) {
			log.error("buildAuthenticationRequest :: " + e.getMessage(), e);
		}

		signature.setKeyInfo(keyInfo);

		return signature;
	}

}
