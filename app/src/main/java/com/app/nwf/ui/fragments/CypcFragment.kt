package com.app.nwf.ui.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.content.Context.RECEIVER_EXPORTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentCypcBinding
import com.app.nwf.model.LangData
import com.app.nwf.model.cypc.CypcData
import com.app.nwf.model.cypc.CypcResponse
import com.app.nwf.network.RemoteCallback
import com.app.nwf.network.WebAPIManager
import com.app.nwf.ui.PdfWebView
import com.app.nwf.ui.XlsWebView
import com.app.nwf.utils.BaseUtils
import com.app.nwf.utils.GlobalProgressBarUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.io.File

class CypcFragment : Fragment() {

    private lateinit var langData: LangData
    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var binding: FragmentCypcBinding
    private var cypcList: MutableList<CypcData> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCypcBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {

        getCypcData()
        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)

        binding.fcyTitleTxt.text = langData.cypcFull


        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)

                binding.fcyTitleTxt.text = langData.cypcFull
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

    private fun getCypcData() {
        GlobalProgressBarUtil.show(requireContext())
        WebAPIManager.instance.getCypc(
            "Bearer " + BaseUtils.getToken(requireContext())!!)
            .enqueue(object : RemoteCallback<CypcResponse>() {
                override fun onSuccess(response: CypcResponse?) {
                    if (response?.success == true) {
                        cypcList = response.data
                        Glide.with(requireContext()).load(response.banner).into(binding.fcyBanner)
                        if(cypcList.size>0) {
                            binding.fcyRecycle.visibility = View.VISIBLE
                            binding.fcyMessage.visibility = View.GONE
                            binding.fcyRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
                            binding.fcyRecycle.adapter = CypcAdapter()
                        }else{
                            binding.fcyMessage.visibility = View.VISIBLE
                            binding.fcyRecycle.visibility = View.GONE
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

    inner class CypcAdapter : RecyclerView.Adapter<CypcAdapter.DataHolder>() {

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
                LayoutInflater.from(parent.context).inflate(R.layout.row_cypc, parent, false)
            )
        }

        override fun onBindViewHolder(holder: DataHolder, @SuppressLint("RecyclerView") position: Int) {

            holder.mName.text = cypcList[position].title
            Glide.with(holder.itemView.context).load(cypcList[position].icon).into(holder.mIcon)

            holder.itemView.setOnClickListener {
                if(cypcList[position].files?.contains(".xls", true) == true){
                    val xlsFileUrl = cypcList[position].files
                    val fileName = "${System.currentTimeMillis()}.xls"
                    val request = DownloadManager.Request(Uri.parse(xlsFileUrl))
                        .setTitle("XLS Download") // Set the title for the download
                        .setDescription("Downloading XLS file") // Set a description
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Show a notification when download is complete
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

                    val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val downloadId = downloadManager.enqueue(request)

                    val onComplete = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            val receivedDownloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                            if (receivedDownloadId == downloadId) {
                                val xlsFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                                val xlsUri = FileProvider.getUriForFile(requireContext(), "com.app.nwf.fileprovider", xlsFile)

                                val xlsIntent = Intent(Intent.ACTION_VIEW)
                                xlsIntent.setDataAndType(xlsUri, "application/vnd.ms-excel")
                                xlsIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                try {
                                    startActivity(xlsIntent)
                                } catch (e: ActivityNotFoundException) {
                                    // Handle the case where there is no app to open the XLS file
                                }
                            }
                        }
                    }
                    registerReceiver(requireContext(), onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS)

                }else if(cypcList[position].files?.contains(".pdf") == true){
                    startActivity(Intent(requireContext(), PdfWebView::class.java).putExtra("url", cypcList[position].files).putExtra("title", cypcList[position].title))
                }else{
                    startActivity(Intent(requireContext(), XlsWebView::class.java).putExtra("url", cypcList[position].files).putExtra("title", cypcList[position].title))

                }
            }

        }

        override fun getItemCount(): Int {
            return cypcList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rcyIcon)!!
            val mName = itemView.findViewById<TextView>(R.id.rcyName)!!
//            val mRoot = itemView.findViewById<LinearLayout>(R.id.rmMain)!!
        }
    }


}