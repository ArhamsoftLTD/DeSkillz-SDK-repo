package com.arhamsoft.deskilz.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.arhamsoft.deskilz.ui.activity.BaseActivity


class Static : Application() {

    // below our native method, which will be called from Unity3D

    fun startSDK(context: Context){

        context.startActivity(Intent(context, BaseActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun getKeyFromUnity(key:String){
        StaticFields.toastClass("gameidfromunity=$key")
        StaticFields.key = key
        if (StaticFields.key.isEmpty() || StaticFields.key == "null") {
            StaticFields.key = key
        }
    }
}
