package com.app.nwf.ui.fragments

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentContactUsBinding
import com.app.nwf.model.CaptchaResponse
import com.app.nwf.model.CommonResponse
import com.app.nwf.model.LangData
import com.app.nwf.model.SubjectData
import com.app.nwf.model.SubjectResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.regex.Pattern

class ContactUsFragment : Fragment() {

    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var langData: LangData
    private lateinit var binding: FragmentContactUsBinding
    private var subList: MutableList<SubjectData> = mutableListOf()
    private var subId = ""
    private var captcha = ""
    private val emailPattern: Pattern = Pattern.compile(
        "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactUsBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)

        getCaptcha()
        getSubjects()

        binding.fcName.hint = langData.name
        binding.fcEmail.hint = langData.yourEmail
        binding.fcSubject.hint = langData.subject
        binding.fcMessage.hint = langData.enterMessage
        binding.captchaText.hint = langData.enterCaptcha
        binding.fcSubmit.text = langData.submitDetails
        val formattedText = HtmlCompat.fromHtml(langData.contactMessage!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.fcDetailText.text = formattedText
        binding.fcTitleTxt.text = langData.contactUs

        binding.fcSubmit.setOnClickListener {
            if (isValidate()) {
                contactUsApi()
            }
        }

        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)

                binding.fcName.hint = langData.name
                binding.fcEmail.hint = langData.yourEmail
                binding.fcSubject.hint = langData.subject
                binding.fcMessage.hint = langData.enterMessage
                binding.captchaText.hint = langData.enterCaptcha
                binding.fcSubmit.text = langData.submitDetails
                val formattedText = HtmlCompat.fromHtml(langData.contactMessage!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding.fcDetailText.text = formattedText
                binding.fcTitleTxt.text = langData.contactUs

            }
        }

        val intentFilter = IntentFilter("update_language")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(myReceiver, intentFilter, RECEIVER_EXPORTED)
        }else {
            ContextCompat.registerReceiver(
                requireContext(),
                myReceiver,
                intentFilter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(myReceiver)
    }

    private fun getCaptcha() {
        GlobalProgressBarUtil.show(requireContext())
        WebAPIManager.instance.getCaptcha()
            .enqueue(object : RemoteCallback<CaptchaResponse>() {
                override fun onSuccess(response: CaptchaResponse?) {
                    if (response?.success == true) {
                        captcha = response.data.toString()
                        binding.capchaHint.text = captcha
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

    private fun contactUsApi() {
        GlobalProgressBarUtil.show(requireContext())
        val mName: RequestBody =
            binding.fcName.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mEmail: RequestBody =
            binding.fcEmail.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mSubject: RequestBody =
            binding.fcSubject.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mMessage: RequestBody =
            binding.fcMessage.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        WebAPIManager.instance.getContactUs(
            mName, mEmail, mSubject, mMessage,
            "Bearer " + BaseUtils.getToken(requireContext())!!,
        )
            .enqueue(object : RemoteCallback<CommonResponse>() {
                override fun onSuccess(response: CommonResponse?) {
                    if (response?.success == true) {
                        binding.fcName.setText("")
                        binding.fcEmail.setText("")
                        binding.fcSubject.setText("")
                        binding.fcMessage.setText("")
//                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
//                        val intent = Intent("change_fragment")
//                        intent.putExtra("type", "12")
//                        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                        showMessage(response.message)

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

    private fun showMessage(message: String?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.confirm_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val mTitle: TextView = dialog.findViewById(R.id.cdTitle)
        mTitle.text = langData.thankYou
        val mMessage: TextView = dialog.findViewById(R.id.cdMessage)
        mMessage.text = langData.contactSubmit
        val okBtn: Button = dialog.findViewById(R.id.cdOk)
        okBtn.text = langData.ok
        okBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent("change_fragment")
            intent.putExtra("type", "12")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }


    }

    private fun isValidate(): Boolean {
        if (binding.fcName.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), langData.enterYourName, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.fcEmail.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), langData.enterYourEmail, Toast.LENGTH_LONG).show()
            return false
        } else if (!emailPattern.matcher(binding.fcEmail.text.toString().trim()).matches()) {
            Toast.makeText(requireContext(), langData.emailValid, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.fcSubject.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), langData.enterSubject, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.fcMessage.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), langData.enterMessage, Toast.LENGTH_LONG).show()
            return false
        }
        else if(binding.captchaText.text.toString().isEmpty()){
            Toast.makeText(requireContext(), langData.enterCaptcha, Toast.LENGTH_LONG).show()
            return false
        } else if(!binding.captchaText.text.toString().trim().equals(captcha, false)){
            binding.captchaText.setText("")
            getCaptcha()
            Toast.makeText(requireContext(), langData.invalidCaptcha, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun getSubjects() {
        GlobalProgressBarUtil.show(requireContext())
        WebAPIManager.instance.getSubject(
            "Bearer " + BaseUtils.getToken(requireContext())!!
        )
            .enqueue(object : RemoteCallback<SubjectResponse>() {
                override fun onSuccess(response: SubjectResponse?) {
                    if (response?.success == true) {
                        subList = response.data
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

    private fun showSubjectDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.subject_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val mRecycle: RecyclerView = dialog.findViewById(R.id.sdRecycle)
        mRecycle.layoutManager = LinearLayoutManager(requireContext())
        mRecycle.adapter = SubjectAdapter(dialog)
    }

    inner class SubjectAdapter(var dialog: Dialog) :
        RecyclerView.Adapter<SubjectAdapter.DataHolder>() {

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
                LayoutInflater.from(parent.context).inflate(R.layout.row_subject, parent, false)
            )
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {
            holder.mName.text = subList[position].name

            holder.itemView.setOnClickListener {
                subId = subList[position].id.toString()
               // binding.fcSubject.text = subList[position].name
                dialog.dismiss()
            }
        }

        override fun getItemCount(): Int {
            return subList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val mName = itemView.findViewById<TextView>(R.id.rsName)!!
        }
    }
}