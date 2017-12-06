package it.italia.developers.spid.integration.service.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.italia.developers.spid.integration.Application;
import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import it.italia.developers.spid.integration.model.AuthRequest;
import it.italia.developers.spid.integration.model.IdpEntry;
import it.italia.developers.spid.integration.service.SPIDIntegrationService;
import it.italia.developers.spid.integration.util.SPIDIntegrationUtil;

/**
 * @author Gianluca Pindinelli
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
public class SPIDIntegrationServiceTest {

	private final Logger log = LoggerFactory.getLogger(SPIDIntegrationServiceTest.class.getName());

	@Autowired
	private SPIDIntegrationService spidIntegrationService;

	@Autowired
	private SPIDIntegrationUtil spidIntegrationUtil;

	@Test
	public void buildAuthenticationRequestTest() {
		try {
			AuthRequest authRequest = spidIntegrationService.buildAuthenticationRequest("idp.spid.gov.it", 0);
			Assert.assertNotNull(authRequest.getXmlAuthRequest());
			System.out.println();
		}
		catch (IntegrationServiceException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void getAllIdpEntryTest() {
		try {
			List<IdpEntry> idpEntries = spidIntegrationService.getAllIdpEntry();
			Assert.assertTrue(idpEntries.size() > 0);
		}
		catch (IntegrationServiceException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

}
