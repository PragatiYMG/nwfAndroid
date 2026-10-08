package com.app.nwf.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.ActivityLanguageScreenBinding
import com.app.nwf.model.AppLanguage
import com.app.nwf.model.AppLanguageResponse
import com.app.nwf.model.LangData
import com.app.nwf.model.LangDataResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Locale

class LanguageScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageScreenBinding
    var langList: MutableList<AppLanguage> = mutableListOf()
    private lateinit var langData: LangData
    private var selPos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {

        getAppLanguageApi()

        binding.alsContinue.setOnClickListener {
            if (selPos == -1) {
                if(BaseUtils.getLangStatus(this@LanguageScreen)){
                    val gson = Gson()
                    val mString = BaseUtils.getLangData(this@LanguageScreen)
                    langData = gson.fromJson(mString, LangData::class.java)

                    Toast.makeText(
                        this@LanguageScreen,
                        langData.chooseAppLanguage,
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Toast.makeText(
                        this@LanguageScreen,
                        "Choose Your Language..",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                callLangDataApi()
            }
        }

        if(BaseUtils.getLangStatus(this@LanguageScreen)){
            val gson = Gson()
            val mString = BaseUtils.getLangData(this@LanguageScreen)
            langData = gson.fromJson(mString, LangData::class.java)

            binding.alsTitle.text = langData.chooseAppLanguage
            binding.alsContinue.text = langData._continue
        }

    }

    private fun callLangDataApi() {
        GlobalProgressBarUtil.show(this@LanguageScreen)

        val mId: RequestBody =
            langList[selPos].id.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        WebAPIManager.instance.getLanguageData(
            "Bearer " + BaseUtils.getToken(this@LanguageScreen)!!, mId
        )
            .enqueue(object : RemoteCallback<LangDataResponse>() {
                override fun onSuccess(response: LangDataResponse?) {
                    if (response?.success == true) {
                        langData = response.data!!


                        BaseUtils.putSelectLanguage(
                            this@LanguageScreen,
                            true,
                            langList[selPos].id.toString(),
                            langList[selPos].name.toString(),
                            langList[selPos].code.toString()
                        )
                        val gson = Gson()
                        val langString: String = gson.toJson(langData)
                        BaseUtils.putLangData(this@LanguageScreen, langString)
                        if(intent.getIntExtra("type",0) == 0) {
                            if (BaseUtils.getUserLogIn(this@LanguageScreen)) {
                                finish()
                                startActivity(Intent(this@LanguageScreen, HomeScreen::class.java))
                            } else {
                                finish()
                                startActivity(Intent(this@LanguageScreen, LoginScreen::class.java))
                            }
                        }else{
                            val intent = Intent("update_language")
                            sendBroadcast(intent)
                            finish()
                        }

                    } else {
                        Toast.makeText(this@LanguageScreen, "Something Wrong...", Toast.LENGTH_LONG)
                            .show()
                    }
                    GlobalProgressBarUtil.hide()
                }

                override fun onUnauthorized(throwable: Throwable) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(this@LanguageScreen, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onFailed(throwable: Throwable) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(this@LanguageScreen, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onInternetFailed() {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(
                        this@LanguageScreen,
                        "Please Check Your internet..",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onEmptyResponse(message: String) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(this@LanguageScreen, message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun getAppLanguageApi() {
        GlobalProgressBarUtil.show(this@LanguageScreen)
        WebAPIManager.instance.appLanguages(
            "Bearer " + BaseUtils.getToken(this@LanguageScreen)!!
        )
            .enqueue(object : RemoteCallback<AppLanguageResponse>() {
                override fun onSuccess(response: AppLanguageResponse?) {
                    if (response?.success == true) {
                        langList = response.data
                        if (langList.size > 0) {

                            val mId = BaseUtils.getLangId(this@LanguageScreen)

                            if(intent.getIntExtra("type",0) == 1){
                                langList.forEachIndexed { index, mLang ->
                                    if (langList[index].id == mId!!.toInt()) {
                                        langList[index].status = 0
                                        selPos = index
                                    } else {
                                        langList[index].status = 1
                                    }
                                }
                            }

                            binding.alsRecycle.visibility = View.VISIBLE
                            binding.alsMessage.visibility = View.GONE
                            binding.alsRecycle.layoutManager =
                                LinearLayoutManager(this@LanguageScreen)
                            binding.alsRecycle.adapter = LanguageAdapter()
                        } else {
                            binding.alsMessage.visibility = View.VISIBLE
                            binding.alsRecycle.visibility = View.GONE
                        }

                    } else {
                        Toast.makeText(this@LanguageScreen, "Something Wrong...", Toast.LENGTH_LONG)
                            .show()
                    }
                    GlobalProgressBarUtil.hide()
                }

                override fun onUnauthorized(throwable: Throwable) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(this@LanguageScreen, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onFailed(throwable: Throwable) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(this@LanguageScreen, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onInternetFailed() {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(
                        this@LanguageScreen,
                        "Please Check Your internet..",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onEmptyResponse(message: String) {
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(this@LanguageScreen, message, Toast.LENGTH_LONG).show()
                }
            })
    }

    inner class LanguageAdapter() : RecyclerView.Adapter<LanguageAdapter.DataHolder>() {

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
                LayoutInflater.from(parent.context).inflate(R.layout.row_language, parent, false)
            )
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {
            holder.mName.text = langList[position].name
            holder.mCode.text = langList[position].code!!.uppercase(Locale.US)

            if (langList[position].status == 0) {
                holder.mMAin.setCardBackgroundColor(this@LanguageScreen.resources.getColor(R.color.purple_700))
                holder.mName.setTextColor(this@LanguageScreen.resources.getColor(R.color.white))
                holder.mCode.setTextColor(this@LanguageScreen.resources.getColor(R.color.white))
            } else {
                holder.mMAin.setCardBackgroundColor(this@LanguageScreen.resources.getColor(R.color.white))
                holder.mName.setTextColor(this@LanguageScreen.resources.getColor(R.color.black))
                holder.mCode.setTextColor(this@LanguageScreen.resources.getColor(R.color.black))
            }

            holder.itemView.setOnClickListener {
                langList.forEachIndexed { index, mLang ->
                    if (index == position) {
                        langList[index].status = 0
                        selPos = index
                    } else {
                        langList[index].status = 1
                    }
                }
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return langList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mCode = itemView.findViewById<TextView>(R.id.rlCode)!!
            val mName = itemView.findViewById<TextView>(R.id.rlName)!!
            val mMAin = itemView.findViewById<CardView>(R.id.rlMain)!!
        }
    }
}