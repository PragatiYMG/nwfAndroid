package com.app.nwf.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity.RECEIVER_EXPORTED
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentDashboardBinding
import com.app.nwf.model.LangData
import com.app.nwf.utils.BaseUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson

class Dashboard : Fragment() {

    private lateinit var langData: LangData
    private lateinit var myReceiver: BroadcastReceiver
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {

        val gson = Gson()
        val mString = BaseUtils.getLangData(requireContext())
        langData = gson.fromJson(mString, LangData::class.java)

        myReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val kString = BaseUtils.getLangData(requireContext())
                langData = gson.fromJson(kString, LangData::class.java)
                binding.fdRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.fdRecycle.adapter = MenuAdapter()
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

        binding.fdRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.fdRecycle.adapter = MenuAdapter()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(myReceiver)
    }

    inner class MenuAdapter : RecyclerView.Adapter<MenuAdapter.DataHolder>() {

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
            return DataHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_menu, parent, false))
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {



            holder.itemView.setOnClickListener {
                var type = position+1
                when(position){
                    5 ->{
                        type = 8
                    }
                    6 ->{
                        type = 7
                    }
                    7 ->{
                        type = 13
                    }
                    8 ->{
                        type = 6
                    }

                }
                val intent = Intent("change_fragment")
                intent.putExtra("type", type.toString())
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }


//            binding.snImpContactTxt.text = langData.importantContact
//            binding.snConsultationsTxt.text = langData.consultation
//            binding.snHandbookTxt.text = langData.childrenHandbook
//            binding.snResourceLibTxt.text = langData.interestingStuff
//            binding.snTrainingTxt.text = langData.watchLearn
//            binding.snCypcTxt.text = langData.cypc
//            binding.snPublicationsTxt.text = langData.publication
//            binding.snContactUsTxt.text = langData.contactUs

            when(position){
                0 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.sn_import_contact)
                        .into(holder.mIcon)
                    holder.mName.text = langData.importantPhone
                }
                1 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.sn_cunsult)
                        .into(holder.mIcon)
                    holder.mName.text = langData.consultation
                }
                2 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.sn_handbook)
                        .into(holder.mIcon)
                    holder.mName.text = langData.childrenHandbook
                }
                3 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.an_resource)
                        .into(holder.mIcon)
                    holder.mName.text = langData.interestingStuff
                }
                4 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.sn_videos)
                        .into(holder.mIcon)
                    holder.mName.text = langData.watchLearn
                }
                5 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.sn_publication)
                        .into(holder.mIcon)
                    holder.mName.text = langData.publication
                }
                6 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.sn_cypc)
                        .into(holder.mIcon)
                    holder.mName.text = langData.cypcFull
                  //  holder.itemView.context.resources.getString(R.string.cypc)
                }
                7 ->{
                    Glide
                        .with(holder.itemView.context)
                        .load(R.drawable.ic_language)
                        .into(holder.mIcon)
                    holder.mName.text = langData.googleTranslator
                }
                8 ->{
                Glide
                    .with(holder.itemView.context)
                    .load(R.drawable.sn_contact_us)
                    .into(holder.mIcon)
                holder.mName.text = langData.contactUs
            }

            }

        }

        override fun getItemCount(): Int {
            return 9
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mIcon = itemView.findViewById<ImageView>(R.id.rmIcon)!!
            val mName = itemView.findViewById<TextView>(R.id.rmName)!!
            val mRoot = itemView.findViewById<CardView>(R.id.rmMain)!!
        }
    }
}