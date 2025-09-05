package com.loksakshya.kyc.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature

// Simple signer using Android Keystore
class Signer(private val alias:String="bharat_kyc_sign"){
  private fun ensureKeyPair():KeyPair{
    val ks=KeyStore.getInstance("AndroidKeyStore").apply{load(null)}
    val priv=ks.getKey(alias,null) as? java.security.PrivateKey
    val cert=ks.getCertificate(alias)
    if(priv!=null&&cert!=null){ return KeyPair(cert.publicKey,priv) }
    val kpg=KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC,"AndroidKeyStore")
    val spec=KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_SIGN)
      .setDigests(KeyProperties.DIGEST_SHA256)
      .setAlgorithmParameterSpec(java.security.spec.ECGenParameterSpec("secp256r1"))
      .build()
    kpg.initialize(spec)
    return kpg.generateKeyPair()
  }
  fun signToBase64(data:ByteArray):String{
    val kp=ensureKeyPair()
    val sig=Signature.getInstance("SHA256withECDSA")
    sig.initSign(kp.private)
    sig.update(data)
    val out=sig.sign()
    return Base64.encodeToString(out,Base64.NO_WRAP)
  }
}
