package com.loksakshya.kyc.analytics

import android.content.Context
import com.loksakshya.kyc.crypto.CryptoManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Event(val ts:Long,val event:String,val props:Map<String,Any?>=emptyMap())

object Analytics{
  private fun file(ctx:Context)=File(ctx.filesDir,"analytics/events.enc")
  private fun read(ctx:Context):JSONArray{
    val f=file(ctx); return if(!f.exists()) JSONArray() else JSONArray(String(CryptoManager().decrypt(f.readBytes())))
  }
  private fun write(ctx:Context,arr:JSONArray){
    val f=file(ctx); f.parentFile?.mkdirs(); f.writeBytes(CryptoManager().encrypt(arr.toString().toByteArray()))
  }
  fun log(ctx:Context,event:String,props:Map<String,Any?>=emptyMap()){
    val arr=read(ctx); val obj=JSONObject().apply{ put("ts",System.currentTimeMillis()); put("event",event); put("props",JSONObject(props)) }
    arr.put(obj); write(ctx,arr)
  }
  fun aggregate(ctx:Context):JSONObject{
    val arr=read(ctx); var completes=0; var livenessOk=0; var ocrEdits=0; var retries=0
    for(i in 0 until arr.length()){
      val e=arr.getJSONObject(i)
      when(e.getString("event")){
        "kyc_complete"->completes++
        "face_liveness"->if(e.optJSONObject("props")?.optBoolean("ok")==true)livenessOk++
        "ocr_edited"->ocrEdits++
        "sync_retry"->retries++
      }
    }
    return JSONObject().apply{ put("completions",completes); put("liveness_success",livenessOk); put("ocr_edit_count",ocrEdits); put("retry_count",retries) }
  }
}

