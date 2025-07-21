package com.app.nwf.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentPublicationBinding
import com.app.nwf.model.LangData
import com.app.nwf.model.PublicationData
import com.app.nwf.model.PublicationResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.ui.PdfWebView
import com.app.nwf.ui.XlsWebView
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PublicationFragment : Fragment() {

    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var langData: LangData
    private lateinit var binding: FragmentPublicationBinding
    private var publicationList : MutableList<PublicationData> = mutableListOf()
    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPublicationBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)
        getPublicationData()

        binding.fpbSearch.hint = langData.typeHere
        binding.fpbTitleTxt.text = langData.publication

        binding.fpbSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if(searchText.equals("")){
                    binding.fpbRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.fpbRecycle.adapter = PublicationAdapter(publicationList)
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
                binding.fpbSearch.hint = langData.typeHere
                binding.fpbTitleTxt.text = langData.publication
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

    private fun filterList(query: String) {
        var mList: MutableList<PublicationData> = mutableListOf()
        for (item in publicationList) {
            if (item.name?.contains(query, ignoreCase = true) == true) {
                mList.add(item)
            }
        }
        binding.fpbRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.fpbRecycle.adapter = PublicationAdapter(mList)

    }

    private fun getPublicationData() {
        GlobalProgressBarUtil.show(requireContext())
        val mLimit: RequestBody = "20".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPage: RequestBody = currentPage.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        WebAPIManager.instance.getPublication(mLimit, mPage,
            "Bearer " + BaseUtils.getToken(requireContext())!!)
            .enqueue(object : RemoteCallback<PublicationResponse>() {
                override fun onSuccess(response: PublicationResponse?) {
                    if (response?.success == true) {
                        Glide.with(requireContext()).load(response.banner).into(binding.fpbBanner)
                        lastPage = response.totalPage!!
                        if(currentPage == 1) {
                            publicationList.clear()
                            publicationList = response.data
                        }else{
                            publicationList.addAll(response.data)
                        }

                        if(publicationList.size>0) {
                            binding.fpbRecycle.visibility = View.VISIBLE
                            binding.fpbMessage.visibility = View.GONE
                            binding.fpbRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
                            binding.fpbRecycle.adapter = PublicationAdapter(publicationList)
                        }else{
                            binding.fpbMessage.visibility = View.VISIBLE
                            binding.fpbRecycle.visibility = View.GONE
                        }

                        if (currentPage < lastPage) {
                            currentPage++
                            isLoading = true
                            getPublicationData()
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


    inner class PublicationAdapter(var pubList: MutableList<PublicationData>) : RecyclerView.Adapter<PublicationAdapter.DataHolder>() {

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
            return DataHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_publication, parent, false))
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {

            holder.mName.text = pubList[position].name
            Glide.with(requireContext()).load(pubList[position].image).into(holder.mIcon)

            holder.itemView.setOnClickListener {

                if(pubList[position].url?.contains(".pdf") == true){
                    startActivity(Intent(requireContext(), PdfWebView::class.java).putExtra("url", pubList[position].url)
                        .putExtra("title", pubList[position].name))
                }else {
                    startActivity(
                        Intent(requireContext(), XlsWebView::class.java).putExtra(
                            "url",
                            pubList[position].url
                        )
                            .putExtra("title", pubList[position].name)
                    )
                }
            }

//            holder.mRoot.setOnTouchListener { view, event ->
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        // Add elevation when clicked
//                        ViewCompat.setElevation(view, resources.getDimension(com.intuit.sdp.R.dimen._3sdp))
//                    }
//                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                        // Remove elevation when the click is released or canceled
//                        ViewCompat.setElevation(view, 0f)
//                    }
//                }
//                false
//            }


        }

        override fun getItemCount(): Int {
            return pubList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rpbImage)!!
              val mName = itemView.findViewById<TextView>(R.id.rpbTitle)!!
//            val mRoot = itemView.findViewById<LinearLayout>(R.id.rmMain)!!
        }
    }

}