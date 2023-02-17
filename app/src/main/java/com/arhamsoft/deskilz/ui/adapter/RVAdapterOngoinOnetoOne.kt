package com.arhamsoft.deskilz.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arhamsoft.deskilz.databinding.RowPendingRequestBinding
import com.arhamsoft.deskilz.networking.networkModels.PlayerWaitingModelData

class RVAdapterOngoinOnetoOne(var listener: OnItemClickListenerHandler
): RecyclerView.Adapter<RVAdapterOngoinOnetoOne.Holder>() {
    private var sList: ArrayList<PlayerWaitingModelData> = ArrayList()



    fun setData(slist: ArrayList<PlayerWaitingModelData>) {
        this.sList = slist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVAdapterOngoinOnetoOne.Holder {
        val binding = RowPendingRequestBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: RVAdapterOngoinOnetoOne.Holder, position: Int) {


        if (sList.isNullOrEmpty()){
            holder.binding.noData.visibility = View.VISIBLE
            holder.binding.layout.visibility = View.GONE
        }else{

            val listPos = sList[position]

            holder.binding.noData.visibility = View.GONE
            holder.binding.layout.visibility = View.VISIBLE
            holder.binding.tournName.text = listPos.tournamentName
            holder.binding.score.text = "Your Score: ${listPos.previousScore}"
            holder.binding.player.text = "Player: ${listPos.playerCount}"

            if (listPos.tournamentId != null){
                holder.binding.remainigtime.text = "Ends in: ${listPos.remainingTime}"
                holder.binding.userStatus.text = "Tournament is in Progress"
            }
            else{
                holder.binding.userStatus.text = "Match is in Progress"
            }

            holder.binding.entryFee.text = if(listPos.tournamentId == null){
                "Fee: Free"
            }else{
                "Fee: ${listPos.entryFee}"
            }

            holder.onBind(sList[position], listener,position)

        }



    }

    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size


    class Holder(val binding: RowPendingRequestBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: Any, listener:OnItemClickListenerHandler , position: Int) {

//            binding.play.setOnClickListener {
//                listener.onItemClicked(model,position)
//            }
        }
    }

    interface OnItemClickListenerHandler {
        fun onItemClicked(click:Any,position: Int)
    }

}