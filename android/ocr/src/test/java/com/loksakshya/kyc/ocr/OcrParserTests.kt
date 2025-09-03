package com.loksakshya.kyc.ocr

import org.junit.Assert.*
import org.junit.Test

class OcrParserTests{
  @Test fun pan_basic(){
    val text="""
      Income Tax Department
      Permanent Account Number
      Name: RAVI KUMAR
      DOB: 12/05/1991
      ABCDE1234F
    """.trimIndent()
    val p=OcrProcessor.parsePan(text)!!
    assertEquals("ABCDE1234F",p.pan)
    assertEquals("RAVI KUMAR",p.name)
    assertEquals("12/05/1991",p.dob)
  }
  @Test fun dl_basic(){
    val text="""
      DL No: HR-12 2009123456
      Name: SUNITA SHARMA
      Date of Birth: 01-01-1985
    """.trimIndent()
    val d=OcrProcessor.parseDl(text)!!
    assertTrue(d.dl.startsWith("HR-12"))
    assertEquals("SUNITA SHARMA",d.name)
    assertEquals("01-01-1985",d.dob)
  }
  @Test fun voter_basic(){
    val text="""
      Election Commission of India
      EPIC No: XYZ1234567
      Name: AMIT VERMA
    """.trimIndent()
    val v=OcrProcessor.parseVoter(text)!!
    assertEquals("XYZ1234567",v.epic)
    assertEquals("AMIT VERMA",v.name)
  }
}

