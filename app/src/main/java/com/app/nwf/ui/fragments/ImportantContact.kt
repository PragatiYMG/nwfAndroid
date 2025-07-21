package com.app.nwf.ui.fragments

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentImportantContactBinding
import com.app.nwf.model.ContactData
import com.app.nwf.model.ContactResponse
import com.app.nwf.model.LangData
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class ImportantContact : Fragment() {

    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var langData: LangData
    private lateinit var binding: FragmentImportantContactBinding
    private var contactList: MutableList<ContactData> = mutableListOf()
    private var currentPage = 1
    private var lastPage = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImportantContactBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)
        getImportantContact()

        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)
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
        requireActivity().unregisterReceiver(myReceiver)
    }

    private fun getImportantContact() {
        GlobalProgressBarUtil.show(requireContext())

       // val mKeyword: RequestBody = BaseUtils.getUserEmail(requireContext())!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mLimit: RequestBody = "20".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val mPage: RequestBody = currentPage.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        isLoading = true
        WebAPIManager.instance.importantContact(
            "Bearer " + BaseUtils.getToken(requireContext())!!,
            mLimit,
            mPage)
            .enqueue(object : RemoteCallback<ContactResponse>() {
                override fun onSuccess(response: ContactResponse?) {
                    isLoading = false
                    if (response?.success == true) {
                        lastPage = response.total_page!!
                        if(currentPage == 1) {
                            contactList.clear()
                            contactList = response.data
                        }else{
                            contactList.addAll(response.data)
                        }
                        if(contactList.size>0) {
                            binding.ficMessage.visibility = View.GONE
                            binding.ficRecycle.visibility = View.VISIBLE
                            binding.ficRecycle.layoutManager = LinearLayoutManager(requireContext())
                            binding.ficRecycle.adapter = ContactAdapter()
                        }else{
                            binding.ficMessage.visibility = View.VISIBLE
                            binding.ficRecycle.visibility = View.GONE
                        }

                        binding.ficRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)

                                if (dy>0) {
                                    val layoutManager = binding.ficRecycle.layoutManager as LinearLayoutManager
                                    val visibleItemCount = layoutManager.childCount
                                    val totalItemCount = layoutManager.itemCount
                                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                                    if (!isLoading && (currentPage < lastPage)) {
                                        if (visibleItemCount + firstVisibleItem >= totalItemCount) {
                                            currentPage++
                                            isLoading = true
                                            getImportantContact()
                                        }
                                    }
                                }
                            }
                        })

                    } else {
                        Toast.makeText(requireContext(), "Something Wrong...", Toast.LENGTH_LONG)
                            .show()
                    }
                    GlobalProgressBarUtil.hide()
                }

                override fun onUnauthorized(throwable: Throwable) {
                    isLoading = false
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onFailed(throwable: Throwable) {
                    isLoading = false
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_LONG).show()
                }

                override fun onInternetFailed() {
                    isLoading = false
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(
                        requireContext(),
                        "Please Check Your internet..",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onEmptyResponse(message: String) {
                    isLoading = false
                    GlobalProgressBarUtil.hide()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                }
            })
    }

    inner class ContactAdapter : RecyclerView.Adapter<ContactAdapter.DataHolder>() {

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
                LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false)
            )
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {

            holder.mName.text = contactList[position].name
            if(contactList[position].email?.isEmpty() == true){
                holder.mEmail.visibility = View.GONE
            }else {
                if(contactList[position].email == null){
                    holder.mEmail.visibility = View.GONE
                }else {
                    holder.mEmail.visibility = View.VISIBLE
                    holder.mEmail.text = contactList[position].email
                }
            }
            if(contactList[position].designation?.isEmpty() == true){
                holder.mDesignation.visibility = View.GONE
            }else{
                holder.mDesignation.visibility = View.VISIBLE
                holder.mDesignation.text = contactList[position].designation
            }
            holder.mContact.text = contactList[position].phone.toString()

            holder.mDescription.text = Html.fromHtml(contactList[position].description.toString().trim(),
                Html.FROM_HTML_MODE_COMPACT)


            Glide.with(requireContext())
                .load(contactList[position].image)
                .into(holder.mIcon)

            holder.itemView.setOnClickListener {
                showConfirmDialog(contactList[position].phone.toString())
            }

            holder.mRoot.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Add elevation when clicked
                        ViewCompat.setElevation(
                            view,
                            resources.getDimension(com.intuit.sdp.R.dimen._8sdp)
                        )
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // Remove elevation when the click is released or canceled
                        ViewCompat.setElevation(view, 0f)
                    }
                }
                false
            }

        }

        private fun showConfirmDialog(mNumber: String) {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.call_confirm)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.show()

            val mMsg: TextView = dialog.findViewById(R.id.ccMsg)

            mMsg.text = "${langData.phone}\n$mNumber?"

            val noButton: Button = dialog.findViewById(R.id.ccNo)
            noButton.text = langData.no
            noButton.setOnClickListener {
                dialog.dismiss()
            }

            val yesButton: Button = dialog.findViewById(R.id.ccYes)
            yesButton.text = langData.yes
            yesButton.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$mNumber")
                startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return contactList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rcImage)!!
            val mName = itemView.findViewById<TextView>(R.id.rcName)!!
            val mDesignation = itemView.findViewById<TextView>(R.id.rcDesignation)!!
            val mEmail = itemView.findViewById<TextView>(R.id.rcEmail)!!
            val mContact = itemView.findViewById<TextView>(R.id.rcContact)!!
            val mDescription = itemView.findViewById<TextView>(R.id.rcDescription)!!
            val mRoot = itemView.findViewById<LinearLayout>(R.id.rcMain)!!
        }
    }

}