package com.arhamsoft.deskilz.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.RowHomeScreenBinding
import com.arhamsoft.deskilz.networking.networkModels.GetMatchesRecord
import com.arhamsoft.deskilz.networking.networkModels.GetMatchesRecordData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterHomeScreen(var listener: OnItemClickListenerHandler
): RecyclerView.Adapter<AdapterHomeScreen.Holder>() {
    private var sList: ArrayList<GetMatchesRecordData> = ArrayList()



    fun setData(slist: ArrayList<GetMatchesRecordData>) {
        this.sList = slist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHomeScreen.Holder {
        val binding = RowHomeScreenBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return Holder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: AdapterHomeScreen.Holder, position: Int) {


        if (sList.isNullOrEmpty()){
            holder.binding.noData.visibility = View.VISIBLE
            holder.binding.home.visibility = View.GONE
        }else{

            val listPos = sList[position]

            holder.binding.noData.visibility = View.GONE
            holder.binding.home.visibility = View.VISIBLE

            holder.binding.matchImg.load(123){
                placeholder(R.mipmap.ic_launcher)
                error(R.mipmap.ic_launcher)
            }

            holder.binding.tournamentName.text = listPos.matchTitle

            if(listPos.isWin) {
                holder.binding.userStatus.text = "You Win"
                holder.binding.userStatus.setTextColor(Color.GREEN)
            }
            else{
                holder.binding.userStatus.text = "You Lose"
                holder.binding.userStatus.setTextColor(Color.RED)
            }

            if (listPos.listofOpponent.size > 1){
                holder.binding.oppoenetName.text = "In a tournament"
            }
            else if (listPos.listofOpponent.size == 1) {
                holder.binding.oppoenetName.text = listPos.listofOpponent[0].usernameOtheruser
            }



            holder.binding.entryFee.text = listPos.entryFee.toString()

            val time = listPos.time.toDate()?.formatTo("dd MM yyyy HH:mm:ss")



            holder.binding.time.text = "Time: ${time}"

            holder.onBind(sList[position], listener,position)

        }



    }

    fun String.toDate(dateFormat: String = "dd/MM/yyyy HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date? {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size


    class Holder(val binding: RowHomeScreenBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: GetMatchesRecordData, listener:OnItemClickListenerHandler , position: Int) {

            binding.home.setOnClickListener {
                listener.onItemClicked(model,position)
            }
        }
    }

    interface OnItemClickListenerHandler {
        fun onItemClicked(click:GetMatchesRecordData,position: Int)
    }

}