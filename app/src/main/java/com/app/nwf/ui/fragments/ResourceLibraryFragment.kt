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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentResourceLibraryBinding
import com.app.nwf.model.LangData
import com.app.nwf.model.ResourceData
import com.app.nwf.model.ResourceResponse
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

class ResourceLibraryFragment : Fragment() {

    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var langData: LangData
    private lateinit var binding: FragmentResourceLibraryBinding
    private var resourceList: MutableList<ResourceData> = mutableListOf()
    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false
    private var catId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResourceLibraryBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {
        catId = arguments?.getString("id", "0").toString()
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)
        getResourceData()

        binding.frlSearch.hint = langData.typeHere
        binding.frlTitleTxt.text = langData.interestingStuff

        binding.frlSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                if (searchText.equals("")) {
                    binding.frlRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.frlRecycle.adapter = ResourceLibAdapter(resourceList)
                } else {
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
                binding.frlSearch.hint = langData.typeHere
                binding.frlTitleTxt.text = langData.interestingStuff
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
        var mList: MutableList<ResourceData> = mutableListOf()
        for (item in resourceList) {
            if (item.title?.contains(query, ignoreCase = true) == true) {
                mList.add(item)
            }
        }
        binding.frlRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.frlRecycle.adapter = ResourceLibAdapter(mList)

    }

    private fun getResourceData() {
        GlobalProgressBarUtil.show(requireContext())

        val mLimit: RequestBody = "30".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPage: RequestBody = currentPage.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mCatId: RequestBody = catId.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        WebAPIManager.instance.getResource(
            mLimit, mPage, mCatId,
            "Bearer " + BaseUtils.getToken(requireContext())!!
        )
            .enqueue(object : RemoteCallback<ResourceResponse>() {
                override fun onSuccess(response: ResourceResponse?) {
                    if (response?.success == true) {
                        lastPage = response.total_page!!

                        if (currentPage == 1) {
                            resourceList.clear()
                            resourceList = response.data
                        } else {
                            resourceList.addAll(response.data)
                        }

                        if (isAdded) {
                            Glide.with(requireContext()).load(response.banner)
                                .into(binding.frlBanner)
                            if (resourceList.size > 0) {
                                binding.frlRecycle.visibility = View.VISIBLE
                                binding.frlMessage.visibility = View.GONE
                                binding.frlRecycle.layoutManager =
                                    GridLayoutManager(requireContext(), 2)
                                binding.frlRecycle.adapter = ResourceLibAdapter(resourceList)
                            } else {
                                binding.frlMessage.visibility = View.VISIBLE
                                binding.frlRecycle.visibility = View.GONE
                            }

                            if (currentPage < lastPage) {
                                currentPage++
                                isLoading = true
                                getResourceData()
                            }
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


    inner class ResourceLibAdapter(var resourceList: MutableList<ResourceData>) :
        RecyclerView.Adapter<ResourceLibAdapter.DataHolder>() {

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
            holder.mKnowMore.visibility = View.VISIBLE
            holder.mName.text = resourceList[position].title
            holder.mKnowMore.text = langData.knowMore
            Glide.with(requireContext()).load(resourceList[position].image).into(holder.mIcon)

            // holder.mKnowMore.text = langData.
            holder.mKnowMore.setOnClickListener {
                if (resourceList[position].url?.contains(".pdf") == true) {
                    startActivity(
                        Intent(requireContext(), PdfWebView::class.java).putExtra(
                            "url",
                            resourceList[position].url
                        ).putExtra("title", resourceList[position].title)
                    )
                } else {
                    startActivity(
                        Intent(requireContext(), XlsWebView::class.java).putExtra(
                            "url",
                            resourceList[position].url
                        ).putExtra("title", resourceList[position].title)
                    )
                }
            }

        }

        override fun getItemCount(): Int {
            return resourceList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rhImage)!!
            val mName = itemView.findViewById<TextView>(R.id.rhName)!!

            //            val mRoot = itemView.findViewById<LinearLayout>(R.id.rmMain)!!
            val mKnowMore = itemView.findViewById<Button>(R.id.rcoKnowMore)!!
        }
    }

}