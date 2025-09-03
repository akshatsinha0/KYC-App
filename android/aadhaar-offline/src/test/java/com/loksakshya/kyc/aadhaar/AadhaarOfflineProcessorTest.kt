package com.loksakshya.kyc.aadhaar

import org.junit.Test
import org.junit.Assert.*

class AadhaarOfflineProcessorTest {
    
    @Test
    fun testConfigurationDefaults() {
        val prodConfig = AadhaarConfig.PRODUCTION
        assertTrue("Production should use strict validation", prodConfig.strictTrustValidation)
        assertTrue("Production should check expiry", prodConfig.checkCertificateExpiry)
        assertFalse("Production should not include detailed errors", prodConfig.includeDetailedErrors)
        
        val devConfig = AadhaarConfig.DEVELOPMENT
        assertFalse("Development should not use strict validation", devConfig.strictTrustValidation)
        assertFalse("Development should not check expiry", devConfig.checkCertificateExpiry)
        assertTrue("Development should include detailed errors", devConfig.includeDetailedErrors)
    }
    
    @Test
    fun testCustomConfiguration() {
        val customConfig = AadhaarConfig(
            strictTrustValidation = true,
            checkCertificateExpiry = false,
            customTrustChain = "custom-pem-content",
            includeDetailedErrors = true
        )
        
        assertTrue("Custom config should use strict validation", customConfig.strictTrustValidation)
        assertFalse("Custom config should not check expiry", customConfig.checkCertificateExpiry)
        assertEquals("Custom trust chain should be set", "custom-pem-content", customConfig.customTrustChain)
        assertTrue("Custom config should include detailed errors", customConfig.includeDetailedErrors)
    }
    
    @Test
    fun testInvalidZipHandling() {
        val invalidZip = "not-a-zip-file".toByteArray()
        val result = AadhaarOfflineProcessor.verifyAndDecrypt(
            invalidZip, 
            "1234", 
            null, 
            AadhaarConfig.TESTING
        )
        
        assertFalse("Invalid ZIP should fail", result.ok)
        assertEquals("Should return bad_zip error", "bad_zip", result.error)
    }
    
    @Test
    fun testEmptyZipHandling() {
        // Create minimal ZIP structure (this would need actual ZIP bytes in real test)
        val emptyZip = ByteArray(0)
        val result = AadhaarOfflineProcessor.verifyAndDecrypt(
            emptyZip, 
            "1234", 
            null, 
            AadhaarConfig.TESTING
        )
        
        assertFalse("Empty ZIP should fail", result.ok)
        assertEquals("Should return bad_zip error", "bad_zip", result.error)
    }
}