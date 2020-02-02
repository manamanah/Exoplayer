package com.example.android.videomvi.views


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.videomvi.R
import com.example.android.videomvi.adapters.VideoAdapter
import com.example.android.videomvi.repository.Repository
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var videoAdapter : VideoAdapter
    private lateinit var layoutManager : LinearLayoutManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val videoList = Repository(activity?.applicationContext
                                                ?: throw IllegalArgumentException("${this.javaClass.simpleName} - ApplicationContext was NULL"))
                                    .getVideoList()

        // setup recyclerView, if content
        if (videoList != null) {
            videoAdapter = VideoAdapter(videoList)
            layoutManager = LinearLayoutManager(context)

            view.videolist_recycler.adapter = videoAdapter
            view.videolist_recycler.layoutManager = layoutManager

            // select video and deliver url as args
            // also option to play all
            view.play_video_button.setOnClickListener {
                val selectedVideo = videoAdapter.selectedVideos.toTypedArray()
                findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToPlayerFragment(selectedVideo))
            }
        } else {
            Log.e(this.javaClass.simpleName, "VideoList was NULL")
        }

        return view
    }
}
