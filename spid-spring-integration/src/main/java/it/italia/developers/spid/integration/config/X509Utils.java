package it.italia.developers.spid.integration.config;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class X509Utils {

	public static X509Certificate generateX509Certificate(final String certificateStr) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificateStr.trim().getBytes()));
	}
}
