package it.italia.developers.spid.integration.util;

import it.italia.developers.spid.integration.exception.IntegrationServiceException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class IPMetadataExtractor {

	private static final String SPID_SPRING_INTEGRATION_IDP_PREFIX = "spid.spring.integration.idp.";
	private static final String SPID_SPRING_INTEGRATION_IDP_KEYS = "spid.spring.integration.idp.keys";

    public IPMetadataExtractor(String entityId) throws IntegrationServiceException {
        String xmlServiceMetadata = retrieveXMLServiceMetadata(entityId);
    }

    private String retrieveXMLServiceMetadata(String entityId) throws IntegrationServiceException {
		Properties properties = new Properties();
		try (InputStream propertiesInputStream = getClass().getResourceAsStream("/idplist.properties")) {
			properties.load(propertiesInputStream);
			String keysProperty = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_KEYS);
			String[] keys = keysProperty.split(",");
			for (String key : keys) {
				String entityIdFromProperties = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".entityId");
				String xmlServiceMetadata = null;
				if (entityId.equals(entityIdFromProperties)) {
					String xmlMetadataFileName = properties.getProperty(SPID_SPRING_INTEGRATION_IDP_PREFIX + key + ".file");
					xmlServiceMetadata = fileNameToContent(xmlMetadataFileName);
				}

				if (xmlServiceMetadata != null) {
					return xmlServiceMetadata;
				}
			}
		}
		catch (FileNotFoundException e) {
			throw new IntegrationServiceException(e);
		}
		catch (IOException e) {
			throw new IntegrationServiceException(e);
		}

		throw new IntegrationServiceException("Metadata file not found for the specified entityId.");
	}

	private String fileNameToContent(String fileName) throws IOException {
		try (InputStream resourceInputStream = getClass().getResourceAsStream("/metadata/idp/" + fileName)) {
			if (resourceInputStream == null) {
				return null;
			}

			try (Scanner scanner = new Scanner(resourceInputStream)) {
				String resourceContent = scanner.useDelimiter("\\Z").next();
				return resourceContent;
			}
		}
	}
}