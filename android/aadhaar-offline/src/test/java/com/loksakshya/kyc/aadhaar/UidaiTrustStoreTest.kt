package com.loksakshya.kyc.aadhaar

import org.junit.Test
import org.junit.Assert.*
import java.io.ByteArrayInputStream

class UidaiTrustStoreTest {
    
    @Test
    fun testLoadPemCertificates() {
        val testPem = """
            -----BEGIN CERTIFICATE-----
            MIIFjTCCA3WgAwIBAgIEAJiWjDANBgkqhkiG9w0BAQsFADCBhTELMAkGA1UEBhMC
            SU4xCzAJBgNVBAgTAkRMMRAwDgYDVQQHEwdOZXcgRGVsaTEMMAoGA1UEChMDVUlE
            QUkxDDAKBgNVBAsTA1VJREFJMRcwFQYDVQQDEw5VSURBSSBSb290IENBMSIwIAYJ
            KoZIhvcNAQkBFhN1aWRhaUBuaWMuaW4gVUlEQUkwHhcNMTQwNzE0MDUzNzU5WhcN
            MjQwNzExMDUzNzU5WjCBhTELMAkGA1UEBhMCSU4xCzAJBgNVBAgTAkRMMRAwDgYD
            VQQHEwdOZXcgRGVsaTEMMAoGA1UEChMDVUlEQUkxDDAKBgNVBAsTA1VJREFJMRcw
            FQYDVQQDEw5VSURBSSBSb290IENBMSIwIAYJKoZIhvcNAQkBFhN1aWRhaUBuaWMu
            aW4gVUlEQUkwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC6VVQC7tBA
            gCgygDiQoKzlmgxKK6wbGwcp/cVVmwsGS7ANf4hGXBTXupnKoKqmMmFf7oUmwqkx
            vEXlrZac8kaVjqxOrmvfFXStlI2TsCCK2FrkivGgGpVasHO7fzAmjlqLdYcINWwc
            tW5kvFDyDFusLpjv0vnIjXYA4rAdkckZ5+9wfsKgSygUl9ksE7sUBKBgse/7/6kK
            BgkqhkiG9w0BAQsFAAOCAgEAuGqVYWI5MFWQdj9LQ1gxC7nBcHmhOmFRt+zrqQlY
            jfTp5o/N/k9usp2TVuRLKQjGpVnVelMNlc+JGK/s3q/MClLcZe4zgwSMlxqJ5Bnt
            L/Bo/hIYD7oBZMqfyqm1+Od5HhSahqPpf+0j4klIlHlqQDstdllL5lyxSn+qoXU=
            -----END CERTIFICATE-----
        """.trimIndent()
        
        val certificates = UidaiTrustStore.loadPemCertificates(testPem)
        
        assertEquals("Should load one certificate", 1, certificates.size)
        
        val cert = certificates[0]
        assertNotNull("Certificate should not be null", cert)
        assertTrue("Subject should contain UIDAI", cert.subjectDN.toString().contains("UIDAI"))
    }
    
    @Test
    fun testLoadMultiplePemCertificates() {
        val multiPem = """
            -----BEGIN CERTIFICATE-----
            MIIFjTCCA3WgAwIBAgIEAJiWjDANBgkqhkiG9w0BAQsFADCBhTELMAkGA1UEBhMC
            SU4xCzAJBgNVBAgTAkRMMRAwDgYDVQQHEwdOZXcgRGVsaTEMMAoGA1UEChMDVUlE
            QUkxDDAKBgNVBAsTA1VJREFJMRcwFQYDVQQDEw5VSURBSSBSb290IENBMSIwIAYJ
            KoZIhvcNAQkBFhN1aWRhaUBuaWMuaW4gVUlEQUkwHhcNMTQwNzE0MDUzNzU5WhcN
            MjQwNzExMDUzNzU5WjCBhTELMAkGA1UEBhMCSU4xCzAJBgNVBAgTAkRMMRAwDgYD
            VQQHEwdOZXcgRGVsaTEMMAoGA1UEChMDVUlEQUkxDDAKBgNVBAsTA1VJREFJMRcw
            FQYDVQQDEw5VSURBSSBSb290IENBMSIwIAYJKoZIhvcNAQkBFhN1aWRhaUBuaWMu
            aW4gVUlEQUkwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC6VVQC7tBA
            gCgygDiQoKzlmgxKK6wbGwcp/cVVmwsGS7ANf4hGXBTXupnKoKqmMmFf7oUmwqkx
            vEXlrZac8kaVjqxOrmvfFXStlI2TsCCK2FrkivGgGpVasHO7fzAmjlqLdYcINWwc
            tW5kvFDyDFusLpjv0vnIjXYA4rAdkckZ5+9wfsKgSygUl9ksE7sUBKBgse/7/6kK
            BgkqhkiG9w0BAQsFAAOCAgEAuGqVYWI5MFWQdj9LQ1gxC7nBcHmhOmFRt+zrqQlY
            jfTp5o/N/k9usp2TVuRLKQjGpVnVelMNlc+JGK/s3q/MClLcZe4zgwSMlxqJ5Bnt
            L/Bo/hIYD7oBZMqfyqm1+Od5HhSahqPpf+0j4klIlHlqQDstdllL5lyxSn+qoXU=
            -----END CERTIFICATE-----
            -----BEGIN CERTIFICATE-----
            MIIFjTCCA3WgAwIBAgIEAJiWjDANBgkqhkiG9w0BAQsFADCBhTELMAkGA1UEBhMC
            SU4xCzAJBgNVBAgTAkRMMRAwDgYDVQQHEwdOZXcgRGVsaTEMMAoGA1UEChMDVUlE
            QUkxDDAKBgNVBAsTA1VJREFJMRcwFQYDVQQDEw5VU0RBSSBJbnRlcm1lZGlhdGUx
            IjAgBgkqhkiG9w0BCQEWE3VpZGFpQG5pYy5pbiBVSURBSTAeFw0xNDA3MTQwNTM3
            NTlaFw0yNDA3MTEwNTM3NTlaMIGFMQswCQYDVQQGEwJJTjELMAkGA1UECBMCRE0x
            EDAOBgNVBAcTB05ldyBEZWxpMQwwCgYDVQQKEwNVSURBSTEMMAoGA1UECxMDVUlE
            QUkxFzAVBgNVBAMTDlVTREFJIEludGVybWVkaWF0ZTEiMCAGCSqGSIb3DQEJARYV
            dWlkYWlAbmljLmluIFVJREFJMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKC
            AgEAuGqVYWI5MFWQdj9LQ1gxC7nBcHmhOmFRt+zrqQlYjfTp5o/N/k9usp2TVuRL
            KQjGpVnVelMNlc+JGK/s3q/MClLcZe4zgwSMlxqJ5BntL/Bo/hIYD7oBZMqfyqm1
            +Od5HhSahqPpf+0j4klIlHlqQDstdllL5lyxSn+qoXUwggIKAoICAQC6VVQC7tBA
            gCgygDiQoKzlmgxKK6wbGwcp/cVVmwsGS7ANf4hGXBTXupnKoKqmMmFf7oUmwqkx
            vEXlrZac8kaVjqxOrmvfFXStlI2TsCCK2FrkivGgGpVasHO7fzAmjlqLdYcINWwc
            tW5kvFDyDFusLpjv0vnIjXYA4rAdkckZ5+9wfsKgSygUl9ksE7sUBKBgse/7/6kK
            BgkqhkiG9w0BAQsFAAOCAgEAuGqVYWI5MFWQdj9LQ1gxC7nBcHmhOmFRt+zrqQlY
            jfTp5o/N/k9usp2TVuRLKQjGpVnVelMNlc+JGK/s3q/MClLcZe4zgwSMlxqJ5Bnt
            L/Bo/hIYD7oBZMqfyqm1+Od5HhSahqPpf+0j4klIlHlqQDstdllL5lyxSn+qoXU=
            -----END CERTIFICATE-----
        """.trimIndent()
        
        val certificates = UidaiTrustStore.loadPemCertificates(multiPem)
        
        assertEquals("Should load two certificates", 2, certificates.size)
    }
    
    @Test
    fun testGetCertificateInfo() {
        val testPem = """
            -----BEGIN CERTIFICATE-----
            MIIFjTCCA3WgAwIBAgIEAJiWjDANBgkqhkiG9w0BAQsFADCBhTELMAkGA1UEBhMC
            SU4xCzAJBgNVBAgTAkRMMRAwDgYDVQQHEwdOZXcgRGVsaTEMMAoGA1UEChMDVUlE
            QUkxDDAKBgNVBAsTA1VJREFJMRcwFQYDVQQDEw5VSURBSSBSb290IENBMSIwIAYJ
            KoZIhvcNAQkBFhN1aWRhaUBuaWMuaW4gVUlEQUkwHhcNMTQwNzE0MDUzNzU5WhcN
            MjQwNzExMDUzNzU5WjCBhTELMAkGA1UEBhMCSU4xCzAJBgNVBAgTAkRMMRAwDgYD
            VQQHEwdOZXcgRGVsaTEMMAoGA1UEChMDVUlEQUkxDDAKBgNVBAsTA1VJREFJMRcw
            FQYDVQQDEw5VSURBSSBSb290IENBMSIwIAYJKoZIhvcNAQkBFhN1aWRhaUBuaWMu
            aW4gVUlEQUkwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQC6VVQC7tBA
            gCgygDiQoKzlmgxKK6wbGwcp/cVVmwsGS7ANf4hGXBTXupnKoKqmMmFf7oUmwqkx
            vEXlrZac8kaVjqxOrmvfFXStlI2TsCCK2FrkivGgGpVasHO7fzAmjlqLdYcINWwc
            tW5kvFDyDFusLpjv0vnIjXYA4rAdkckZ5+9wfsKgSygUl9ksE7sUBKBgse/7/6kK
            BgkqhkiG9w0BAQsFAAOCAgEAuGqVYWI5MFWQdj9LQ1gxC7nBcHmhOmFRt+zrqQlY
            jfTp5o/N/k9usp2TVuRLKQjGpVnVelMNlc+JGK/s3q/MClLcZe4zgwSMlxqJ5Bnt
            L/Bo/hIYD7oBZMqfyqm1+Od5HhSahqPpf+0j4klIlHlqQDstdllL5lyxSn+qoXU=
            -----END CERTIFICATE-----
        """.trimIndent()
        
        val certificates = UidaiTrustStore.loadPemCertificates(testPem)
        val cert = certificates[0]
        
        val info = UidaiTrustStore.getCertificateInfo(cert)
        
        assertTrue("Info should contain Subject", info.contains("Subject:"))
        assertTrue("Info should contain Issuer", info.contains("Issuer:"))
        assertTrue("Info should contain UIDAI", info.contains("UIDAI"))
    }
}