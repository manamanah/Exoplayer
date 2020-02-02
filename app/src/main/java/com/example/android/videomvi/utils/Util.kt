package com.example.android.videomvi.utils

import android.content.Context
import com.google.android.exoplayer2.upstream.*

/**
 * Helper function to allow crossProtocolRedirects
 * source: https://stackoverflow.com/questions/41517440/exoplayer2-how-can-i-make-a-http-301-redirect-work
 */

fun createDataSourceFactory(context: Context?, userAgent: String): DefaultDataSourceFactory? { // Default parameters, except allowCrossProtocolRedirects is true
    val httpDataSourceFactory = DefaultHttpDataSourceFactory(
        userAgent,
        null,
        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
        true /* allowCrossProtocolRedirects */
    )
    return DefaultDataSourceFactory(context, null, httpDataSourceFactory)
}