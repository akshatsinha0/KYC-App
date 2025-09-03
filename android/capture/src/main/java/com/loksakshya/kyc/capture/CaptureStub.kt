package com.loksakshya.kyc.capture

// Simple camera abstraction without bringing CameraX deps here
// Host can implement this interface inside app module using CameraX
interface CameraController{
  fun start(onFrame:(rgb:ByteArray,w:Int,h:Int)->Unit)
  fun stop()
}

object CaptureStub

