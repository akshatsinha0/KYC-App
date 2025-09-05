package com.loksakshya.bharatkyc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.loksakshya.kyc.face.FaceProcessor
import com.loksakshya.kyc.face.Landmarks
import com.loksakshya.kyc.face.Point
import com.loksakshya.kyc.sdk.KycSdk
import org.json.JSONObject

class FaceActivity:AppCompatActivity(){
  override fun onCreate(savedInstanceState:Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_face)
    val btn=findViewById<Button>(R.id.btn_face_sim)
    val status=findViewById<TextView>(R.id.tv_face_status)
    btn.setOnClickListener{
      val seq=simulateBlinkSequence()
      val (ok,score)=FaceProcessor.livenessFromSequence(seq)
      val match=FaceProcessor.faceMatch(FloatArray(128){1f},FloatArray(128){1f})
      status.text="liveness_ok="+ok+" score="+String.format("%.2f",score)+" match="+String.format("%.2f",match)
      val session=KycSdk.createSession(this,"face")
      val payload=JSONObject().apply{ put("liveness_ok",ok); put("liveness_score",score); put("match",match) }
      KycSdk.enqueueFinalize(this,BuildConfig.KYC_BASE_URL,session,payload)
      Toast.makeText(this,"Submitted: "+session.id,Toast.LENGTH_SHORT).show()
    }
  }
  private fun simulateBlinkSequence():List<Landmarks>{
    fun eye(open:Boolean)=listOf(
      Point(10f,10f),Point(12f,if(open)8f else 10f),Point(14f,if(open)8f else 10f),Point(16f,10f),Point(14f,if(open)12f else 10f),Point(12f,if(open)12f else 10f)
    )
    val seq=mutableListOf<Landmarks>()
    seq+=Landmarks(eye(true),eye(true),listOf(Point(13f,10f)),emptyList())
    seq+=Landmarks(eye(false),eye(false),listOf(Point(12f,10f)),emptyList())
    seq+=Landmarks(eye(true),eye(true),listOf(Point(11f,10f)),emptyList())
    return seq
  }
}
