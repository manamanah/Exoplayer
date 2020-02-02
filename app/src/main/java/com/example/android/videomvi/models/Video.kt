package com.example.android.videomvi.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Video(
    val title: String = "Title",
    val subtitle: String = "Subtitle",
    val description: String = "Description",
    val url: String
)