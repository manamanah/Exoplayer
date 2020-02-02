package com.example.android.videomvi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.android.videomvi.R
import com.example.android.videomvi.databinding.VideoBinding
import com.example.android.videomvi.models.Video
import kotlinx.android.synthetic.main.video.view.*

class VideoAdapter(private val videoList: List<Video>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    val selectedVideos = mutableListOf<String>()

    override fun getItemCount(): Int = videoList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VideoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until itemCount) {
            holder.bind(videoList[position])
        }
    }

    inner class ViewHolder(private val binding: VideoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(videoItem: Video) {
            binding.viewModel = videoItem

            binding.root.setOnClickListener {
                binding.root.description.isVisible = !binding.root.description.isVisible

                // set background color if clicked and add/remove to selected videoList
                if (binding.root.description.isVisible) {
                    binding.videoItem.setBackgroundColor(binding.root.resources.getColor(R.color.video_BG_selected))
                    selectedVideos.add(videoItem.url)
                } else {
                    binding.videoItem.setBackgroundColor(binding.root.resources.getColor(R.color.video_BG))
                    selectedVideos.remove(videoItem.url)
                }
            }
        }
    }
}