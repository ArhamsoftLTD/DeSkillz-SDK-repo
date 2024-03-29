package com.arhamsoft.deskilz.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.arhamsoft.deskilz.AppController
import com.arhamsoft.deskilz.networking.networkModels.LoginModel
import com.arhamsoft.deskilz.ui.activity.BaseActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

object StaticFields {
    var sharedPreference: CustomSharedPreference =
        CustomSharedPreference(AppController.getContext())


    var userModel: LoginModel? = null
    var key : String = "0"


    // native method which will be called from native game android
    fun startSDK(context: Context){

        context.startActivity(Intent(context,BaseActivity::class.java))
    }
    fun toastClass(text:String,context: Context = AppController.getContext()){
        val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
//        toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0)
        toast.show()
    }



    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }


    fun fcmToken() {
        //firebase token

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("Fetching", task.exception.toString())
                return@OnCompleteListener
            }
            val token = task.result
            sharedPreference.saveValue("TOKEN", token)


        })

    }



}