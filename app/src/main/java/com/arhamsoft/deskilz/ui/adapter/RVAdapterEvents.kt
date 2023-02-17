package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arhamsoft.deskilz.databinding.CardViewEventsBinding
import com.arhamsoft.deskilz.networking.networkModels.EventsModelData


class RVAdapterEvents(
    var context: Context,
    private var sList: ArrayList<EventsModelData>,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterEvents.ViewHolder>() {
    private lateinit var binding: CardViewEventsBinding
    private var mContext: Context? = null
    var pic: String? = null


    fun addData(slist: ArrayList<EventsModelData>) {
        this.sList = slist
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CardViewEventsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        mContext = parent.context



//        this.mContext = context
        return ViewHolder(binding)
    }

//    override fun getItemViewType(position: Int): Int {
//        return position
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (sList.isNullOrEmpty()){
            holder.bind.noData.visibility = View.VISIBLE
            holder.bind.layout.visibility = View.GONE
        }else {

            holder.bind.noData.visibility = View.GONE
            holder.bind.layout.visibility = View.VISIBLE

            val listPos = sList[position]


//            holder.bind.eventImg.load(listPos.eventImage) {
//                placeholder(R.drawable.coin_medium)
//            }
//            holder.bind.eventImg.load(listPos.eve){
//                placeholder(R.drawable.onedollarlarge)
//                error(R.drawable.onedollarlarge)
//            }
            holder.bind.eventName.text = listPos.eventName
            holder.bind.desc.text = listPos.eventDescription
            holder.bind.entryFee.text = " Entry $${listPos.entryFee} "
            holder.bind.player.text = "${listPos.playerCount} players"
//        holder.bind.prize.text = listPos.prizes[0].rewardPrice


            holder.onBind(listPos, listenerClick, position)
        }

    }


    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size


    class ViewHolder(val bind: CardViewEventsBinding) : RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: EventsModelData, listener: OnItemClick, position: Int) {


            bind.play.setOnClickListener{
                listener.onClick(model,position)
            }


//            bind.liked.setOnClickListener {
//                listener.onLiked(model, position)
//
//                    bind.unlike.visibility = View.VISIBLE
//            }
//
//            bind.unlike.setOnClickListener {
//                listener.onUnLiked(model,position)
//                bind.unlike.visibility = View.GONE
//
//            }
//
//            bind.share.setOnClickListener {
//
//                listener.onShare(model, position)
//            }
//
//            bind.reply.setOnClickListener {
//                listener.onReply(model, position)
//            }
        }
    }


    interface OnItemClick {
        fun onClick(click:EventsModelData,position: Int)
//        fun onLiked(comment: GetCommentDataList, position: Int)
//        fun onShare(comment: GetCommentDataList, position: Int)
//        fun onReply(comment: GetCommentDataList, position: Int)
//        fun onUnLiked(comment: GetCommentDataList,position: Int)

    }
}