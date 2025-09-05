package com.loksakshya.bharatkyc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.loksakshya.kyc.sdk.KycSdk
import org.json.JSONObject

class DocumentScanActivity:AppCompatActivity(){
  private fun parsePan(text:String):Triple<String?,String?,String?>{
    val m=Regex("\\b([A-Z]{5}[0-9]{4}[A-Z])\\b").find(text.replace("\n"," "))?:return Triple(null,null,null)
    val pan=m.groupValues[1]
    val name=Regex("(?i)name[:\\-\\s]*([A-Z ]{3,})").find(text)?.groupValues?.getOrNull(1)?.trim()
    val dob=Regex("(?i)dob[:\\-\\s]*([0-9]{2}[\\-/][0-9]{2}[\\-/][0-9]{4})").find(text)?.groupValues?.getOrNull(1)
    return Triple(pan,name,dob)
  }
  private fun parseDl(text:String):Triple<String?,String?,String?>{
    val m=Regex("\\b([A-Z]{1,3}[- ]?[0-9]{2}[- ]?[0-9]{4,10})\\b").find(text.replace("\n"," "))?:return Triple(null,null,null)
    val dl=m.groupValues[1].replace(" ","")
    val name=Regex("(?i)name[:\\-\\s]*([A-Z .]{3,})").find(text)?.groupValues?.getOrNull(1)?.trim()
    val dob=Regex("(?i)(dob|date of birth)[:\\-\\s]*([0-9]{2}[\\-/][0-9]{2}[\\-/][0-9]{4})").find(text)?.groupValues?.getOrNull(2)
    return Triple(dl,name,dob)
  }
  private fun parseVoter(text:String):Pair<String?,String?>{
    val m=Regex("\\b([A-Z]{3}[0-9]{7})\\b").find(text.replace("\n"," "))?:return Pair(null,null)
    val epic=m.groupValues[1]
    val name=Regex("(?i)name[:\\-\\s]*([A-Z .]{3,})").find(text)?.groupValues?.getOrNull(1)?.trim()
    return Pair(epic,name)
  }
  override fun onCreate(savedInstanceState:Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_document_scan)
    val input=findViewById<EditText>(R.id.et_ocr_text)
    val parseBtn=findViewById<Button>(R.id.btn_parse)
    val status=findViewById<TextView>(R.id.tv_doc_status)

    parseBtn.setOnClickListener{
      val txt=input.text.toString()
      val map=mutableMapOf<String,String>()
      parsePan(txt).let{ if(it.first!=null){ map["pan"]=it.first!!; it.second?.let{n->map["name"]=n}; it.third?.let{d->map["dob"]=d} } }
      parseDl(txt).let{ if(it.first!=null){ map["dl"]=it.first!!; it.second?.let{n->map["name"]=n}; it.third?.let{d->map["dob"]=d} } }
      parseVoter(txt).let{ if(it.first!=null){ map["epic"]=it.first!!; it.second?.let{n->map["name"]=n} } }
      if(map.isEmpty()){ status.text="No fields detected"; return@setOnClickListener }
      status.text="Fields: "+map.toString()
      val session=KycSdk.createSession(this,"document_scan")
      val payload=JSONObject(map as Map<*,*>)
      KycSdk.enqueueFinalize(this,BuildConfig.KYC_BASE_URL,session,payload)
      Toast.makeText(this,"Submitted: "+session.id,Toast.LENGTH_SHORT).show()
    }
  }
}
