package com.loksakshya.kyc.crypto

import java.security.MessageDigest

object Hash{
  fun sha256(bytes:ByteArray):ByteArray{
    val md=MessageDigest.getInstance("SHA-256")
    return md.digest(bytes)
  }
  fun hex(bytes:ByteArray):String{
    val sb=StringBuilder(bytes.size*2)
    for(b in bytes){ val v=b.toInt() and 0xFF; sb.append("0123456789abcdef"[v ushr 4]); sb.append("0123456789abcdef"[v and 0x0F]) }
    return sb.toString()
  }
}
