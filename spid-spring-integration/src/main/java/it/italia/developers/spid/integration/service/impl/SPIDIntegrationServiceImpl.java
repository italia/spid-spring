package it.italia.developers.spid.integration.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.stereotype.Service;

import it.italia.developers.spid.integration.config.SAMLConfig;
import it.italia.developers.spid.integration.config.SAMLContext;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.model.ResponseDecoded;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.integration.util.AuthenticationInfoExtractor;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class SPIDIntegrationServiceImpl implements SPIDIntegrationService {
	private final Logger log = LoggerFactory.getLogger(SPIDIntegrationServiceImpl.class.getName());

	private static final String SPID_SPRING_INTEGRATION_IDP_PREFIX = "spid.spring.integration.idp.";
	private static final String SPID_SPRING_INTEGRATION_IDP_KEYS = "spid.spring.integration.idp.keys";

	@Value("${spid.spring.integration.sp.assertionConsumerServiceUrl}")
	private String assertionConsumerServiceUrl;

	@Value("${spid.spring.integration.sp.issuerId}")
	private String issuerId;

	@Autowired
	SPIDIntegrationUtil spidIntegrationUtil;

	@Override
	public AuthRequest buildAuthenticationRequest(final String entityId, final int assertionConsumerServiceIndex) throws IntegrationServiceException {
		AuthenticationInfoExtractor authenticationInfoExtractor = new AuthenticationInfoExtractor(entityId, spidIntegrationUtil, assertionConsumerServiceIndex);
		AuthRequest authRequest = authenticationInfoExtractor.getAuthenticationRequest();
		return authRequest;
	}

	@Override
	public List<IdpEntry> getAllIdpEntry() throws IntegrationServiceException {
		List<IdpEntry> idpEntries = new ArrayList<>();

		Properties properties = new Properties();
		try (InputStream propertiesInputStream = getClass().getResourceAsStream("/idplist.properties")) {
			properties.load(propertiesInputStream);
			idpEntries = propertiesToIdPEntry(properties);
		}
		catch (FileNotFoundException e) {
			throw new IntegrationServiceException(e);
		}
		catch (IOException e) {
			throw new IntegrationServiceException(e);
		}
		return idpEntries;
	}

	private List<IdpEntry> propertiesToIdPEntry(final Properties properties) {
		List<IdpEntry> idpEntries = new ArrayList<>();

		String keysProperty = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_KEYS);
		String[] keys = keysProperty.split(",");
		for (String key : keys) {
			IdpEntry idpEntry = new IdpEntry();
			String name = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".name");
			idpEntry.setName(name);
			String imageUrl = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".imageUrl");
			idpEntry.setImageUrl(imageUrl);
			String entityId = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".entityId");
			idpEntry.setEntityId(entityId);
			idpEntry.setIdentifier(key);
			idpEntries.add(idpEntry);
		}

		return idpEntries;
	}

	@Override
	public ResponseDecoded processAuthenticationResponse(
			final HttpServletRequest request, final HttpServletResponse response)
					throws IntegrationServiceException {

		// TODO: configurazione SAML
		SAMLConfig saml2Config = new SAMLConfig();

		SAMLCredential credential = null;
		try {
			SAMLContext context = new SAMLContext(request, saml2Config);
			SAMLMessageContext messageContext = context.createSamlMessageContext(request, response);

			// Process response
			context.getSamlProcessor().retrieveMessage(messageContext);

			messageContext
					.setLocalEntityEndpoint(SAMLUtil.getEndpoint(messageContext.getLocalEntityRoleMetadata().getEndpoints(),
							messageContext.getInboundSAMLBinding(), new HttpServletRequestAdapter(request)));
			messageContext.getPeerEntityMetadata().setEntityID(saml2Config.getIdpEntityId());

			WebSSOProfileConsumer consumer = new WebSSOProfileConsumerImpl(context.getSamlProcessor(),
					context.getMetadataManager());
			credential = consumer.processAuthenticationResponse(messageContext);
		}
		catch (Exception e) {
			log.error("Errore di lettura delle credenziali SAML.", e);
			throw new IntegrationServiceException("Errore di lettura delle credenziali SAML.", e);
		}



		ResponseDecoded retVal = new ResponseDecoded();
		retVal.setCodiceIdentificativo(credential.getAttributeAsString("spidCode"));
		retVal.setNome(credential.getAttributeAsString("name"));
		retVal.setCognome(credential.getAttributeAsString("familyName"));

		retVal.setLuogoNascita(credential.getAttributeAsString("placeOfBirth"));
		retVal.setProvinciaNascita(credential.getAttributeAsString("countyOfBirth"));

		// TODO
		// retVal.setDataNascita(credential.getAttributeAsString("dateOfBirth"));

		retVal.setSesso(credential.getAttributeAsString("gender"));
		retVal.setRagioneSociale(credential.getAttributeAsString("companyName"));

		retVal.setIndirizzoSedeLegale(credential.getAttributeAsString("registeredOffice"));
		retVal.setCodiceFiscale(credential.getAttributeAsString("fiscalNumber"));
		retVal.setPartitaIva(credential.getAttributeAsString("ivaCode"));

		retVal.setDocumentoIdentita(credential.getAttributeAsString("idCard"));

		// TODO
		// retVal.setDataScadenzaIdentita(credential.getAttributeAsString("expirationDate"));

		retVal.setIndirizzoDomicilio(credential.getAttributeAsString("address"));
		retVal.setNumeroTelefono(credential.getAttributeAsString("mobilePhone"));
		retVal.setEmailAddress(credential.getAttributeAsString("email"));
		retVal.setEmailPec(credential.getAttributeAsString("digitalAddress"));


		return retVal;
	}


}
