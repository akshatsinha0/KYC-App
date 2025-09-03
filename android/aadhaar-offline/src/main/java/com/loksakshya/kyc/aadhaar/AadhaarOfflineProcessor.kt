package com.loksakshya.kyc.aadhaar

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.enums.AesKeyStrength
import org.bouncycastle.cms.CMSSignedData
import org.bouncycastle.cms.SignerInformation
import org.bouncycastle.cms.SignerInformationStore
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory

// Simple implementation. Decrypt ZIP with share code, verify signature, parse fields.
data class AadhaarResult(
  val ok:Boolean,
  val name:String?=null,
  val uid:String?=null,
  val gender:String?=null,
  val dob:String?=null,
  val refId:String?=null,
  val xmlRaw:String?=null,
  val error:String?=null
)

object AadhaarOfflineProcessor{
  init{ if(Security.getProvider("BC")==null) Security.addProvider(BouncyCastleProvider()) }

  fun verifyAndDecrypt(
    zipBytes:ByteArray,
    shareCode:String,
    context:android.content.Context?=null,
    config:AadhaarConfig=AadhaarConfig.PRODUCTION
  ):AadhaarResult{
    if(zipBytes.isEmpty()) return AadhaarResult(ok=false,error="bad_zip")
    // Write to temp file for zip4j
    val tmpDir=createTempDir()
    val zipPath=File(tmpDir, UUID.randomUUID().toString()+".zip")
    Files.write(zipPath.toPath(),zipBytes)

    val zip=ZipFile(zipPath)
    if(!zip.isValidZipFile){ return AadhaarResult(ok=false,error="bad_zip") }
    try{
      if(zip.isEncrypted){ zip.setPassword(shareCode.toCharArray()) }
    }catch(e:ZipException){ return AadhaarResult(ok=false,error="bad_share_code") }

    try{
      val entries=zip.fileHeaders
      val xmlEntry=entries.firstOrNull{ it.fileName.lowercase().endsWith(".xml") }
        ?: return AadhaarResult(ok=false,error="xml_missing")
      val sigEntry=entries.firstOrNull{ val n=it.fileName.lowercase(); n.endsWith(".p7b")||n.endsWith(".p7s")||n.contains("sign") }
        ?: return AadhaarResult(ok=false,error="signature_missing")

      val xmlBytes=zip.getInputStream(xmlEntry).use{it.readBytes()}
      val sigBytes=zip.getInputStream(sigEntry).use{it.readBytes()}

      val verified=verifySignature(xmlBytes,sigBytes,context,config)
      if(!verified) return AadhaarResult(ok=false,error="signature_invalid")

      val parsed=parseXml(xmlBytes)?:return AadhaarResult(ok=false,error="xml_parse_error")
      return AadhaarResult(ok=true,name=parsed.name,uid=parsed.uid,gender=parsed.gender,dob=parsed.dob,refId=parsed.refId,xmlRaw=String(xmlBytes))
    }catch(e:Exception){
      return AadhaarResult(ok=false,error="bad_zip")
    }

  }

  private data class Parsed(val name:String?,val uid:String?,val gender:String?,val dob:String?,val refId:String?)

  private fun parseXml(xml:ByteArray):Parsed?{
    return try{
      val dbf=DocumentBuilderFactory.newInstance()
      dbf.isNamespaceAware=true
      val doc=dbf.newDocumentBuilder().parse(ByteArrayInputStream(xml))
      val data=doc.getElementsByTagName("UidData").item(0)?:doc.documentElement
      val name=data.attributes?.getNamedItem("name")?.nodeValue
      val uid=data.attributes?.getNamedItem("uid")?.nodeValue
      val gender=data.attributes?.getNamedItem("gender")?.nodeValue
      val dob=data.attributes?.getNamedItem("dob")?.nodeValue
      val refId=data.attributes?.getNamedItem("referenceId")?.nodeValue
      Parsed(name,uid,gender,dob,refId)
    }catch(e:Exception){ null }
  }

  private fun verifySignature(data:ByteArray,sigBytes:ByteArray,context:android.content.Context?,config:AadhaarConfig):Boolean{
    return try{
      // CMS (PKCS#7) detached signature expected; if attached, CMSSignedData will also contain content
      val cms=CMSSignedData(org.bouncycastle.cms.CMSProcessableByteArray(data),sigBytes)
      val signers:SignerInformationStore=cms.signerInfos
      val certs=cms.certificates
      val signer:SignerInformation=signers.signers.first()
      // Find matching certificate holder for signer id
      @Suppress("UNCHECKED_CAST")
      val holders=certs.getMatches(null) as Collection<org.bouncycastle.cert.X509CertificateHolder>
      val certHolder=holders.firstOrNull{ signer.sid.match(it) }?:return false
      val cert=CertificateFactory.getInstance("X.509").generateCertificate(ByteArrayInputStream(certHolder.encoded)) as X509Certificate
      val verifier=JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert)
      val basicValid=signer.verify(verifier)
      if(!basicValid) return false
      
      // Check certificate expiry if configured
      if(config.checkCertificateExpiry) {
        try {
          cert.checkValidity()
        } catch(e: Exception) {
          return false // Certificate expired or not yet valid
        }
      }
      
      // Skip trust chain validation if not in strict mode
      if(!config.strictTrustValidation) return basicValid
      
      // Load trust chain based on configuration
      val trustChain = when {
        !config.customTrustChain.isNullOrBlank() -> UidaiTrustStore.loadPemCertificates(config.customTrustChain)
        context != null -> UidaiTrustStore.loadEmbeddedChain(context)
        else -> emptyList()
      }
      
      // If no trust chain available in strict mode, reject
      if(trustChain.isEmpty()) return false
      
      // Verify certificate is trusted by UIDAI chain
      return UidaiTrustStore.isTrustedCertificate(cert, trustChain)
    }catch(e:Exception){ false }
  }


}

