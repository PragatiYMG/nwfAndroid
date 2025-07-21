package com.app.nwf.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentConsultationBinding
import com.app.nwf.model.CommonResponse
import com.app.nwf.model.LangData
import com.app.nwf.model.consult.AnsRequest
import com.app.nwf.model.consult.Answer
import com.app.nwf.model.consult.ConsultData
import com.app.nwf.model.consult.ConsultResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson


class ConsultationFragment : Fragment() {

    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var langData: LangData
    private lateinit var binding: FragmentConsultationBinding
    private var quesList: MutableList<ConsultData> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentConsultationBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)
        getConsultData()

        binding.fcSubmit.text = langData.submit
        binding.fcTitleTxt.text = langData.consultation

        binding.fcSubmit.setOnClickListener {
            val request = AnsRequest()
            request.token = BaseUtils.getToken(requireContext())
            val ansList: MutableList<Answer> = mutableListOf()
            var notFound = false
            for (mQues in quesList){

                val ans = Answer()
                ans.types = mQues.types
                ans.questionId = mQues.id
                var found = false
                if(mQues.types?.equals("MCQ") == true){
                    for(mOpt in mQues.answerId){
                        if(mOpt.status == 20){
                            ans.answerId = mOpt.id
                            found = true
                        }
                    }
                }else{
                    if(mQues.answer != ""){
                        ans.answer = mQues.answer
                        found = true
                    }
                }

                if(!found){
                    notFound = true
                }
                ansList.add(ans)
            }
            if (!notFound){
                request.answers = ansList
                saveAnsApi(request)
            }else{
                Toast.makeText(requireContext(), "Answer all the question", Toast.LENGTH_SHORT).show()
            }
        }

        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)

                binding.fcSubmit.text = langData.submit
                binding.fcTitleTxt.text = langData.consultation
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

    private fun showMessage(message: String?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.confirm_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val mMessage: TextView = dialog.findViewById(R.id.cdMessage)
        val mTitle: TextView = dialog.findViewById(R.id.cdTitle)
        mTitle.text = langData.thankYou

        mMessage.visibility = View.GONE
        val okBtn: Button = dialog.findViewById(R.id.cdOk)
        okBtn.text = langData.gotIt
        okBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent("change_fragment")
            intent.putExtra("type", "12")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

    }

    private fun saveAnsApi(request: AnsRequest) {
        GlobalProgressBarUtil.show(requireContext())
        WebAPIManager.instance.saveAnswer(
            "Bearer " + BaseUtils.getToken(requireContext())!!, request)
            .enqueue(object : RemoteCallback<CommonResponse>() {
                override fun onSuccess(response: CommonResponse?) {
                    if (response?.success == true) {
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

    private fun showMessage2(message: String?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.confirm_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val mMessage: TextView = dialog.findViewById(R.id.cdMessage)
        val mTitle: TextView = dialog.findViewById(R.id.cdTitle)
        mTitle.visibility = View.GONE
        mMessage.visibility = View.VISIBLE
        mMessage.text = message
        val okBtn: Button = dialog.findViewById(R.id.cdOk)
        okBtn.text = langData.gotIt
        okBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent("change_fragment")
            intent.putExtra("type", "12")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

    }

    private fun getConsultData() {
        GlobalProgressBarUtil.show(requireContext())
        WebAPIManager.instance.getConsultation(
            "Bearer " + BaseUtils.getToken(requireContext())!!)
            .enqueue(object : RemoteCallback<ConsultResponse>() {
                override fun onSuccess(response: ConsultResponse?) {
                    if (response?.success == true) {
                        quesList = response.data
                        Glide.with(requireContext()).load(response.banner).into(binding.fcBanner)
                        if(quesList.size>0) {
                            binding.fcRecycle.visibility = View.VISIBLE
                            binding.fcSubmit.visibility = View.VISIBLE
                            binding.fcMessage.visibility = View.GONE
                            binding.fcRecycle.layoutManager = LinearLayoutManager(requireContext())
                            binding.fcRecycle.adapter = ConsultAdapter()
                        }else{
                            showMessage2(langData.consultationMsg)
//                            binding.fcMessage.visibility = View.VISIBLE
//                            binding.fcRecycle.visibility = View.GONE
//                            binding.fcSubmit.visibility = View.GONE
                        }

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


    inner class ConsultAdapter : RecyclerView.Adapter<ConsultAdapter.DataHolder>() {

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
            return DataHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_consult, parent, false))
        }

        override fun onBindViewHolder(holder: DataHolder, @SuppressLint("RecyclerView") position: Int) {

            holder.mQuestion.text = "Q${position+1} - " + quesList[position].question
            if(quesList[position].types?.equals("MCQ") == true){
                holder.mRecycle.visibility = View.VISIBLE
                holder.mAns.visibility = View.GONE
                holder.mRecycle.layoutManager = LinearLayoutManager(requireContext())
                holder.mRecycle.adapter = OptionAdapter(position)
            }else{
                holder.mRecycle.visibility = View.GONE
                holder.mAns.visibility = View.VISIBLE

            }

            holder.mAns.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // This method is called before the text is changed.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // This method is called as the text is changing.
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    quesList[position].answer = text
                   // notifyDataSetChanged()
                }
            })


        }

        override fun getItemCount(): Int {
            return quesList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mQuestion = itemView.findViewById<TextView>(R.id.rcoQues)!!
            val mRecycle = itemView.findViewById<RecyclerView>(R.id.rcoOption)!!
            val mAns = itemView.findViewById<EditText>(R.id.rcoAns)!!
        }
    }

    inner class OptionAdapter(var mPos: Int) : RecyclerView.Adapter<OptionAdapter.DataHolder>() {

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
            return DataHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_option, parent, false))
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {

            holder.mOption.text = quesList[mPos].answerId[position].consultationOption
            holder.mOption.isChecked = quesList[mPos].answerId[position].status == 20

            holder.mOption.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    for (index in 0 until quesList[mPos].answerId.size) {
                        if (position == index) {
                            quesList[mPos].answerId[index].status = 20
                        } else {
                            quesList[mPos].answerId[index].status = 1
                        }
                    }
                    binding.fcRecycle.adapter?.notifyDataSetChanged()
                }
            }

        }

        override fun getItemCount(): Int {
            return quesList[mPos].answerId.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mOption = itemView.findViewById<RadioButton>(R.id.rOption)!!
        }
    }

}