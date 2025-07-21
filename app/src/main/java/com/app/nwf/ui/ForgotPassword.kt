package com.app.nwf.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.nwf.databinding.ForgotPasswordBinding

class ForgotPassword : AppCompatActivity() {

    private lateinit var binding: ForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

    }
}