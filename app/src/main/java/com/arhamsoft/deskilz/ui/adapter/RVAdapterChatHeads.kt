package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import coil.load
import android.view.View
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.CardViewChatheadsBinding
import com.arhamsoft.deskilz.networking.networkModels.GetChatsHeadModelData
import kotlin.collections.ArrayList


class RVAdapterChatHeads(
    var context: Context,
    private var sList: ArrayList<GetChatsHeadModelData>,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterChatHeads.ViewHolder>() {
    private lateinit var binding: CardViewChatheadsBinding
    private var mContext: Context? = null
    var pic: String? = null


    fun addData(slist: ArrayList<GetChatsHeadModelData>) {
        this.sList = slist
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CardViewChatheadsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        mContext = parent.context



        return ViewHolder(binding)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (sList.isNullOrEmpty()){
            holder.bind.noData.visibility = View.VISIBLE
            holder.bind.chatHead.visibility = View.GONE
        }
        else {

            val listPos = sList[position]

            holder.bind.noData.visibility = View.GONE
            holder.bind.chatHead.visibility = View.VISIBLE
            holder.bind.userName.text = listPos.username
            holder.bind.userPic.load(listPos.userImage) {
                placeholder(R.drawable.ic_baseline_person_24)
                error(R.drawable.ic_baseline_person_24)
            }

            holder.onBind(listPos, listenerClick, position)
        }
    }


    override fun getItemCount(): Int =if(sList.size ==0){
        1
    }else  sList.size


    class ViewHolder(val bind: CardViewChatheadsBinding) : RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: GetChatsHeadModelData, listener: OnItemClick, position: Int) {



            bind.chatHead.setOnClickListener{
                listener.onUserClick(model,position)
            }

        }
    }


    interface OnItemClick {
        fun onUserClick(click:GetChatsHeadModelData,position: Int)

    }
}