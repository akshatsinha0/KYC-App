package com.loksakshya.kyc.sdk

import android.content.Context
import com.loksakshya.kyc.crypto.Hash
import com.loksakshya.kyc.sync.KycSyncWorker
import org.json.JSONObject
import java.util.UUID

object KycSdk{
  // simple local stub. Real impl will use encrypted storage and WorkManager
  fun createSession(ctx:Context,method:String):KycSession{
    val now=System.currentTimeMillis()
    return KycSession(UUID.randomUUID().toString(),method,"created",now,now)
  }
  fun enqueueFinalize(ctx:Context,baseUrl:String,session:KycSession,payload:JSONObject){
    val digest=Hash.hex(Hash.sha256(payload.toString().toByteArray()))
    val url=baseUrl.trimEnd('/')+"/v1/kyc/sessions/"+session.id+"/finalize"
    KycSyncWorker.enqueue(ctx,session.id,url,JSONObject().apply{ put("resultDigest",digest) },digest.take(32))
  }
}

