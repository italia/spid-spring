package it.italia.developers.spid.integration.util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import it.italia.developers.spid.integration.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
public class SPIDIntegrationUtilTest {

      @Autowired
      private SPIDIntegrationUtil spidIntegrationUtil;

      @Test
      public void xmlStringToXMLObjectTest() {

            ClassLoader classLoader = getClass().getClassLoader();
            File xmlFile = new File(classLoader.getResource("metadata/idp/telecom-metadata.xml").getFile());
            try (Scanner scanner = new Scanner(xmlFile)) {
                  String xmlData = scanner.useDelimiter("\\Z").next();
                  Element node = spidIntegrationUtil.xmlStringToElement(xmlData);

                  Assert.assertEquals("md:EntityDescriptor", node.getNodeName());

            } catch (SAXException | IOException | ParserConfigurationException e) {
                  e.printStackTrace();
                  Assert.fail();
            }
      }
}
