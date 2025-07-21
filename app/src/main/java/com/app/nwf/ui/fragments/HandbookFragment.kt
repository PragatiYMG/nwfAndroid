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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentHandbookBinding
import com.app.nwf.model.LangData
import com.app.nwf.model.handbook.HandbookData
import com.app.nwf.model.handbook.HandbookResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.ui.PdfWebView
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class HandbookFragment : Fragment() {

    private lateinit var langData: LangData
    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var binding: FragmentHandbookBinding
    private var handbookList: MutableList<HandbookData> = mutableListOf()
    private var langId = "0"
    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHandbookBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {
        langId = arguments?.getString("id", "0").toString()
        getHandbookData()

        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)

        binding.fhTitleTxt.text = langData.handbook



        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)

                binding.fhTitleTxt.text = langData.handbook
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

    private fun getHandbookData() {
        GlobalProgressBarUtil.show(requireContext())

        val mLangId: RequestBody = langId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mLimit: RequestBody = "20".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPage: RequestBody = currentPage.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        WebAPIManager.instance.getHandbook(
            mLimit, mLangId, mPage,
            "Bearer " + BaseUtils.getToken(requireContext())!!
        )
            .enqueue(object : RemoteCallback<HandbookResponse>() {
                override fun onSuccess(response: HandbookResponse?) {
                    if (response?.success == true) {
                        if (isAdded && context != null) {
                            Glide.with(requireContext()).load(response.banner)
                                .into(binding.fhBanner)

                            lastPage = response.total!!
                            if (currentPage == 1) {
                                handbookList.clear()
                                handbookList = response.data
                            } else {
                                handbookList.addAll(response.data)
                            }
                            if (handbookList.size > 0) {
                                binding.fhRecycle.visibility = View.VISIBLE
                                binding.fhMessage.visibility = View.GONE
                                binding.fhRecycle.layoutManager =
                                    GridLayoutManager(requireContext(), 2)
                                binding.fhRecycle.adapter = HandbookAdapter()
                            } else {
                                binding.fhMessage.visibility = View.VISIBLE
                                binding.fhRecycle.visibility = View.GONE
                            }

                            if (currentPage < lastPage) {
                                currentPage++
                                isLoading = true
                                getHandbookData()
                            }
                        }

//                        binding.fhRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                                super.onScrolled(recyclerView, dx, dy)
//
//
//
//                                if (dy>0) {
//                                    val layoutManager = binding.fhRecycle.layoutManager as LinearLayoutManager
//                                    val visibleItemCount = layoutManager.childCount
//                                    val totalItemCount = layoutManager.itemCount
//                                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
//
//                                    if (!isLoading && (currentPage < lastPage)) {
//                                        if (visibleItemCount + firstVisibleItem >= totalItemCount) {
//                                            currentPage++
//                                            isLoading = true
//                                            getHandbookData()
//                                        }
//                                    }
//                                }
//                            }
//                        })

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
            return DataHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_handbook, parent, false)
            )
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {

            holder.mName.text = handbookList[position].name
            Glide.with(requireContext()).load(handbookList[position].image).into(holder.mIcon)

            holder.itemView.setOnClickListener {
                startActivity(
                    Intent(requireContext(), PdfWebView::class.java).putExtra(
                        "url",
                        handbookList[position].pdf
                    ).putExtra("title", handbookList[position].name)
                )
            }

        }

        override fun getItemCount(): Int {
            return handbookList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rhImage)!!
            val mName = itemView.findViewById<TextView>(R.id.rhName)!!
//            val mRoot = itemView.findViewById<LinearLayout>(R.id.rmMain)!!
        }
    }

}