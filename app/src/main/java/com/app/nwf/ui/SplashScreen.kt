package com.app.nwf.ui

import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.TranslateAnimation
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.nwf.databinding.ActivitySplashBinding
import com.app.nwf.utils.BaseUtils
import com.bumptech.glide.Glide

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        animateImage()
        Handler(Looper.getMainLooper()).postDelayed({


            if(BaseUtils.getLangStatus(this@SplashScreen)) {
                //  startActivity(Intent(this, LoginScreen::class.java))
                if (BaseUtils.getUserLogIn(this@SplashScreen)) {
                    startActivity(Intent(this, HomeScreen::class.java))
                } else {
                    startActivity(Intent(this, LoginScreen::class.java))
                }
            }else{
                startActivity(Intent(this, LanguageScreen::class.java).putExtra("type", 0))
            }

            try {
                Glide.get(this@SplashScreen).clearMemory()
                // Clear disk cache (run this in a background thread)
                Thread {
                    Glide.get(this@SplashScreen).clearDiskCache()
                }.start()
            }catch (_: Exception){

            }

            finish()
        }, 3000)

    }

    private fun animateImage() {
        val fromYDelta = 1000 // Move from the bottom of the screen
        val toYDelta = 0 // Move to the center of the screen
        val animation = TranslateAnimation(
            0f, 0f,
            fromYDelta.toFloat(),
            toYDelta.toFloat()
        )

        animation.duration = 2800 // Animation duration in milliseconds
        animation.fillAfter = true

        binding.spImage.startAnimation(animation)
    }
}