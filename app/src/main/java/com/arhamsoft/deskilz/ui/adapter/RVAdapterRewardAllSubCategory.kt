package com.arhamsoft.deskilz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.AdapterCartSkillRewardSubCatrgoryBinding
import com.arhamsoft.deskilz.networking.networkModels.GetMarketLoadMoreModelData
import com.arhamsoft.deskilz.networking.networkModels.ProductInfoModel


class RVAdapterRewardAllSubCategory(
    var context: Context,
    private var sList: ArrayList<ProductInfoModel>,
    private var listenerClick: OnItemClick,
) : RecyclerView.Adapter<RVAdapterRewardAllSubCategory.ViewHolder>() {
    private lateinit var binding: AdapterCartSkillRewardSubCatrgoryBinding
    private var mContext: Context? = null
    var pic: String? = null
    fun addData(slist: ArrayList<ProductInfoModel>) {
        this.sList = slist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = AdapterCartSkillRewardSubCatrgoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        mContext = parent.context
        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productInfo = sList[position]
        holder.bind.nameTxt.text = productInfo.description
        holder.bind.imgSet.load(productInfo.imageProduct) {
            placeholder(R.drawable.ic_baseline_person_24)
        }
        holder.bind.ticketPrice.text = productInfo.price
        holder.onBind(productInfo, listenerClick, position)
    }


    override fun getItemCount(): Int = if (sList.size == 0) {
        1
    } else sList.size


    class ViewHolder(val bind: AdapterCartSkillRewardSubCatrgoryBinding) :
        RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: ProductInfoModel, listener: OnItemClick, position: Int) {

        }
    }


    interface OnItemClick {
        fun onClick(click: GetMarketLoadMoreModelData, position: Int)


    }
}