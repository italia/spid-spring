package it.italia.developers.spid.integration.service.test;

import it.italia.developers.spid.integration.Application;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Element;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Gianluca Pindinelli
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Application.class })
public class SPIDIntegrationServiceTest {

	private final Logger log = LoggerFactory.getLogger(SPIDIntegrationServiceTest.class.getName());

	@Autowired
	private SPIDIntegrationService spidIntegrationService;

	@Autowired
	private SPIDIntegrationUtil spidIntegrationUtil;

	@Test
	public void shouldBuildAuthRequestWhenGiveValidRequest() throws IntegrationServiceException {

			AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest("idp.spid.gov.it", 0);
			assertThat(authRequest.getXmlAuthRequest()).isNotNull();
	}

	@Test
	public void shouldGetSpecificXMLWhenCallAuthRequest() throws Exception {
		String expectedIssuer = "https://spid.lecce.it";


			AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest("idp.spid.gov.it", 0);
			String result = spidIntegrationUtil.decode(authRequest.getXmlAuthRequest());

			Element resultElement = spidIntegrationUtil.xmlStringToElement(result);

			assertThat(resultElement.getElementsByTagName("saml2:Issuer").item(0).getTextContent()).isEqualTo(expectedIssuer);


	}

	@Test
	public void shouldGetIdWhenCallAuthRequest() throws Exception {

		LocalDate dateToday = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
		String formattedString = dateToday.format(formatter);

		String IDPEntityId = "idp.spid.gov.it";
		int assertionConsumerServiceIndex = 0;
		AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest(IDPEntityId, assertionConsumerServiceIndex);
		String result = spidIntegrationUtil.decode(authRequest.getXmlAuthRequest());

		Element resultElement = spidIntegrationUtil.xmlStringToElement(result);
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(resultElement.getAttributes().getNamedItem("ID")).isNotNull();
			softly.assertThat(resultElement.getAttributes().getNamedItem("IssueInstant").getTextContent()).contains(formattedString);
			softly.assertThat(resultElement.getAttributes().getNamedItem("Version").getTextContent()).isEqualTo("2.0");
			softly.assertThat(resultElement.getAttributes().getNamedItem("Destination").getTextContent()).contains(IDPEntityId);
			softly.assertThat(resultElement.getAttributes().getNamedItem("AssertionConsumerServiceIndex").getTextContent()).isEqualTo(String.valueOf(assertionConsumerServiceIndex));
			softly.assertThat(resultElement.getAttributes().getNamedItem("ProtocolBinding").getTextContent()).isEqualTo("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
			softly.assertThat(resultElement.getAttributes().getNamedItem("IsPassive")).isNull();
		});
	}

	@Test
	public void shouldRetrieveListOfIdpWhenCallListIDP() throws Exception{

		List<IdpEntry> idpEntries = spidIntegrationService.getAllIdpEntry();

		assertThat(idpEntries).hasSizeGreaterThan(0);

	}



}
