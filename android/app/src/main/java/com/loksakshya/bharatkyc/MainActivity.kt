package com.loksakshya.bharatkyc

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity:AppCompatActivity(){
  override fun onCreate(savedInstanceState:Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    fun click(id:Int, msg:Int){ findViewById<Button>(id).setOnClickListener{ Toast.makeText(this,msg,Toast.LENGTH_SHORT).show() } }
    click(R.id.btn_consent,R.string.toast_stub)
    click(R.id.btn_digilocker,R.string.toast_stub)
    click(R.id.btn_aadhaar,R.string.toast_stub)
    click(R.id.btn_scan,R.string.toast_stub)
    click(R.id.btn_face,R.string.toast_stub)
    click(R.id.btn_submit,R.string.toast_stub)
    
    // Add sync status button
    findViewById<Button>(R.id.btn_sync_status).setOnClickListener {
      startActivity(android.content.Intent(this, SyncStatusActivity::class.java))
    }
  }
}

