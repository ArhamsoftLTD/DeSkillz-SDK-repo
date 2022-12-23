package com.arhamsoft.deskilz.ui.adapter

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.AdapterOpponentlistViewBinding
import com.arhamsoft.deskilz.databinding.AdapterTrophiesViewBinding
import com.arhamsoft.deskilz.databinding.RowHomeScreenBinding
import com.arhamsoft.deskilz.networking.networkModels.EarnedTrophiesModel
import com.arhamsoft.deskilz.networking.networkModels.GetMatchesRecord
import com.arhamsoft.deskilz.networking.networkModels.GetMatchesRecordData
import com.arhamsoft.deskilz.networking.networkModels.ListofOpponentModel

class AdapterTrophies(var listener: OnItemClickListenerHandler
): RecyclerView.Adapter<AdapterTrophies.Holder>() {
    private var sList: ArrayList<EarnedTrophiesModel> = ArrayList()



    fun setData(slist: ArrayList<EarnedTrophiesModel>) {
        this.sList = slist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterTrophies.Holder {
        val binding = AdapterTrophiesViewBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: AdapterTrophies.Holder, position: Int) {


        if (sList.isNullOrEmpty()) {
            holder.binding.noData.visibility = View.VISIBLE
            holder.binding.trophyLayout.visibility = View.GONE
        } else {

            val listPos = sList[position]

            holder.binding.noData.visibility = View.GONE
            holder.binding.trophyLayout.visibility = View.VISIBLE
//
//
            if (listPos.name.contains("Newbie")){
                holder.binding.trophy.load(R.drawable.newbie){
                    placeholder(R.drawable.trophy_lar)
                    error(R.drawable.trophy_lar)
                }

            }
            else if (listPos.name.contains("Loser")){
                holder.binding.trophy.load(R.drawable.loser){
                    placeholder(R.drawable.trophy_lar)
                    error(R.drawable.trophy_lar)
                }
            }
            else if (listPos.name.contains("Loser")){
                holder.binding.trophy.load(R.drawable.loser){
                    placeholder(R.drawable.trophy_lar)
                    error(R.drawable.trophy_lar)
                }
            }
            else if (listPos.name.contains("Eliminator")){
                holder.binding.trophy.load(R.drawable.eliminator){
                    placeholder(R.drawable.trophy_lar)
                    error(R.drawable.trophy_lar)
                }
            }
            else if (listPos.name.contains("Brightest")){
                holder.binding.trophy.load(R.drawable.brightest){
                    placeholder(R.drawable.trophy_lar)
                    error(R.drawable.trophy_lar)
                }
            }
            else{
                holder.binding.trophy.load(123){
                    placeholder(R.drawable.trophy_lar)
                    error(R.drawable.trophy_lar)
                }
            }


            holder.binding.trophyName.text = listPos.name


//            holder.binding.oppoenetImg.load(listPos.opponentImage){
//                placeholder(R.drawable.ic_baseline_person_24)
//                error(R.drawable.ic_baseline_person_24)
//            }
//            holder.binding.oppoenetName.text = if (listPos.opponentName.isNullOrEmpty()){
//                "UnknownPlayer"
//            }
//            else{
//                listPos.opponentName
//            }
//            holder.binding.oppoenetFlag.load(listPos.)

            holder.onBind(sList[position], listener, position)

        }
    }






    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size


    class Holder(val binding: AdapterTrophiesViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: EarnedTrophiesModel, listener:OnItemClickListenerHandler, position: Int) {

            binding.trophy.setOnClickListener {
                listener.onItemClicked(model,position)
            }
        }
    }

    interface OnItemClickListenerHandler {
        fun onItemClicked(click: EarnedTrophiesModel, position: Int)
    }

}