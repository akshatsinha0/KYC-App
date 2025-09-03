package com.loksakshya.kyc.face

// Simple interfaces so app can plug a detector
data class Point(val x:Float,val y:Float)

data class Landmarks(val leftEye:List<Point>,val rightEye:List<Point>,val nose:List<Point>,val mouth:List<Point>,val faceRect:IntArray?=null)

interface LandmarkDetector{ fun detect(rgb:ByteArray,w:Int,h:Int):Landmarks? }

object FaceProcessor{
  // Basic Eye Aspect Ratio for blink: average vertical/horizontal ratio
  private fun ear(eye:List<Point>):Float{
    if(eye.size<6)return 1f
    fun dist(a:Point,b:Point)=kotlin.math.hypot((a.x-b.x).toDouble(),(a.y-b.y).toDouble()).toFloat()
    val v1=dist(eye[1],eye[5]); val v2=dist(eye[2],eye[4]); val h=dist(eye[0],eye[3])
    return ((v1+v2)/(2f*h)).coerceIn(0f,2f)
  }
  fun livenessFromSequence(seq:List<Landmarks>):Pair<Boolean,Float>{
    if(seq.isEmpty())return false to 0f
    val ears=seq.map{ (ear(it.leftEye)+ear(it.rightEye))/2f }
    val minEar=ears.minOrNull()?:1f; val maxEar=ears.maxOrNull()?:1f
    val blinkDetected=minEar<0.18f && (maxEar-minEar)>0.12f
    val headTurn=seq.size>=3 && kotlin.math.abs((seq.first().nose.firstOrNull()?.x?:0f)-(seq.last().nose.firstOrNull()?.x?:0f))>5f
    val score=(if(blinkDetected)0.6f else 0f)+(if(headTurn)0.4f else 0f)
    return (score>=0.6f) to score
  }
  // Simple cosine similarity on 128D embeddings
  fun faceMatch(emb1:FloatArray,emb2:FloatArray):Float{
    fun dot(a:FloatArray,b:FloatArray):Float{ var s=0f; for(i in a.indices){ s+=a[i]*b.getOrElse(i){0f} }; return s }
    fun norm(a:FloatArray):Float{ var s=0f; for(v in a){ s+=v*v }; return kotlin.math.sqrt(s) }
    val denom=norm(emb1)*norm(emb2); if(denom==0f)return 0f; return (dot(emb1,emb2)/denom).coerceIn(-1f,1f)
  }
}

