package com.app.nwf.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentResourceBinding
import com.app.nwf.model.LangData
import com.app.nwf.model.LanguageData
import com.app.nwf.model.LanguageResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson

class ResourceFragment : Fragment() {

    private lateinit var langData: LangData
    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var binding: FragmentResourceBinding
    private var langList : MutableList<LanguageData> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentResourceBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {

        getLanguages()
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)

        binding.frTitleTxt.text = langData.childrenHandbook


        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)

                binding.frTitleTxt.text = langData.childrenHandbook
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

    private fun getLanguages() {
        GlobalProgressBarUtil.show(requireContext())
        WebAPIManager.instance.getLanguage(
            "Bearer " + BaseUtils.getToken(requireContext())!!)
            .enqueue(object : RemoteCallback<LanguageResponse>() {
                override fun onSuccess(response: LanguageResponse?) {
                    if (response?.success == true) {
                        langList = response.data
                        Glide.with(requireContext()).load(response.banner).into(binding.frBanner)
                        if(langList.size>0) {
                            binding.frRecycle.visibility = View.VISIBLE
                            binding.frMessage.visibility = View.GONE
                            binding.frRecycle.layoutManager = LinearLayoutManager(requireContext())
                            binding.frRecycle.adapter = HandbookAdapter()
                        }else{
                            binding.frMessage.visibility = View.VISIBLE
                            binding.frRecycle.visibility = View.GONE
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


    inner class HandbookAdapter : RecyclerView.Adapter<HandbookAdapter.DataHolder>() {

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
            return DataHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_resource, parent, false))
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {

            holder.mName.text = langList[position].name
            holder.itemView.setOnClickListener {
                val intent = Intent("change_fragment")
                intent.putExtra("type", "10")
                intent.putExtra("id", langList[position].id.toString())
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

        }

        override fun getItemCount(): Int {
            return langList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val mIcon = itemView.findViewById<ImageView>(R.id.rmIcon)!!
            val mName = itemView.findViewById<TextView>(R.id.rrName)!!
//            val mRoot = itemView.findViewById<LinearLayout>(R.id.rmMain)!!
        }
    }

}