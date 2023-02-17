package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.CardViewPracticeBinding
import com.arhamsoft.deskilz.networking.networkModels.GetTournamentsListData


class RVAdapterPractice(
    var context: Context,
    private var sList: ArrayList<GetTournamentsListData>,
    private var listenerClick: OnItemClick,
    ) : RecyclerView.Adapter<RVAdapterPractice.ViewHolder>() {
    private lateinit var binding: CardViewPracticeBinding
    private var mContext: Context? = null
    var pic: String? = null


    fun addData(slist: ArrayList<GetTournamentsListData>) {
        this.sList = slist
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CardViewPracticeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        mContext = parent.context



        return ViewHolder(binding)
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val listPos = sList[position]


        holder.bind.pracImg.load(listPos.tournamentImage){
            placeholder(R.drawable.onedollarlarge)
            error(R.drawable.onedollarlarge)
        }
        holder.bind.tournamentName.text = listPos.tournamentName

        holder.bind.entryFee.text = " Entry $${listPos.entryFee} "
        holder.bind.players.text = "${listPos.playerCount} players"

        holder.onBind(listPos, listenerClick, position)

    }


    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size


    class ViewHolder(val bind: CardViewPracticeBinding) : RecyclerView.ViewHolder(bind.root) {
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