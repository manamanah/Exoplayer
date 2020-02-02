package com.example.android.videomvi.repository

import android.content.Context
import com.example.android.videomvi.R.string.videoListFile
import com.example.android.videomvi.models.Video
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Repository(private val applicationContext: Context) {

    private val videoListPath = applicationContext.resources.getString(videoListFile)

    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    fun getVideoList(): List<Video>? {
        val videoListType = Types.newParameterizedType(List::class.java, Video::class.java)
        val jsonAdapter: JsonAdapter<List<Video>> = moshi.adapter(videoListType)

        val bufferedReader = applicationContext.assets.open(videoListPath).bufferedReader()
        val jsonString = bufferedReader.use { it.readText() } // read and store in string

        return jsonAdapter.fromJson(jsonString)
    }
}