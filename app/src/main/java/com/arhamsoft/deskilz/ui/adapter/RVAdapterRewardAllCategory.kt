package com.arhamsoft.deskilz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arhamsoft.deskilz.databinding.AdapterCartSkillRewardAllcategorysBinding
import com.arhamsoft.deskilz.networking.networkModels.GetMarketLoadMoreModelData


class RVAdapterRewardAllCategory(
    var context: Context,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterRewardAllCategory.ViewHolder>() {
    private lateinit var binding: AdapterCartSkillRewardAllcategorysBinding
    private var mContext: Context? = null
    var pic: String? = null
    private var sList: ArrayList<GetMarketLoadMoreModelData> = ArrayList()


    fun addData(slist: ArrayList<GetMarketLoadMoreModelData>) {
        this.sList = slist
        notifyDataSetChanged()
    }

    fun loadMoreDataInSub(slist: GetMarketLoadMoreModelData, position: Int) {
        this.sList[position].productInfo.addAll(slist.productInfo)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = AdapterCartSkillRewardAllcategorysBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        mContext = parent.context
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (sList.isNullOrEmpty()){
//                Toast.makeText(holder.itemView.context,"not available",Toast.LENGTH_SHORT).show()

        }
        else{


        binding.allCategoryRecycler.adapter  =
            RVAdapterRewardAllSubCategory(binding.allCategoryRecycler.context,
                sList[position].productInfo,
                object: RVAdapterRewardAllSubCategory.OnItemClick {
                override fun onClick(click: GetMarketLoadMoreModelData, position: Int) {
                    //listenerClick.onClick(click,position)
                }
            })
        binding.marketName.text = sList[position].marketName

        holder.onBind(sList[position], listenerClick, position)
    }
    }


    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size


    class ViewHolder(val bind: AdapterCartSkillRewardAllcategorysBinding) : RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: GetMarketLoadMoreModelData, listener: OnItemClick, position: Int) {



            bind.seeMore.setOnClickListener{
                listener.onClick(model,position)
            }

        }
    }


    interface OnItemClick {
        fun onClick(click:GetMarketLoadMoreModelData,position: Int)

    }
}