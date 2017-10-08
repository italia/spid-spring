package it.italia.developers.spid.integration.config;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataMemoryProvider;

public class IdpMetadataGenerator {
	private XMLObjectBuilderFactory builderFactory;

	public IdpMetadataGenerator() {
		this.builderFactory = Configuration.getBuilderFactory();
	}

	@SuppressWarnings("unchecked")
	public MetadataProvider generate(final SAMLConfig configuration) throws MetadataProviderException, ResourceException  {
		SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder<EntityDescriptor>) builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        EntityDescriptor descriptor = builder.buildObject();
        descriptor.setID(configuration.getIdpEntityId());
        descriptor.setEntityID(configuration.getIdpEntityId());
        descriptor.getRoleDescriptors().add(buildIDPSSODescriptor(configuration));

        MetadataMemoryProvider memoryProvider = new MetadataMemoryProvider(descriptor);
        memoryProvider.initialize();

        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
    	extendedMetadata.setSigningKey(configuration.getIdpEntityId());

    	ExtendedMetadataDelegate idpMetadataProvider = new ExtendedMetadataDelegate(memoryProvider, extendedMetadata);
    	idpMetadataProvider.setMetadataRequireSignature(false);
    	idpMetadataProvider.initialize();

    	return idpMetadataProvider;
	}

	@SuppressWarnings("unchecked")
	private IDPSSODescriptor buildIDPSSODescriptor(final SAMLConfig configuration) {
        SAMLObjectBuilder<IDPSSODescriptor> builder = (SAMLObjectBuilder<IDPSSODescriptor>) builderFactory.getBuilder(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        IDPSSODescriptor idpDescriptor = builder.buildObject();
        idpDescriptor.setWantAuthnRequestsSigned(false);
        idpDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);

        idpDescriptor.getSingleSignOnServices().add(getSingleSignOnService(configuration, SAMLConstants.SAML2_REDIRECT_BINDING_URI));
        idpDescriptor.getSingleLogoutServices().add(getSingleLogoutService(configuration, SAMLConstants.SAML2_REDIRECT_BINDING_URI));

        return idpDescriptor;

    }

	@SuppressWarnings("unchecked")
	private SingleSignOnService getSingleSignOnService(final SAMLConfig configuration, final String binding) {
		SAMLObjectBuilder<SingleSignOnService> builder = (SAMLObjectBuilder<SingleSignOnService>) builderFactory.getBuilder(SingleSignOnService.DEFAULT_ELEMENT_NAME);
		SingleSignOnService service = builder.buildObject();
		service.setBinding(binding);
		service.setLocation(configuration.getLoginUrl());

		return service;
	}

	@SuppressWarnings("unchecked")
	private SingleLogoutService getSingleLogoutService(final SAMLConfig configuration, final String binding) {
		SAMLObjectBuilder<SingleLogoutService> builder = (SAMLObjectBuilder<SingleLogoutService>) builderFactory.getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
		SingleLogoutService service = builder.buildObject();
		service.setBinding(binding);
		service.setLocation(configuration.getLogoutUrl());

		return service;
	}
}
