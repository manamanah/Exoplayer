package com.example.android.videomvi.models

import com.example.android.videomvi.R


data class PlayerViewConfig(
    val playWhenReady: Boolean = true,
    var currentWindow: Int = 0,
    var playbackPosition: Long = 0,
    val timeOut: MilliSeconds = MilliSeconds(R.integer.controller_timeout_Ms),
    val fastForwardIncrement: MilliSeconds = MilliSeconds(R.integer.fastforward_timeMs),
    val rewindIncrement: MilliSeconds = MilliSeconds(R.integer.rewind_timeMs)
)