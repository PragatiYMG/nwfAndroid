package com.app.nwf.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.nwf.R
import com.app.nwf.databinding.ActivityLoginScreenBinding
import com.app.nwf.model.CommonResponse
import com.app.nwf.model.LangData
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.regex.Pattern

class LoginScreen : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var langData: LangData
    private var isPassVisible = false
    private var deviceID = ""
    private val emailPattern: Pattern = Pattern.compile(
        "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {
        val gson = Gson()
        val mString = BaseUtils.getLangData(this@LoginScreen)
        langData = gson.fromJson(mString, LangData::class.java)

        binding.lsName.hint = langData.name
        binding.lsEmail.hint = langData.yourEmail
        binding.lsPhone.hint = langData.phoneNumber
        binding.lsLogin.text = langData.login

        //getCaptcha()
        deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
       // deviceID = "234:909"
        binding.lsForgotPassword.setOnClickListener(this)
        binding.lsLogin.setOnClickListener(this)
        binding.lsSignUp.setOnClickListener(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        return true
    }



    override fun onClick(view: View?) {
        when(view?.id){
            R.id.lsLogin ->{
                //startActivity(Intent(this@LoginScreen, HomeScreen::class.java))
                if(isValidate()){
                    loginApi()
                }
            }
            R.id.lsForgotPassword ->{

            }
            R.id.lsSignUp ->{
                startActivity(Intent(this@LoginScreen, RegisterScreen::class.java))
            }

        }
    }

    private fun loginApi() {
        GlobalProgressBarUtil.show(this@LoginScreen)

        val mName: RequestBody = binding.lsName.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPhone: RequestBody = binding.lsPhone.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mEmail: RequestBody = binding.lsEmail.text.toString().trim().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mDeviceId: RequestBody = deviceID.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        WebAPIManager.instance.userLogin(mName, mEmail, mPhone, mDeviceId).enqueue(object : RemoteCallback<CommonResponse>() {
            override fun onSuccess(response: CommonResponse?) {
                if (response?.success == true) {

                   BaseUtils.putUserLogIn(
                       this@LoginScreen,
                       true,
                       binding.lsName.text.toString().trim(),
                       binding.lsPhone.text.toString().trim(),
                       binding.lsEmail.text.toString().trim(),
                       response.token!!
                   )
                    startActivity(Intent(this@LoginScreen, HomeScreen::class.java))

                } else {
                    Toast.makeText(this@LoginScreen, "Something Wrong...", Toast.LENGTH_LONG).show()
                }
                GlobalProgressBarUtil.hide()
            }

            override fun onUnauthorized(throwable: Throwable) {
                GlobalProgressBarUtil.hide()
                Toast.makeText(this@LoginScreen, throwable.message, Toast.LENGTH_LONG).show()
            }

            override fun onFailed(throwable: Throwable) {
                GlobalProgressBarUtil.hide()
                Toast.makeText(this@LoginScreen, throwable.message, Toast.LENGTH_LONG).show()
            }

            override fun onInternetFailed() {
                GlobalProgressBarUtil.hide()
                Toast.makeText(this@LoginScreen, "Please Check Your internet..", Toast.LENGTH_LONG).show()
            }

            override fun onEmptyResponse(message: String) {
                GlobalProgressBarUtil.hide()
                Toast.makeText(this@LoginScreen, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun isValidate(): Boolean {
        if(binding.lsName.text.toString().isEmpty()){
            Toast.makeText(this@LoginScreen, langData.user, Toast.LENGTH_LONG).show()
            return false
        }else if(binding.lsPhone.text.toString().isEmpty()){
            Toast.makeText(this@LoginScreen, langData.phoneNumber, Toast.LENGTH_LONG).show()
            return false
        }
//        else if(binding.captchaText.text.toString().isEmpty()){
//            Toast.makeText(this@LoginScreen, "Enter Captcha", Toast.LENGTH_LONG).show()
//            return false
//        } else if(!binding.captchaText.text.toString().trim().equals(captcha, false)){
//            binding.captchaText.setText("")
//            getCaptcha()
//            Toast.makeText(this@LoginScreen, "Invalid Captcha", Toast.LENGTH_LONG).show()
//            return false
//        }
        else if(binding.lsEmail.text.toString().isEmpty()){
            Toast.makeText(this@LoginScreen, langData.email, Toast.LENGTH_LONG).show()
            return false
        } else if(!emailPattern.matcher(binding.lsEmail.text.toString().trim()).matches()){
            Toast.makeText(this@LoginScreen, langData.emailValid, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}