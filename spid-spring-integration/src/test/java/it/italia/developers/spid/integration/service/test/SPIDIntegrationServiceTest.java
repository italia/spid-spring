package it.italia.developers.spid.integration.service.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import it.italia.developers.spid.integration.Application;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

/**
 * @author Gianluca Pindinelli
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Application.class})
class SPIDIntegrationServiceTest {

    private final Logger log = LoggerFactory.getLogger(SPIDIntegrationServiceTest.class.getName());

    @Autowired
    private SPIDIntegrationService spidIntegrationService;

    @Autowired
    private SPIDIntegrationUtil spidIntegrationUtil;

    @Test
    void shouldBuildAuthRequestWhenGiveValidRequest() throws IntegrationServiceException {

        AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest("idp.spid.gov.it", 0);
        assertThat(authRequest.getXmlAuthRequest()).isNotNull();
    }

    @Test
    void shouldGetSpecificXMLWhenCallAuthRequest() throws Exception {
        String expectedIssuer = "https://spid.lecce.it";

        AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest("idp.spid.gov.it", 0);
        String result = authRequest.getXmlAuthRequest();

        Element resultElement = spidIntegrationUtil.xmlStringToElement(result);

        assertThat(resultElement.getElementsByTagName("saml2:Issuer").item(0).getTextContent()).isEqualTo(expectedIssuer);
    }

    @Test
    void shouldGetIdWhenCallAuthRequest() throws Exception {

        LocalDate dateToday = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        String formattedString = dateToday.format(formatter);

        String IDPEntityId = "idp.spid.gov.it";
        int assertionConsumerServiceIndex = 0;
        AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest(IDPEntityId, assertionConsumerServiceIndex);
        String xmlAuthRequest = authRequest.getXmlAuthRequest();

        Element resultElement = spidIntegrationUtil.xmlStringToElement(xmlAuthRequest);
        NamedNodeMap actualAttributes = resultElement.getAttributes();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actualAttributes.getNamedItem("ID")).isNotNull();
            softly.assertThat(actualAttributes.getNamedItem("IssueInstant").getTextContent()).contains(formattedString);
            softly.assertThat(actualAttributes.getNamedItem("Version").getTextContent()).isEqualTo("2.0");
            softly.assertThat(actualAttributes.getNamedItem("Destination").getTextContent()).contains(IDPEntityId);
            softly.assertThat(actualAttributes.getNamedItem("AssertionConsumerServiceIndex").getTextContent()).isEqualTo(String.valueOf(assertionConsumerServiceIndex));
            softly.assertThat(actualAttributes.getNamedItem("ProtocolBinding").getTextContent()).isEqualTo("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
            softly.assertThat(actualAttributes.getNamedItem("IsPassive").getTextContent()).isEqualTo("false");
        });
    }

    @Test
    void shouldRetrieveListOfIdpWhenCallListIDP() throws Exception {
        assertThat(spidIntegrationService.idpEntries()).hasSizeGreaterThan(0);
    }
}
