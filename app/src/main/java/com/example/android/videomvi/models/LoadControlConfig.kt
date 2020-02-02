package com.example.android.videomvi.models

object LoadControlConfig {
    val minBufferDuration: MilliSeconds = MilliSeconds(10_000)
    val maxBufferDuration: MilliSeconds = MilliSeconds(10_000)
    val minPlaybackStartBuffer: MilliSeconds = MilliSeconds(5_000)
    val minPlaybackResumeBuffer: MilliSeconds = MilliSeconds(5_000)
}