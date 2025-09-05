package com.loksakshya.bharatkyc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.loksakshya.kyc.sdk.KycSdk
import org.json.JSONObject

class FaceActivity:AppCompatActivity(){
  override fun onCreate(savedInstanceState:Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_face)
    val btn=findViewById<Button>(R.id.btn_face_sim)
    val status=findViewById<TextView>(R.id.tv_face_status)
    btn.setOnClickListener{
      val ok=true
      val score=0.8
      val match=0.95
      status.text="liveness_ok="+ok+" score="+String.format("%.2f",score)+" match="+String.format("%.2f",match)
      val session=KycSdk.createSession(this,"face")
      val payload=JSONObject().apply{ put("liveness_ok",ok); put("liveness_score",score); put("match",match) }
      KycSdk.enqueueFinalize(this,BuildConfig.KYC_BASE_URL,session,payload)
      Toast.makeText(this,"Submitted: "+session.id,Toast.LENGTH_SHORT).show()
    }
  }
}
