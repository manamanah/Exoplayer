package com.example.android.videomvi.views

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.videomvi.R
import com.example.android.videomvi.models.PlayerSettings
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*


class PlayerFragment : Fragment() {

    private val args: PlayerFragmentArgs by navArgs()

    private var player: SimpleExoPlayer? = null
    private var playerSettings = PlayerSettings()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        view.back_button.setOnClickListener {
            // use navigateUp to go back, to avoid growing backstack & creating new HomeFragment instances
            findNavController().navigateUp()
        }

        // Inflate the layout for this fragment
        return view
    }

    /**
     * Starting with API level 24 multiple windows are supported
     * - app can be visible not not active in split window mode
     * -> need to init player onStart
     * < 24 wait as long as possible -> onResume for init suffices
     */
    override fun onStart() {
        super.onStart()

        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        if (Util.SDK_INT < 24) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun initializePlayer(){
        if (player == null) {
            // select track according to bandwidth
            // play adaptive streaming source using trackSelector
            val trackSelector = DefaultTrackSelector()
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())
            player = ExoPlayerFactory.newSimpleInstance(activity?.baseContext, trackSelector)
        }

        exoplayer_view.player = player
        val mediaSource = getMediaSource()

        player?.playWhenReady = playerSettings.playWhenReady
        player?.seekTo(playerSettings.currentWindow, playerSettings.playbackPosition)
        // don't reset position or state, since those have been set in 2 lines above
        player?.prepare(mediaSource, false, false)
    }

    private fun getMediaSource(): MediaSource {
        val uri = Uri.parse(args.mediaUrl)
        val dataSourceFactory = DefaultDataSourceFactory(activity?.baseContext, getString(R.string.exoplayer))

        // mediaUrl -> DASH - adaptive streaming format
        return DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun releasePlayer() {
        if (player != null) {
            playerSettings = playerSettings.copy(
                                playWhenReady = player?.playWhenReady ?: true,
                                playbackPosition = player?.currentPosition ?: 0,
                                currentWindow = player?.currentWindowIndex ?: 0
                            )
            player?.release()
            player = null
        }
    }

    private fun hideSystemUI() {
        exoplayer_view.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }
}
