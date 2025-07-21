package com.app.nwf.ui

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.nwf.R
import com.app.nwf.databinding.HomeScreenBinding
import com.app.nwf.model.DetailsResponse
import com.app.nwf.model.LangData
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.ui.fragments.ConsultationFragment
import com.app.nwf.ui.fragments.ContactUsFragment
import com.app.nwf.ui.fragments.CypcFragment
import com.app.nwf.ui.fragments.Dashboard
import com.app.nwf.ui.fragments.HandbookFragment
import com.app.nwf.ui.fragments.ImportantContact
import com.app.nwf.ui.fragments.InterestingStuffFragment
import com.app.nwf.ui.fragments.PublicationFragment
import com.app.nwf.ui.fragments.ResourceFragment
import com.app.nwf.ui.fragments.ResourceLibraryFragment
import com.app.nwf.ui.fragments.TrainingFragment
import com.app.nwf.ui.fragments.TranslatorFragment
import com.app.nwf.ui.fragments.VideoFragment
import com.app.nwf.utils.BaseUtils
import com.google.gson.Gson
import java.util.Locale

class HomeScreen : AppCompatActivity(), OnClickListener {

    private lateinit var langData: LangData
    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var binding: HomeScreenBinding
    private var FRAG_TAG = 1
    private var ukEmail = ""
    private var ukNumber = ""
    private var ukSubject = ""

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (FRAG_TAG == 1) {
                showDialog()
            } else if (FRAG_TAG == 2) {
                setFragment(Dashboard())
                FRAG_TAG = 1
            } else if (FRAG_TAG == 4) {
                setFragment(TrainingFragment())
                FRAG_TAG = 2
            } else if (FRAG_TAG == 5){
                setFragment(InterestingStuffFragment())
                FRAG_TAG = 2
            }else {
                setFragment(ResourceFragment())
                FRAG_TAG = 2
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_exit_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val noButton: Button = dialog.findViewById(R.id.cancelButton)
        noButton.text = langData.no
        noButton.setOnClickListener {
            dialog.dismiss()
        }

        val yesButton: Button = dialog.findViewById(R.id.exitButton)
        yesButton.text = langData.yes
        yesButton.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gson = Gson()
        val mString = BaseUtils.getLangData(this@HomeScreen)
        langData = gson.fromJson(mString, LangData::class.java)

        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                println("===============Language update")
                binding.snLanguage.text = BaseUtils.getLangCode(this@HomeScreen)?.uppercase(Locale.US)

                val kString = BaseUtils.getLangData(this@HomeScreen)
                langData = gson.fromJson(kString, LangData::class.java)

                binding.snDashboardTxt.text = langData.dashboard
                binding.snImpContactTxt.text = langData.importantContact
                binding.snConsultationsTxt.text = langData.consultation
                binding.snHandbookTxt.text = langData.childrenHandbook
                binding.snResourceLibTxt.text = langData.interestingStuff
                binding.snTrainingTxt.text = langData.watchLearn
                binding.snCypcTxt.text = langData.cypc
                binding.snPublicationsTxt.text = langData.publication
                binding.snTranslationTxt.text = langData.googleTranslator
                binding.snContactUsTxt.text = langData.contactUs
            }
        }

        val intentFilter = IntentFilter("update_language")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myReceiver, intentFilter, RECEIVER_EXPORTED)
        }else {
            ContextCompat.registerReceiver(
                this@HomeScreen,
                myReceiver,
                intentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        initView()

    }

    private fun initView() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("change_fragment"))

        binding.snUserName.text = BaseUtils.getUserName(this@HomeScreen)
        binding.snLanguage.text = BaseUtils.getLangCode(this@HomeScreen)?.uppercase(Locale.US)

        binding.snDashboardTxt.text = langData.dashboard
        binding.snImpContactTxt.text = langData.importantContact
        binding.snConsultationsTxt.text = langData.consultation
        binding.snHandbookTxt.text = langData.childrenHandbook
        binding.snResourceLibTxt.text = langData.interestingStuff
        binding.snTrainingTxt.text = langData.watchLearn
        binding.snCypcTxt.text = langData.cypc
        binding.snPublicationsTxt.text = langData.publication
        binding.snContactUsTxt.text = langData.contactUs

        binding.hsMenuIcon.setOnClickListener(this)
        binding.snDashboard.setOnClickListener(this)
        binding.snImpContact.setOnClickListener(this)
        binding.snConsultations.setOnClickListener(this)
        binding.snHandbook.setOnClickListener(this)
        binding.snResourceLib.setOnClickListener(this)
        binding.snTraining.setOnClickListener(this)
        binding.snContactUs.setOnClickListener(this)
        binding.snVideos.setOnClickListener(this)
        binding.snCypc.setOnClickListener(this)
        binding.snPublications.setOnClickListener(this)
        binding.hsHomeComing.setOnClickListener(this)
        binding.snTranslation.setOnClickListener(this)

        setFragment(Dashboard())

        binding.hsCall.setOnClickListener {
            showConfirmDialog(ukNumber, 1)
        }

        binding.hsEmail.setOnClickListener {
            showConfirmDialog(ukEmail, 2)
        }

        binding.snLanguage.setOnClickListener {
            startActivity(Intent(this, LanguageScreen::class.java).putExtra("type", 1))
        }

        getDetailsData()

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myReceiver)
    }

    private fun getDetailsData() {
        WebAPIManager.instance.getDetails(
            "Bearer " + BaseUtils.getToken(this@HomeScreen)!!)
            .enqueue(object : RemoteCallback<DetailsResponse>() {
                override fun onSuccess(response: DetailsResponse?) {
                    if (response?.success == true) {
                        if(response.data.size > 0 ) {
                            ukEmail = response.data[0].email!!
                            ukNumber = response.data[0].phone!!.toString()
                            ukSubject = response.data[0].subject!!.toString()
                        }
                    } else {
                        Toast.makeText(this@HomeScreen, "Something Wrong...", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onUnauthorized(throwable: Throwable) {
                    Toast.makeText(this@HomeScreen, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onFailed(throwable: Throwable) {
                    Toast.makeText(this@HomeScreen, throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onInternetFailed() {
                    Toast.makeText(
                        this@HomeScreen,
                        "Please Check Your internet..",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onEmptyResponse(message: String) {
                    Toast.makeText(this@HomeScreen, message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun showConfirmDialog(mNumber: String, type: Int) {
        val dialog = Dialog(this@HomeScreen)
        dialog.setContentView(R.layout.call_confirm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val mMsg: TextView = dialog.findViewById(R.id.ccMsg)
        if(type == 1){
            mMsg.text = "${langData.phone} $mNumber?"
        }else{
            mMsg.text = "${langData.mail} $mNumber?"
        }
        val noButton: Button = dialog.findViewById(R.id.ccNo)
        noButton.text = langData.no
        noButton.setOnClickListener {
            dialog.dismiss()
        }

        val yesButton: Button = dialog.findViewById(R.id.ccYes)
        yesButton.text = langData.yes
        yesButton.setOnClickListener {
            dialog.dismiss()
            if(type == 1) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$mNumber")
                startActivity(intent)
            }else{

                val sendEmail = Intent(Intent.ACTION_SENDTO)
                val uriText = "mailto:" + Uri.encode(mNumber) +
                        "?subject=" + Uri.encode(ukSubject).toString()
                val uri: Uri = Uri.parse(uriText)

                sendEmail.data = uri
                startActivity(Intent.createChooser(sendEmail, "Send Mail"))

//                ]

            }
        }

    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_main_content_id, fragment).commit()
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.getStringExtra("type").equals("1")) {
                setFragment(ImportantContact())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("2")) {
                setFragment(ConsultationFragment())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("3")) {
                setFragment(ResourceFragment())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("14")) {
                val args = Bundle()
                args.putString("id", intent.getStringExtra("id"))
                val mFrag = ResourceLibraryFragment()
                mFrag.arguments = args
                setFragment(mFrag)
                FRAG_TAG = 5
            } else if (intent.getStringExtra("type").equals("4")) {
                setFragment(InterestingStuffFragment())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("5")) {
                //setFragment(TrainingFragment())
                setFragment(VideoFragment())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("6")) {
                setFragment(ContactUsFragment())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("7")) {
                setFragment(CypcFragment())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("8")) {
                setFragment(PublicationFragment())
                FRAG_TAG = 2
            } else if (intent.getStringExtra("type").equals("10")) {
                val args = Bundle()
                args.putString("id", intent.getStringExtra("id"))
                val mFrag = HandbookFragment()
                mFrag.arguments = args
                setFragment(mFrag)
                FRAG_TAG = 3
            } else if (intent.getStringExtra("type").equals("11")) {
                val args = Bundle()
                args.putString("id", intent.getStringExtra("id"))
                val frag = VideoFragment()
                frag.arguments = args
                setFragment(frag)
                FRAG_TAG = 4
            } else if (intent.getStringExtra("type").equals("12")) {
                setFragment(Dashboard())
                FRAG_TAG = 1
            } else if (intent.getStringExtra("type").equals("13")) {
                setFragment(TranslatorFragment())
                FRAG_TAG = 2
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.hsMenuIcon -> {
                if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            R.id.hsHomeComing -> {
                setFragment(Dashboard())
                FRAG_TAG = 1
            }
            R.id.snDashboard -> {
                closeDrawer()
                setFragment(Dashboard())
                FRAG_TAG = 1
            }
            R.id.snImpContact -> {
                closeDrawer()
                setFragment(ImportantContact())
                FRAG_TAG = 2
            }
            R.id.snConsultations -> {
                closeDrawer()
                setFragment(ConsultationFragment())
                FRAG_TAG = 2
            }
            R.id.snHandbook -> {
                closeDrawer()
                setFragment(ResourceFragment())
                FRAG_TAG = 2
            }
            R.id.snResource_lib -> {
                closeDrawer()
                setFragment(InterestingStuffFragment())
                FRAG_TAG = 2
            }
            R.id.snTraining -> {
                closeDrawer()
                setFragment(VideoFragment())
                FRAG_TAG = 2
            }
            R.id.snContactUs -> {
                closeDrawer()
                setFragment(ContactUsFragment())
                FRAG_TAG = 2
            }
            R.id.snVideos -> {
                closeDrawer()
                setFragment(VideoFragment())
                FRAG_TAG = 2
            }
            R.id.snCypc -> {
                closeDrawer()
                setFragment(CypcFragment())
                FRAG_TAG = 2
            }
            R.id.snPublications -> {
                closeDrawer()
                setFragment(PublicationFragment())
                FRAG_TAG = 2
            }
            R.id.snTranslation -> {
                closeDrawer()
                setFragment(TranslatorFragment())
                FRAG_TAG = 2
            }

        }
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }
}