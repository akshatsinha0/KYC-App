package com.loksakshya.parsers

// Minimal, JVM-only versions of the OCR field parsers

data class PanData(val pan:String,val name:String?=null,val dob:String?=null)

data class DlData(val dl:String,val name:String?=null,val dob:String?=null)

data class VoterData(val epic:String,val name:String?=null)

object Parsers{
  fun parsePan(text:String):PanData?{
    val panRegex=Regex("\\b([A-Z]{5}[0-9]{4}[A-Z])\\b")
    val m=panRegex.find(text.replace("\n"," "))?:return null
    val pan=m.groupValues[1]
    val name=Regex("(?i)name[:\\-\\s]*([A-Z ]{3,})").find(text)?.groupValues?.getOrNull(1)?.trim()
    val dob=Regex("(?i)dob[:\\-\\s]*([0-9]{2}[\\-/][0-9]{2}[\\-/][0-9]{4})").find(text)?.groupValues?.getOrNull(1)
    return PanData(pan,name,dob)
  }
  fun parseDl(text:String):DlData?{
    val dlRegex=Regex("\\b([A-Z]{1,3}[- ]?[0-9]{2}[- ]?[0-9]{4,10})\\b")
    val m=dlRegex.find(text.replace("\n"," "))?:return null
    val dl=m.groupValues[1].replace(" ","")
    val name=Regex("(?i)name[:\\-\\s]*([A-Z .]{3,})").find(text)?.groupValues?.getOrNull(1)?.trim()
    val dob=Regex("(?i)(dob|date of birth)[:\\-\\s]*([0-9]{2}[\\-/][0-9]{2}[\\-/][0-9]{4})").find(text)?.groupValues?.getOrNull(2)
    return DlData(dl,name,dob)
  }
  fun parseVoter(text:String):VoterData?{
    val epicRegex=Regex("\\b([A-Z]{3}[0-9]{7})\\b")
    val m=epicRegex.find(text.replace("\n"," "))?:return null
    val epic=m.groupValues[1]
    val name=Regex("(?i)name[:\\-\\s]*([A-Z .]{3,})").find(text)?.groupValues?.getOrNull(1)?.trim()
    return VoterData(epic,name)
  }
}

