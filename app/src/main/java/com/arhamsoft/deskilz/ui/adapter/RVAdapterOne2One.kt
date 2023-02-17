package com.arhamsoft.deskilz.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arhamsoft.deskilz.databinding.CardViewOne2oneBinding
import com.arhamsoft.deskilz.networking.networkModels.GetTournamentsListData


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