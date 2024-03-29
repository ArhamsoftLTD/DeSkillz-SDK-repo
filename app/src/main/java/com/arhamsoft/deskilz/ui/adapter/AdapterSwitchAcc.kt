package com.arhamsoft.deskilz.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alphaCareInc.app.room.User
import com.arhamsoft.deskilz.databinding.RowAccountsBinding

import com.arhamsoft.deskilz.networking.retrofit.URLConstant

class AdapterSwitchAcc(var listener: OnItemClickListenerHandler
): RecyclerView.Adapter<AdapterSwitchAcc.Holder>() {
    private var sList: ArrayList<User> = ArrayList()



    fun setData(slist: ArrayList<User>) {
        this.sList = slist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSwitchAcc.Holder {
        val binding = RowAccountsBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: AdapterSwitchAcc.Holder, position: Int) {


        if (sList.isNullOrEmpty()){
            holder.binding.noData.visibility = View.VISIBLE
            holder.binding.layout.visibility = View.GONE
        }else{

            val listPos = sList[position]

            holder.binding.noData.visibility = View.GONE
            holder.binding.layout.visibility = View.VISIBLE


            holder.binding.status.text = if(listPos.userId == URLConstant.u_id){
                 " "
            }
            else
            {
                " Signed out "
            }


            holder.binding.userEmail.text = if (listPos.userEmail.isNullOrEmpty()){
                "UnknownPlayer"
            }
            else{
                listPos.userEmail
            }

            holder.binding.userName.text = if (listPos.userName.isNullOrEmpty()){
                "UnknownPlayer"
            }
            else{
                listPos.userName
            }



            holder.onBind(sList[position], listener,position)

        }



    }

    override fun getItemCount(): Int = if(sList.size ==0){
        1
    }else  sList.size


    class Holder(val binding: RowAccountsBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: User, listener:OnItemClickListenerHandler , position: Int) {

            binding.user.setOnClickListener {
                listener.onItemClicked(model,position)
            }
        }
    }

    interface OnItemClickListenerHandler {
        fun onItemClicked(click:User,position: Int)
    }

}