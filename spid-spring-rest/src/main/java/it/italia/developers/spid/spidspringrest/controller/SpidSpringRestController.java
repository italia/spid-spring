package it.italia.developers.spid.spidspringrest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.ResponseDecoded;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.spidspringrest.model.ExtraInfo;
import it.italia.developers.spid.spidspringrest.model.SpidProviders;

@RestController()
public class SpidSpringRestController {

    private final Logger log = LoggerFactory.getLogger(SpidSpringRestController.class.getName());

    @Value("${spid.spring.rest.assertionConsumerServiceIndex}")
    private Integer assertionConsumerServiceIndex;

    @Autowired
    private SPIDIntegrationService spidIntegrationService;

    @ApiOperation(value = "Elenco Providers SPID", notes = "Servizio REST per ottenere l'elenco dei provider abilitati", response = SpidProviders.class)
    @GetMapping(value = "list-providers")
    public SpidProviders listIdProviders() throws IntegrationServiceException {
        SpidProviders retVal = new SpidProviders();
        retVal.setIdentityProviders(spidIntegrationService.idpEntries());
        retVal.setExtraInfo(EXTRA_INFO);
        return retVal;
    }

    @ApiOperation(value = "Generazione della richiesta di autorizzazione", notes = "Servizio REST per generare la richiesta di autorizzazione", response = AuthRequest.class)
    @GetMapping(value = "auth-spid")
    public AuthRequest authRequest(@RequestParam(name = "entityId", required = true) @ApiParam(value = "Entity Id dell'Idp", required = true) final String entityId)
        throws IntegrationServiceException {

        return spidIntegrationService.buildAuthenticationRequest(entityId, assertionConsumerServiceIndex);
    }

    @ApiOperation(value = "Decodifica della risposta di autorizzazione", notes = "Servizio rest per decodificare i dati dell'utente autenticato", response = ResponseDecoded.class)
    @PostMapping(value = "send-response",consumes = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseDecoded decodeResponse(@ApiParam(value = "Valore cifrato della Response di Spid", required = true) @RequestBody final String responseEncripted) {
        log.info(responseEncripted);

        ResponseDecoded retVal = new ResponseDecoded();
        retVal.setNome("TEST");
        retVal.setCognome("TEST");

        return retVal;
    }

    @PostMapping(value = "send-response-test")
    public ResponseDecoded decodeResponseTest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

        try {
            return spidIntegrationService.processAuthenticationResponse(request, response);
        } catch (Exception e) {
            log.error("Saml plugin error.", e);
            throw new ServletException(e);
        }
    }

    private static final List<ExtraInfo> EXTRA_INFO = new ArrayList<>();

    static {
        EXTRA_INFO.add(new ExtraInfo("Maggiori informazioni", "https://www.spid.gov.it/"));
        EXTRA_INFO.add(new ExtraInfo("Non hai SPID?", "https://www.spid.gov.it/richiedi-spid"));
        EXTRA_INFO.add(new ExtraInfo("Serve aiuto?", "https://www.spid.gov.it/serve-aiuto"));
    }
}
