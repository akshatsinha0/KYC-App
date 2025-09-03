package com.loksakshya.kyc.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoManager(private val alias:String="bharat_kyc_aes"){
  private fun key():SecretKey{
    val ks=KeyStore.getInstance("AndroidKeyStore").apply{load(null)}
    (ks.getKey(alias,null) as? SecretKey)?.let{return it}
    val gen=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
    val spec=KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
      .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      .setUserAuthenticationRequired(false).build()
    gen.init(spec)
    return gen.generateKey()
  }
  fun encrypt(plain:ByteArray):ByteArray{
    val c=Cipher.getInstance("AES/GCM/NoPadding")
    c.init(Cipher.ENCRYPT_MODE,key())
    val iv=c.iv
    val enc=c.doFinal(plain)
    return ByteArray(iv.size+enc.size).apply{ System.arraycopy(iv,0,this,0,iv.size); System.arraycopy(enc,0,this,iv.size,enc.size) }
  }
  fun decrypt(blob:ByteArray):ByteArray{
    val iv=blob.copyOfRange(0,12)
    val data=blob.copyOfRange(12,blob.size)
    val c=Cipher.getInstance("AES/GCM/NoPadding")
    c.init(Cipher.DECRYPT_MODE,key(),GCMParameterSpec(128,iv))
    return c.doFinal(data)
  }
}

