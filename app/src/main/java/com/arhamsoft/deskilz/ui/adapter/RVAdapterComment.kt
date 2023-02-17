package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import coil.load

import android.view.View
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.CardViewChatBinding

import com.arhamsoft.deskilz.networking.networkModels.ReceivedDataFromSocket
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import java.util.*
import kotlin.collections.ArrayList


class RVAdapterComment(
    var context: Context,
    private var sList: ArrayList<ReceivedDataFromSocket>,
    private var date:String,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterComment.ViewHolder>() {
    private lateinit var binding: CardViewChatBinding
    var pic: String? = null


    fun addData(slist: ArrayList<ReceivedDataFromSocket>) {
        this.sList = slist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CardViewChatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )



         return ViewHolder(binding)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val listPos = sList[position]


        if (listPos.userId==URLConstant.u_id){
            //my msg
            holder.bind.userMsgLayout.visibility = View.VISIBLE
            holder.bind.otherMsgLayout.visibility = View.GONE

            holder.bind.myMessage.text = listPos.message
            holder.bind.myMessageTime.text = if (listPos.createdAtDate.startsWith(date)){
                 listPos.createdAtTime

            }
            else{
                 "${listPos.createdAtDate}, ${listPos.createdAtTime}"
            }




        }
        else{
            //other msgs
            holder.bind.userMsgLayout.visibility = View.GONE
            holder.bind.otherMsgLayout.visibility = View.VISIBLE

            binding.userPic.load(listPos.userImage){
                placeholder(R.drawable.ic_baseline_person_24)
                error(R.drawable.ic_baseline_person_24)
            }
            holder.bind.otherMsgs.text = listPos.message
            holder.bind.UserName.text = listPos.senderName
            holder.bind.myMessageTime.text = if (listPos.createdAtDate.startsWith(date)){
                 listPos.createdAtTime
            }
            else{
                "${listPos.createdAtDate}, ${listPos.createdAtTime}"
            }
        }


        holder.onBind(listPos, listenerClick, position)

    }


    override fun getItemCount(): Int =  sList.size


    class ViewHolder(val bind: CardViewChatBinding) : RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: ReceivedDataFromSocket, listener: OnItemClick, position: Int) {


            bind.userPic.setOnClickListener{
                listener.onOthersClick(model,position)
            }

            bind.userMsgLayout.setOnClickListener{
                listener.onUserClick(model,position)
            }
        }
    }


    interface OnItemClick {
        fun onOthersClick(click:ReceivedDataFromSocket,position: Int)
        fun onUserClick(click:ReceivedDataFromSocket,position: Int)


    }
}