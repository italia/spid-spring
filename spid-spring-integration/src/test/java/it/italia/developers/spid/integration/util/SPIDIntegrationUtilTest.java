package it.italia.developers.spid.integration.util;

import it.italia.developers.spid.integration.Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
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

                  assertThat( node.getNodeName()).isEqualTo("md:EntityDescriptor");


            } catch (SAXException | IOException | ParserConfigurationException e) {
                  e.printStackTrace();
                  fail("Exception "+e.getMessage());
            }
      }
}
