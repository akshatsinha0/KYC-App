package com.loksakshya.bharatkyc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.loksakshya.kyc.aadhaar.AadhaarConfig
import com.loksakshya.kyc.aadhaar.AadhaarOfflineProcessor
import com.loksakshya.kyc.sdk.KycSdk
import org.json.JSONObject

class AadhaarActivity:AppCompatActivity(){
  private var selectedUri:Uri?=null
  override fun onCreate(savedInstanceState:Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_aadhaar)
    val pick=findViewById<Button>(R.id.btn_pick_zip)
    val share=findViewById<EditText>(R.id.et_share_code)
    val verify=findViewById<Button>(R.id.btn_verify)
    val status=findViewById<TextView>(R.id.tv_status)

    val opener=registerForActivityResult(ActivityResultContracts.OpenDocument()){ uri->
      if(uri!=null){ contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
        selectedUri=uri; status.text=getString(R.string.aadhaar_zip_selected) } else { status.text=getString(R.string.aadhaar_zip_not_selected) }
    }

    pick.setOnClickListener{ opener.launch(arrayOf("application/zip","application/octet-stream")) }

    verify.setOnClickListener{
      val uri=selectedUri; val code=share.text.toString().trim()
      if(uri==null){ toast("Select ZIP file") ; return@setOnClickListener }
      if(code.length!=4){ toast("Enter 4-digit share code"); return@setOnClickListener }
      val bytes=contentResolver.openInputStream(uri)?.use{ it.readBytes() }?:run{ toast("Cannot read file"); return@setOnClickListener }
      val res=AadhaarOfflineProcessor.verifyAndDecrypt(bytes,code,this,AadhaarConfig.DEVELOPMENT)
      if(!res.ok){ status.text="Aadhaar verify failed: ${res.error}"; return@setOnClickListener }
      status.text=getString(R.string.aadhaar_ok)
      val session=KycSdk.createSession(this,"aadhaar_offline")
      val payload=JSONObject().apply{
        put("name",res.name); put("uid",res.uid); put("gender",res.gender); put("dob",res.dob); put("refId",res.refId)
      }
      KycSdk.enqueueFinalize(this,BuildConfig.KYC_BASE_URL,session,payload)
      toast("Submitted for sync: "+session.id)
    }
  }
  private fun toast(msg:String){ Toast.makeText(this,msg,Toast.LENGTH_SHORT).show() }
}
