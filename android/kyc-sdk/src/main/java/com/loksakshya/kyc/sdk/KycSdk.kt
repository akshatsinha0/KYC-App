package com.loksakshya.kyc.sdk

import android.content.Context
import android.provider.Settings
import com.loksakshya.kyc.crypto.Hash
import com.loksakshya.kyc.crypto.Signer
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
    val deviceId=Settings.Secure.getString(ctx.contentResolver,Settings.Secure.ANDROID_ID)?:"unknown"
    val signature=Signer().signToBase64(digest.toByteArray(Charsets.UTF_8))
    val url=baseUrl.trimEnd('/')+"/v1/kyc/sessions/"+session.id+"/finalize"
    val body=JSONObject().apply{
      put("resultDigest",digest)
      put("signature",signature)
      put("deviceId",deviceId)
    }
    KycSyncWorker.enqueue(ctx,session.id,url,body,digest.take(32))
  }
}

