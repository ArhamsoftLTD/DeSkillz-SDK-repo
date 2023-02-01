package com.arhamsoft.deskilz.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.navigation.findNavController
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.ui.activity.BaseActivity



class Static : Activity() {

    // below our native method, which will be called from Unity3D

    fun startSDK(context: Context){

        context.startActivity(Intent(context, BaseActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun getKeyFromUnity(key:String){
        StaticFields.key = key
        StaticFields.toastClass("gameidfromunity=$key")
        if (StaticFields.key.isEmpty() || StaticFields.key == "null") {
            StaticFields.key = key
        }
    }

    fun getScoreFromUnity(Score:String){
        StaticFields.toastClass("gameidfromunity=$Score")
        val intent = Intent()

        intent.putExtra("matchScore",Score.toLong());
        setResult(RESULT_OK, intent)

    }


}
