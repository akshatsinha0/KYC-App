package com.loksakshya.bharatkyc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity:AppCompatActivity(){
  override fun onCreate(savedInstanceState:Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    findViewById<Button>(R.id.btn_consent).setOnClickListener{ Toast.makeText(this,R.string.toast_stub,Toast.LENGTH_SHORT).show() }
    findViewById<Button>(R.id.btn_digilocker).setOnClickListener{
      val url="http://10.0.2.2:8080/web/digilocker.html"; startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
    findViewById<Button>(R.id.btn_aadhaar).setOnClickListener{ startActivity(Intent(this,AadhaarActivity::class.java)) }
    findViewById<Button>(R.id.btn_scan).setOnClickListener{ startActivity(Intent(this,DocumentScanActivity::class.java)) }
    findViewById<Button>(R.id.btn_face).setOnClickListener{ startActivity(Intent(this,FaceActivity::class.java)) }
    findViewById<Button>(R.id.btn_submit).setOnClickListener{ Toast.makeText(this,R.string.toast_stub,Toast.LENGTH_SHORT).show() }
    findViewById<Button>(R.id.btn_sync_status).setOnClickListener { startActivity(Intent(this, SyncStatusActivity::class.java)) }
  }
}

