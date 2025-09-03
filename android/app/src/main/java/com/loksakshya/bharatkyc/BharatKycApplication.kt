package com.loksakshya.bharatkyc

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

class BharatKycApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize WorkManager with custom configuration
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
            
        WorkManager.initialize(this, config)
    }
}