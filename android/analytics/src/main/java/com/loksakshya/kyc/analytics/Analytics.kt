package com.loksakshya.kyc.analytics

import android.content.Context
import org.json.JSONObject

object Analytics{
  fun log(ctx:Context,event:String){}
  fun aggregate(ctx:Context):JSONObject{ return JSONObject() }
}

