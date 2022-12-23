package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import android.util.Base64
import android.view.View
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.CardViewChatBinding
import com.arhamsoft.deskilz.databinding.CardViewHeadtoheadBinding
import com.arhamsoft.deskilz.databinding.CardViewOne2oneBinding
import com.arhamsoft.deskilz.databinding.CardViewPracticeBinding
import com.arhamsoft.deskilz.networking.networkModels.GetChatsModel
import com.arhamsoft.deskilz.networking.networkModels.GetChatsModelData
import com.arhamsoft.deskilz.networking.networkModels.GetTournamentsListData
import java.text.DecimalFormat

import java.util.Base64.getDecoder
import kotlin.collections.ArrayList


class RVAdapterOne2One(
    var context: Context,
    private var sList: ArrayList<GetTournamentsListData>,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterOne2One.ViewHolder>() {
    private lateinit var binding: CardViewOne2oneBinding
    var pic: String? = null


    fun addData(slist: ArrayList<GetTournamentsListData>) {
        this.sList = slist
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //same cardview using for both bracket adapter and head2head adapter
        binding = CardViewOne2oneBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )



        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listPos = sList[position]



        holder.bind.tournamentName.text = listPos.tournamentName
//        holder.bind.img.load(listPos.tournamentImage){
//            placeholder(R.drawable.onedollarlarge)
//            error(R.drawable.onedollarlarge)
//        }
        holder.bind.entryFee.text = " Entry $${listPos.entryFee} "
        holder.bind.player.text = "${listPos.playerCount} players"
        holder.bind.prize.text = "Prize: ${listPos.winningPrize}"




        holder.onBind(listPos, listenerClick, position)

    }


    override fun getItemCount() =  sList.size


    class ViewHolder(val bind: CardViewOne2oneBinding) : RecyclerView.ViewHolder(bind.root) {
        fun onBind(model: GetTournamentsListData, listener: OnItemClick, position: Int) {


            bind.play.setOnClickListener{
                listener.onClick(model,position)
            }


        }
    }


    interface OnItemClick {
        fun onClick(click:GetTournamentsListData,position: Int)

    }
}