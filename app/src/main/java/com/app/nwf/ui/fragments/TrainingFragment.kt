package com.app.nwf.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentTrainingBinding
import com.app.nwf.model.LangData
import com.app.nwf.model.TrainingData
import com.app.nwf.model.TrainingResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TrainingFragment : Fragment() {

    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var langData: LangData
    private lateinit var binding: FragmentTrainingBinding
    private var trainingList: MutableList<TrainingData> = mutableListOf()
    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentTrainingBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {
        getTrainingData()

        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)

        binding.ftTitleTxt.text = langData.watchLearn

        binding.ftSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if(searchText.equals("")){
                    binding.ftRecycle.layoutManager = LinearLayoutManager(requireContext())
                    binding.ftRecycle.adapter = TrainingAdapter(trainingList)
                }else {
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
                binding.ftTitleTxt.text = langData.watchLearn
            }
        }

        val intentFilter = IntentFilter("update_language")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(myReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
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


    private fun filterList(query: String) {
        var mList:MutableList<TrainingData>  = mutableListOf()
        for (item in trainingList) {
            if (item.name?.contains(query, ignoreCase = true) == true) {
                mList.add(item)
            }
        }
        binding.ftRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.ftRecycle.adapter = TrainingAdapter(mList)
    }


    private fun getTrainingData() {
        GlobalProgressBarUtil.show(requireContext())
        val mLimit: RequestBody = "20".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPage: RequestBody = currentPage.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        WebAPIManager.instance.getTraining(mLimit, mPage,
            "Bearer " + BaseUtils.getToken(requireContext())!!)
            .enqueue(object : RemoteCallback<TrainingResponse>() {
                override fun onSuccess(response: TrainingResponse?) {
                    if (response?.success == true) {
                        Glide.with(requireContext()).load(response.banner).into(binding.ftBanner)
                        lastPage = response.total_page!!
                        if(currentPage == 1) {
                            trainingList.clear()
                            trainingList = response.data
                        }else{
                            trainingList.addAll(response.data)
                        }
                        if(trainingList.size>0) {
                            binding.ftRecycle.visibility = View.VISIBLE
                            binding.ftMessage.visibility = View.GONE
                            binding.ftRecycle.layoutManager = LinearLayoutManager(requireContext())
                            binding.ftRecycle.adapter = TrainingAdapter(trainingList)
                        }else{
                            binding.ftMessage.visibility = View.VISIBLE
                            binding.ftRecycle.visibility = View.GONE
                        }

                        if (currentPage < lastPage) {
                                currentPage++
                                isLoading = true
                                getTrainingData()
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

    inner class TrainingAdapter(var traneeList: MutableList<TrainingData>) : RecyclerView.Adapter<TrainingAdapter.DataHolder>() {

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
            return DataHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_training, parent, false))
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {

            Glide.with(requireContext()).load(traneeList[position].image).into(holder.mIcon)
            holder.mName.text = traneeList[position].name
            holder.mTitle.text = traneeList[position].title
            holder.mDesc.text = Html.fromHtml(traneeList[position].description, Html.FROM_HTML_MODE_LEGACY)
          //  holder.mDate.text = BaseUtils.convertDateTime(trainingList[position].createdAt!!)
            holder.mDate.text = traneeList[position].createdAt!!

            holder.mWatchNow.setOnClickListener {
                val intent = Intent("change_fragment")
                intent.putExtra("type", "11")
                intent.putExtra("id", traneeList[position].id.toString())
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }

        }

        override fun getItemCount(): Int {
            return traneeList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rtImage)!!
            val mName = itemView.findViewById<TextView>(R.id.rtName)!!
            val mTitle = itemView.findViewById<TextView>(R.id.rtTitle)!!
            val mDesc = itemView.findViewById<TextView>(R.id.rtDesc)!!
            val mDate = itemView.findViewById<TextView>(R.id.rtDate)!!
            val mWatchNow = itemView.findViewById<LinearLayout>(R.id.rtWatchNow)!!
        }
    }

}