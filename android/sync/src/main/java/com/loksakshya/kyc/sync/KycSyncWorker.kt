package com.loksakshya.kyc.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Headers
import com.loksakshya.kyc.crypto.CryptoManager
import com.loksakshya.kyc.crypto.Hash
import org.json.JSONObject
import java.io.File

class KycSyncWorker(appContext:Context,params:WorkerParameters):CoroutineWorker(appContext,params){
  private val client=OkHttpClient()
  private val json="application/json; charset=utf-8".toMediaType()

  override suspend fun doWork():Result{
    // Read encrypted queue file from app files
    val dir=File(applicationContext.filesDir,"queue"); if(!dir.exists())dir.mkdirs()
    val f=File(dir,"pending.enc")
    if(!f.exists())return Result.success()
    val enc=f.readBytes()
    val dec=CryptoManager().decrypt(enc)
    val arr=org.json.JSONArray(String(dec))
    val now=System.currentTimeMillis(); val ttl=30L*24L*60L*60L*1000L
    var allOk=true
    val keep=mutableListOf<JSONObject>()
    for(i in 0 until arr.length()){
      val item=arr.getJSONObject(i)
      val ts=item.optLong("ts",now)
      if(now-ts>ttl) continue // drop expired items
      val payload=item.getJSONObject("payload").toString()
      val idem=item.optString("idempotencyKey").ifBlank{ Hash.hex(Hash.sha256(payload.toByteArray())).take(32) }
      val url=item.getString("url")
      val req=Request.Builder()
        .url(url)
        .post(payload.toRequestBody(json))
        .headers(Headers.headersOf("Content-Type","application/json","Idempotency-Key",idem))
        .build()
      try{
        client.newCall(req).execute().use{ resp ->
          if(!resp.isSuccessful){ allOk=false; keep.add(item) }
        }
      }catch(_:Exception){ allOk=false; keep.add(item) }
    }
    // Write back remaining
    val newArr=org.json.JSONArray(keep)
    val newBytes=CryptoManager().encrypt(newArr.toString().toByteArray())
    f.writeBytes(newBytes)
    return if(allOk) Result.success() else Result.retry()
  }

  companion object{
    fun enqueue(applicationContext:Context,sessionId:String,url:String,payload:JSONObject,idempotencyKey:String?=null){
      val dir=File(applicationContext.filesDir,"queue"); if(!dir.exists())dir.mkdirs()
      val f=File(dir,"pending.enc")
      val current=if(f.exists()) CryptoManager().decrypt(f.readBytes()).toString(Charsets.UTF_8) else "[]"
      val arr=org.json.JSONArray(current)
      val obj=JSONObject().apply{
        put("sessionId",sessionId)
        put("url",url)
        put("payload",payload)
        put("ts",System.currentTimeMillis())
        if(idempotencyKey!=null)put("idempotencyKey",idempotencyKey)
      }
      arr.put(obj)
      val enc=CryptoManager().encrypt(arr.toString().toByteArray())
      f.writeBytes(enc)
    }
  }
}

