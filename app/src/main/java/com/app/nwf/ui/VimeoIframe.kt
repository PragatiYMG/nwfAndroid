package com.app.nwf.ui

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.app.nwf.databinding.ActivityVimeoIframeBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

class VimeoIframe : AppCompatActivity() {

    private lateinit var binding: ActivityVimeoIframeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVimeoIframeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        window.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
//                        View.SYSTEM_UI_FLAG_FULLSCREEN or
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                )
//        supportActionBar?.hide()
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        initView()
    }

    private fun initView() {

        binding.viTitle.text = intent.getStringExtra("title")
        binding.viBack.setOnClickListener {
            super.onBackPressedDispatcher.onBackPressed()
        }

        binding.playerView.enableAutomaticInitialization = false

        lifecycle.addObserver(binding.playerView)

        val iFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(1)
            .rel(0)
            .fullscreen(1) // enable full screen button
            .build()

        binding.playerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                binding.viRoot.visibility = View.GONE
                binding.playerView.visibility = View.GONE
                binding.fullScreenViewContainer.visibility = View.VISIBLE
                binding.fullScreenViewContainer.addView(fullscreenView)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onExitFullscreen() {
                binding.viRoot.visibility = View.VISIBLE
                binding.playerView.visibility = View.VISIBLE
                binding.fullScreenViewContainer.visibility = View.GONE
                binding.fullScreenViewContainer.removeAllViews()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        })

        binding.playerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = intent.getStringExtra("id")
                val playTime = intent.getFloatExtra("time", 0f)
                youTubePlayer.loadVideo(videoId!!, playTime)

            }
        }, iFramePlayerOptions)


    }

    override fun onDestroy() {
        binding.playerView.release()
        binding.playerView.removeAllViews()
        super.onDestroy()
    }

}