package com.loksakshya.bharatkyc

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.loksakshya.kyc.sync.KycSyncWorker
import com.loksakshya.kyc.sdk.KycSdk
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SyncStatusActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var retryButton: Button
    private lateinit var testButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync_status)
        
        statusText = findViewById(R.id.tv_sync_status)
        retryButton = findViewById(R.id.btn_retry_sync)
        testButton = findViewById(R.id.btn_test_sync)
        
        // Set up retry button
        retryButton.setOnClickListener {
            scheduleSyncWork()
        }
        
        // Set up test button to create a test sync job
        testButton.setOnClickListener {
            createTestSyncJob()
        }
        
        // Observe sync work status
        observeSyncWork()
        
        // Schedule initial sync work
        scheduleSyncWork()
    }
    
    private fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<KycSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "kyc_sync",
            ExistingPeriodicWorkPolicy.REPLACE,
            syncRequest
        )
        
        updateStatus("Sync scheduled with network and battery constraints")
    }
    
    private fun createTestSyncJob() {
        // Create a test session and enqueue a finalize job
        val session = KycSdk.createSession(this, "test")
        val payload = JSONObject().apply {
            put("testData", "mock_verification_result")
            put("timestamp", System.currentTimeMillis())
        }
        
        KycSdk.enqueueFinalize(this, BuildConfig.KYC_BASE_URL, session, payload)
        
        // Trigger immediate sync
        val immediateRequest = OneTimeWorkRequestBuilder<KycSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
            
        WorkManager.getInstance(this).enqueue(immediateRequest)
        
        updateStatus("Test sync job created and triggered")
    }    
   
 private fun observeSyncWork() {
        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData("kyc_sync")
            .observe(this, Observer { workInfos ->
                if (workInfos.isNotEmpty()) {
                    val workInfo = workInfos[0]
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> updateStatus("Sync work enqueued, waiting for constraints")
                        WorkInfo.State.RUNNING -> updateStatus("Sync work running...")
                        WorkInfo.State.SUCCEEDED -> updateStatus("Last sync completed successfully")
                        WorkInfo.State.FAILED -> updateStatus("Last sync failed, will retry automatically")
                        WorkInfo.State.BLOCKED -> updateStatus("Sync blocked, waiting for constraints")
                        WorkInfo.State.CANCELLED -> updateStatus("Sync cancelled")
                    }
                }
            })
    }
    
    private fun updateStatus(message: String) {
        statusText.text = "${System.currentTimeMillis() / 1000}: $message"
    }
}