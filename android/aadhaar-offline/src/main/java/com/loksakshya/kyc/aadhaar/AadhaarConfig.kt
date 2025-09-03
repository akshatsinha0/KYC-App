package com.loksakshya.kyc.aadhaar

/**
 * Configuration for Aadhaar Offline verification
 */
data class AadhaarConfig(
    /**
     * Whether to enforce strict certificate trust chain validation
     * When true: Only UIDAI-signed certificates are accepted
     * When false: Basic signature validation only (less secure)
     */
    val strictTrustValidation: Boolean = true,
    
    /**
     * Whether to allow expired certificates
     * When true: Certificates past their expiry date are rejected
     * When false: Expired certificates are accepted (not recommended)
     */
    val checkCertificateExpiry: Boolean = true,
    
    /**
     * Custom trust chain PEM content (optional)
     * If provided, this overrides the embedded UIDAI certificates
     */
    val customTrustChain: String? = null,
    
    /**
     * Whether to include detailed error information in results
     * Useful for debugging but may expose sensitive information
     */
    val includeDetailedErrors: Boolean = false
) {
    companion object {
        /**
         * Production configuration with maximum security
         */
        val PRODUCTION = AadhaarConfig(
            strictTrustValidation = true,
            checkCertificateExpiry = true,
            customTrustChain = null,
            includeDetailedErrors = false
        )
        
        /**
         * Development configuration with relaxed validation for testing
         */
        val DEVELOPMENT = AadhaarConfig(
            strictTrustValidation = false,
            checkCertificateExpiry = false,
            customTrustChain = null,
            includeDetailedErrors = true
        )
        
        /**
         * Testing configuration for unit tests with mock certificates
         */
        val TESTING = AadhaarConfig(
            strictTrustValidation = true,
            checkCertificateExpiry = false,
            customTrustChain = null,
            includeDetailedErrors = true
        )
    }
}