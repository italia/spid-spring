package it.italia.developers.spid.integration.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.model.ResponseDecoded;

/**
 * @author Gianluca Pindinelli
 *
 */
public interface SPIDIntegrationService {

	/**
	 * Costruisce l'oggetto Saml2 AuthNRequest a partire dall'entityID dell'i-esimo IDP.
	 *
	 * @param entityId
	 * @param assertionConsumerServiceIndex
	 * @return
	 */
	AuthRequest buildAuthenticationRequest(String entityId, int assertionConsumerServiceIndex) throws IntegrationServiceException;

	/**
	 * Carica la lista di tutti gli IDP presenti.
	 *
	 * @return
	 * @throws IntegrationServiceException
	 */
	List<IdpEntry> getAllIdpEntry() throws IntegrationServiceException;

	/**
	 * Costruisce la risposta decodificata a partire dall'auth response SAML2.
	 *
	 * @param authResponse
	 * @return
	 * @throws IntegrationServiceException
	 */
	ResponseDecoded processAuthenticationResponse(HttpServletRequest request, HttpServletResponse response) throws IntegrationServiceException;

}
