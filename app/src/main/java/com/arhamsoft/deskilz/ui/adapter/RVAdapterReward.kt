package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.AdapterCartSkillRewardsBinding
import com.arhamsoft.deskilz.networking.networkModels.*


class RVAdapterReward(
    var context: Context,
    private var sList: ArrayList<ProductInfoModel>,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterReward.ViewHolder>() {
    private lateinit var binding: AdapterCartSkillRewardsBinding
    private var mContext: Context? = null
    var pic: String? = null


    fun addData(slist: ArrayList<ProductInfoModel>) {
        this.sList = slist
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = AdapterCartSkillRewardsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        mContext = parent.context



        return ViewHolder(binding)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if(sList.isNullOrEmpty()){
            holder.bind.rewardLayout.visibility = View.GONE
        }
        else {

            holder.bind.rewardLayout.visibility = View.VISIBLE
            val listPos = sList[position]


            holder.bind.nameTxt.text = listPos.description
            holder.bind.imgSet.load(listPos.imageProduct) {
                placeholder(R.drawable.ic_baseline_person_24)
            }
            holder.bind.ticketPrice.text = listPos.price



            holder.onBind(sList[position], listenerClick, position)
        }
    }


    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size

    class ViewHolder(val bind: AdapterCartSkillRewardsBinding) : RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: ProductInfoModel, listener: OnItemClick, position: Int) {



            bind.imgSet.setOnClickListener{
                listener.onClick(model,position)
            }

        }
    }


    interface OnItemClick {
        fun onClick(click:ProductInfoModel,position: Int)
    }
}