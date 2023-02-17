package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.CardGetRequestListBinding
import com.arhamsoft.deskilz.networking.networkModels.*


class RVAdapterRequestList(
    var context: Context,
    private var sList: ArrayList<GetFriendRequestListModelData>,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterRequestList.ViewHolder>() {
    private lateinit var binding: CardGetRequestListBinding
    private var mContext: Context? = null
    var pic: String? = null


    fun addData(slist: ArrayList<GetFriendRequestListModelData>) {
        this.sList = slist
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CardGetRequestListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        mContext = parent.context



        return ViewHolder(binding)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


                if (sList.isNullOrEmpty()) {
                    holder.bind.noData.visibility = View.VISIBLE
                    holder.bind.friendReqLayout.visibility = View.GONE
                } else {


                    val listPos = sList[position]
                    holder.bind.noData.visibility = View.GONE
                    holder.bind.friendReqLayout.visibility = View.VISIBLE



                    holder.bind.userNameReq.text = listPos.username ?: "Name"
                    holder.bind.userPicReq.load(listPos.userImage) {
                        placeholder(R.drawable.ic_baseline_person_24)
                        error(R.drawable.ic_baseline_person_24)
                    }



                    holder.onBind(listPos, listenerClick, position)

                }

    }


    override fun getItemCount(): Int =if(sList.size ==0){
        1
    }else  sList.size


    class ViewHolder(val bind: CardGetRequestListBinding) : RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: GetFriendRequestListModelData, listener: OnItemClick, position: Int) {




            bind.acceptReq.setOnClickListener {
                listener.onAccept(model,position)

            }
            bind.cancelReq.setOnClickListener {
                listener.onReject(model,position)

            }

        }
    }


    interface OnItemClick {
        fun onAccept(click:GetFriendRequestListModelData,position: Int)
        fun onReject(click:GetFriendRequestListModelData,position: Int)

    }
}