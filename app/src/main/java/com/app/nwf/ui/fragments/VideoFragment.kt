package com.app.nwf.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentVideoBinding
import com.app.nwf.model.LangData
import com.app.nwf.model.VideoData
import com.app.nwf.model.VideoResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.ui.VimeoIframe
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class VideoFragment : Fragment() {

    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var langData: LangData
    private lateinit var binding: FragmentVideoBinding
    private var videoList: MutableList<VideoData> = mutableListOf()
    private var filterList: MutableList<VideoData> = mutableListOf()
    var id = ""
    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false
    private var visibleThreshold = 2
    private lateinit var mActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(layoutInflater)

        mActivity = requireActivity()
        id = arguments?.getString("id") ?: ""
        initView()
        return binding.root
    }

    private fun initView() {
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)

        binding.fvRecycle.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.fvRecycle.adapter = VideoAdapter()
        getVideoData()

        binding.fvSearch.hint = langData.typeHere
        binding.fvTitleTxt.text = langData.watchLearn

        binding.fvSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if (searchText.equals("")) {
                    filterList.clear()
                    filterList.addAll(videoList)
                    binding.fvRecycle.adapter?.notifyDataSetChanged()
                } else {
                    filterList(searchText)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable) {
                // Not used
            }
        })

        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)
                binding.fvSearch.hint = langData.typeHere
                binding.fvTitleTxt.text = langData.watchLearn
            }
        }

        val intentFilter = IntentFilter("update_language")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(myReceiver, intentFilter, RECEIVER_EXPORTED)
        }else {
            requireContext().registerReceiver(myReceiver, intentFilter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(myReceiver)
    }

    private fun filterList(query: String) {
        filterList.clear()
        for (item in videoList) {
            if (item.title?.contains(query, ignoreCase = true) == true) {
                filterList.add(item)
            }
        }
        binding.fvRecycle.adapter?.notifyDataSetChanged()
    }

    private fun getVideoData() {
        GlobalProgressBarUtil.show(requireContext())

        val mLangId: RequestBody = id.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mLimit: RequestBody = "20".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPage: RequestBody =
            currentPage.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        WebAPIManager.instance.getVideo(
            mLimit, mLangId, mPage,
            "Bearer " + BaseUtils.getToken(requireContext())!!
        )
            .enqueue(object : RemoteCallback<VideoResponse>() {
                override fun onSuccess(response: VideoResponse?) {
                    isLoading = false
                    if (response?.success == true) {
                        Glide.with(requireActivity()).load(response.banner).into(binding.fvBanner)
                        lastPage = response.totalPage!!
                        if (currentPage == 1) {
                            videoList.clear()
                            filterList.clear()
                            videoList.addAll(response.data)
                            filterList.addAll(response.data)
                        } else {
                            videoList.addAll(response.data)
                            filterList.addAll(response.data)
                        }

                        if (filterList.size > 0) {
                            binding.fvRecycle.visibility = View.VISIBLE
                            binding.fvMessage.visibility = View.GONE
                            binding.fvRecycle.adapter?.notifyDataSetChanged()
                        } else {
                            binding.fvMessage.visibility = View.VISIBLE
                            binding.fvRecycle.visibility = View.GONE
                        }

                        binding.fvRecycle.addOnScrollListener(object :
                            RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)

                                val layoutManager =
                                    recyclerView.layoutManager as LinearLayoutManager
                                val visibleItemCount = layoutManager.childCount
                                val totalItemCount = layoutManager.itemCount
                                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                                val nineteenthItemPosition = totalItemCount - visibleThreshold

                                // Add this within the onScrolled block of your addOnScrollListener
                                Log.d(
                                    "==================================VideoFragment",
                                    "FirstVisibleItem: $firstVisibleItem, TotalItemCount: $totalItemCount, NineteenthItemPosition: $nineteenthItemPosition"
                                )


                                if (!isLoading && firstVisibleItem >= nineteenthItemPosition) {
                                    if (currentPage < lastPage) {
                                        currentPage++
                                        isLoading = true
                                        getVideoData()
                                    }
                                }
                            }
                        })

                    } else {
                        Toast.makeText(requireContext(), "Something Wrong...", Toast.LENGTH_LONG)
                            .show()
                    }
                    GlobalProgressBarUtil.hide()
                }

                override fun onUnauthorized(throwable: Throwable) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onFailed(throwable: Throwable) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onInternetFailed() {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(
                        requireContext(),
                        "Please Check Your internet..",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onEmptyResponse(message: String) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            })
    }


    inner class VideoAdapter : RecyclerView.Adapter<VideoAdapter.DataHolder>() {

        private val initializedPlayers = mutableSetOf<Int>()

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun setHasStableIds(hasStableIds: Boolean) {
            super.setHasStableIds(hasStableIds)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
            return DataHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_video, parent, false)
            )
        }

        override fun onBindViewHolder(
            holder: DataHolder,
            @SuppressLint("RecyclerView") position: Int
        ) {

    //        var rPlayer: YouTubePlayer? = null
            holder.mName.text = filterList[position].title
            Glide.with(requireContext()).load(filterList[position].image).into(holder.mIcon)

//            if (!initializedPlayers.contains(position)) {
//
//                holder.mPlayerView.enableAutomaticInitialization = false
//
//                lifecycle.addObserver(holder.mPlayerView)
//
//                val iFramePlayerOptions = IFramePlayerOptions.Builder()
//                    .controls(1)
//                    .fullscreen(1) // enable full screen button
//                    .build()
//
//                holder.mPlayerView.addFullscreenListener(object : FullscreenListener {
//                    override fun onEnterFullscreen(
//                        fullscreenView: View,
//                        exitFullscreen: () -> Unit
//                    ) {
//                        rPlayer!!.pause()
//                        startActivity(
//                            Intent(requireContext(), VimeoIframe::class.java)
//                                .putExtra("id", extractVideoId(filterList[position].url!!))
//                                .putExtra("time", 2f)
//                        )
////                    binding.fvRoot.visibility = View.GONE
////                    binding.fullScreenViewContainer.visibility = View.VISIBLE
////                    binding.fullScreenViewContainer.addView(fullscreenView)
//                    }
//
//                    override fun onExitFullscreen() {
////                    binding.fvRoot.visibility = View.VISIBLE
////                    binding.fullScreenViewContainer.visibility = View.GONE
////                    binding.fullScreenViewContainer.removeAllViews()
//                    }
//                })
//
//                holder.mPlayerView.getYouTubePlayerWhenReady()
//
//                holder.mPlayerView.initialize(object : AbstractYouTubePlayerListener() {
//                    override fun onReady(youTubePlayer: YouTubePlayer) {
//
//                    }
//                }, iFramePlayerOptions)
//
//            }

//            holder.mPlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//                override fun onReady(youTubePlayer: YouTubePlayer) {
//                    val videoId = extractVideoId(filterList[position].url!!)
//                    youTubePlayer.cueVideo(videoId!!, 0f)
//                }
//            }, iFramePlayerOptions)

            // holder.mPlayerView.initialize(holder.mPlayerView.addYouTubePlayerListener(), iFramePlayerOptions);

            holder.itemView.setOnClickListener {
                startActivity(Intent(requireContext(), VimeoIframe::class.java)
                    .putExtra("id", extractVideoId(filterList[position].url!!))
                    .putExtra("title", filterList[position].title!!)
                    .putExtra("time", 0f)
                )
            }
        }

        override fun getItemCount(): Int {
            return filterList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rvImage)!!
          //  val mPlayer = itemView.findViewById<YouTubePlayerView>(R.id.rvImage)!!
            val mName = itemView.findViewById<TextView>(R.id.rvTitle)!!
            // val mRoot = itemView.findViewById<LinearLayout>(R.id.videoRoot)!!
        }
    }

    fun extractVideoId(youtubeLink: String): String? {
        val uri = Uri.parse(youtubeLink)
        if (uri.host == "youtu.be") {
            // For youtu.be short links
            return uri.lastPathSegment
        } else if (uri.host == "www.youtube.com" || uri.host == "m.youtube.com") {
            // For standard YouTube links
            val query = uri.getQueryParameter("v")
            if (!query.isNullOrEmpty()) {
                return query
            }
        }
        return null
    }

}