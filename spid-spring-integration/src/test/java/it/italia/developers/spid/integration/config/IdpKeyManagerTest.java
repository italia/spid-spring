package it.italia.developers.spid.integration.config;

import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensaml.xml.security.credential.Credential;

import java.security.cert.Certificate;

import static org.assertj.core.api.Assertions.assertThat;

class IdpKeyManagerTest {

    static IdpKeyManager idpKeyManager;

    @SneakyThrows
    @BeforeAll
     static void startup(){
        String entity ="test.idp.entity";
        String certificateStr = "-----BEGIN CERTIFICATE-----\n" +
                "MIICmDCCAgGgAwIBAgIBADANBgkqhkiG9w0BAQ0FADBpMQswCQYDVQQGEwJpdDEP\n" +
                "MA0GA1UECAwGVG9yaW5vMQ0wCwYDVQQKDARTcGlkMQ0wCwYDVQQDDARDYXRhMQ0w\n" +
                "CwYDVQQLDARzcGlkMRwwGgYJKoZIhvcNAQkBFg1mYWJlckBtYWlsLml0MB4XDTIx\n" +
                "MDcyNTE2MzA0M1oXDTIyMDcyNTE2MzA0M1owaTELMAkGA1UEBhMCaXQxDzANBgNV\n" +
                "BAgMBlRvcmlubzENMAsGA1UECgwEU3BpZDENMAsGA1UEAwwEQ2F0YTENMAsGA1UE\n" +
                "CwwEc3BpZDEcMBoGCSqGSIb3DQEJARYNZmFiZXJAbWFpbC5pdDCBnzANBgkqhkiG\n" +
                "9w0BAQEFAAOBjQAwgYkCgYEAxVPBzk/AHxSdsXhZ9kRbc9r2pUoRTDHEIts7BCjW\n" +
                "gJSEHjRKOJG58Sx+LMv9feoStNNsR7XN/mNGFliq6mRucui1cdc+M6lP//ZjR5Al\n" +
                "m4LRQhQiDXl2Hr5WD4+vgM3UH2zQTftawXBQO/VI8FHnKOMT55b4jtgSI12+U5OW\n" +
                "EQ0CAwEAAaNQME4wHQYDVR0OBBYEFIF6KK7ydM5V1rXlj/eh3RM7QuooMB8GA1Ud\n" +
                "IwQYMBaAFIF6KK7ydM5V1rXlj/eh3RM7QuooMAwGA1UdEwQFMAMBAf8wDQYJKoZI\n" +
                "hvcNAQENBQADgYEArHyy3+ju6aHDkfFmKIOhWEMCutUQiC7/8HXa+Y4EkHkf5nsO\n" +
                "/aU5lv9UoFPfSIGTPJKBC1mMYU9ybHXI8dRgBvbrKhp59jimBd9BknFB7qeJZW07\n" +
                "4TvnsfrDA6QC7Hon4Q9PB6ARbirg31T6xHq9XJpUMEGvV5GEqA2hKhJkkJk=\n" +
                "-----END CERTIFICATE-----";
        idpKeyManager = new IdpKeyManager(entity,certificateStr);
    }


    @Test
    void shouldReturnCredentialWhenKeyisNotNullAndKeyEqualEntity() {
        Credential credential = idpKeyManager.getCredential("test.idp.entity");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(credential).isNotNull();
            softly.assertThat(credential.getEntityId()).isEqualTo("test.idp.entity");
        });
    }
    @Test
    void shouldReturnNullCredentialWhenKeyisNull() {
        Credential credential = idpKeyManager.getCredential(null);
        assertThat(credential).isNull();
    }
    @Test
    void shouldReturnNullCredentialWhenKeyisNotEqualToEntity() {
        Credential credential = idpKeyManager.getCredential("test");
        assertThat(credential).isNull();
    }

    @Test
    void shouldReturnCertificateWhenKeyisNotNullAndKeyEqualEntity() {
        Certificate certificate = idpKeyManager.getCertificate("test.idp.entity");
        assertThat(certificate).isNotNull();
    }

    @Test
    void shouldReturnNullCertificateWhenKeyisNull() {
        Certificate certificate = idpKeyManager.getCertificate(null);
        assertThat(certificate).isNull();
    }
    @Test
    void shouldReturnNullCertificateWhenKeyisNotEqualToEntity() {
        Certificate certificate = idpKeyManager.getCertificate("test");
        assertThat(certificate).isNull();
    }

}