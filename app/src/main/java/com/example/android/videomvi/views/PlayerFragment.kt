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
import com.example.android.videomvi.models.LoadControlConfig
import com.example.android.videomvi.models.PlayerViewConfig
import com.example.android.videomvi.utils.createDataSourceFactory
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import java.lang.IllegalStateException


class PlayerFragment : Fragment() {

    private val args: PlayerFragmentArgs by navArgs()
    private val TAG = this.javaClass.simpleName

    private var player: SimpleExoPlayer? = null
    private var playerConfig = PlayerViewConfig()
    private lateinit var playbackListener: PlaybackStateListener

    // region lifecycle methods
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        playbackListener = PlaybackStateListener()

        view.back_button.setOnClickListener {
            // use navigateUp to go back, to avoid growing backstack & creating new HomeFragment instances
            findNavController().navigateUp()
        }

        applyPlayerConfigs(view.exoplayer_view)

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
    // endregion

    /**
     * Set different time-values for playerView
     */
    private fun applyPlayerConfigs(playerView: PlayerView) {
        playerView.setFastForwardIncrementMs(playerConfig.fastForwardIncrement.mS)
        playerView.setRewindIncrementMs(playerConfig.rewindIncrement.mS)
        playerView.controllerShowTimeoutMs = playerConfig.timeOut.mS
    }

    private fun initializePlayer(){
        if (player == null) {
            // select track according to bandwidth
            // play adaptive streaming source using trackSelector
            val trackSelector = DefaultTrackSelector()
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())

            // LoadControl
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    LoadControlConfig.minBufferDuration.mS,
                    LoadControlConfig.maxBufferDuration.mS,
                    LoadControlConfig.minPlaybackStartBuffer.mS,
                    LoadControlConfig.minPlaybackResumeBuffer.mS
                    )
                .createDefaultLoadControl()

            player = ExoPlayerFactory.newSimpleInstance(
                activity?.baseContext,
                trackSelector,
                loadControl
            )
        }

        exoplayer_view.player = player
        val mediaSource = getMediaSource()

        player?.addListener(playbackListener)
        player?.playWhenReady = playerConfig.playWhenReady
        player?.seekTo(playerConfig.currentWindow, playerConfig.playbackPosition)
        // don't reset position or state, since those have been set in 2 lines above
        player?.prepare(mediaSource, false, false)
    }

    private fun getMediaSource(): MediaSource {
        val uri = Uri.parse(args.mediaUrl)
        val dataSourceFactory = createDataSourceFactory(
                                                        activity?.baseContext,
                                                        getString(R.string.exoplayer)
                                                    )

        return when (val contentType = Util.inferContentType(uri)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_SS -> throw IllegalStateException("SmoothStreaming is not supported.")
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $contentType")
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playerConfig = playerConfig.copy(
                                playWhenReady = player?.playWhenReady ?: true,
                                playbackPosition = player?.currentPosition ?: 0,
                                currentWindow = player?.currentWindowIndex ?: 0
                            )
            player?.removeListener(playbackListener)
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

    inner class PlaybackStateListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED"
                else -> "State.UNKNOWN"
            }

            Log.d(TAG, "State changed to $stateString, playWhenReady is $playWhenReady")
        }
    }
}
