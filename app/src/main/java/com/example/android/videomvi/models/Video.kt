package com.example.android.videomvi.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Video(val title: String, val subtitle: String, val description: String, val url: String)