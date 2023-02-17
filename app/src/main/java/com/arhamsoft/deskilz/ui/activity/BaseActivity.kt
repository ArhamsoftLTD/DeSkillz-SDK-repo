package com.arhamsoft.deskilz.ui.activity

import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.arhamsoft.deskilz.AppController
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.ActivityBaseBinding
import com.arhamsoft.deskilz.databinding.DialogDepositBinding
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.utils.CustomSharedPreference
import com.arhamsoft.deskilz.utils.LogoutHandler
import com.arhamsoft.deskilz.utils.LogoutInterface
import com.arhamsoft.deskilz.utils.StaticFields

open class BaseActivity : AppCompatActivity(), LogoutInterface {

    private lateinit var binding: ActivityBaseBinding
    private lateinit var navController: NavController
    lateinit var sharedPreference: CustomSharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(binding.root)
        //hide status bar


        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).apply {
            StaticFields.key = metaData.getString("GameId").toString()
            Log.e("gameKeyStringOnCreate2", "onCreate:${StaticFields.key}..String " )

            if (StaticFields.key.isEmpty() || StaticFields.key == "null") {
                StaticFields.key = metaData.getInt("GameId").toString()
                Log.e("gameKeyIntOnCreate2", "onCreate:${StaticFields.key}..Int " )

            }
            URLConstant.gameActivity = metaData.getString("gameActivity").toString()
        }
        val fragmentHost = supportFragmentManager.findFragmentById(binding.navGraph.id) as NavHostFragment
        navController = fragmentHost.navController

        LogoutHandler.setListener(this)


    }

    override fun logoutListener() {

        runOnUiThread {
            showDialog()
        }
    }


    private fun showDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogDepositBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.para.visibility = View.GONE
        dialogBinding.h1.text ="You have logged-In in another device"
        dialogBinding.price.visibility = View.GONE
        dialogBinding.cancelButton.visibility = View.GONE
        dialogBinding.okButton.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            dialog.dismiss()

            val size= navController.backQueue.size
            Log.e("stackSize", "showDialog:$size " )
            for (item in 0 until navController.backQueue.size) {
                navController.popBackStack()
            }
            navController.navigate(R.id.signInFragment)


        },3000)

        dialog.show()
    }


}