package com.loksakshya.kyc.digilocker

// Stub for in-app browser redirect and return URI handling
object DigiLockerStub{
  fun authUrl(redirectUri:String,state:String)="https://digilocker.gov.in/oauth/authorize?client_id=LOCAL&response_type=code&redirect_uri="+redirectUri+"&state="+state
}

