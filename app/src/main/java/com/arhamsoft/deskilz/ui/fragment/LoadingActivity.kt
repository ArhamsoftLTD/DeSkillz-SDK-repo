package com.arhamsoft.deskilz.ui.fragment

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.FragmentLoadingScreenBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.ForgotModel
import com.arhamsoft.deskilz.networking.networkModels.ThemeModel
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.utils.CustomSharedPreference
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.google.android.gms.location.LocationRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val REQUEST_CHECK_SETTINGS = 10001


class LoadingActivity : Fragment() {

    private lateinit var binding: FragmentLoadingScreenBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreference: CustomSharedPreference
    lateinit var loading:LoadingDialog

    var apiHit:Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoadingScreenBinding.inflate(layoutInflater)
        navController = findNavController()
        coreLoop()
        loading = LoadingDialog(requireContext() as Activity)


        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                if (p0?.isPermanentlyDenied == true){
                    Toast.makeText(
                        requireContext(),
                        "You have to enable permissions from App settings in order for app to work properly",
                        Toast.LENGTH_SHORT
                    ).show()
                    showSettingsDialog()
                }


            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()


            }
        }).check()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = CustomSharedPreference(requireContext())

        getThemeApi()

        binding.lottieLoader.addAnimatorListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                if (apiHit){
                    if (sharedPreference.isLogin("LOGIN")) {
                        if (activity?.intent?.extras != null) {
                            val bundle2 = bundleOf()

                            val map = HashMap<String, String>()

                            for (key in activity?.intent?.extras!!.keySet()) {
                                activity?.intent?.extras!!.getString(key)?.let { map.put(key, it) }
                            }

                            if (map["notificationType"]?.toInt() == 4){
                                navController.navigate(R.id.dashboardActivity)

                            }
                            else if (map["notificationType"]?.toInt() == 6){
                                navController.navigate(R.id.dashboardActivity)

                            }
                            else if (map["notificationType"]?.toInt() == 5){
                                navController.navigate(R.id.action_splashFragment_to_chatHeadsFragment)
                            }
                            else if (map["notificationType"]?.toInt() == 3){
                                sharedPreference.saveLogin("LOGIN", false)
                                navController.navigate(R.id.signInFragment)
                            }
                            else if (map["notificationType"]?.toInt() == 0){
                                bundle2.putInt("GLOBAL_CHAT", 1)

                                navController.navigate(R.id.action_splashFragment_to_chatFragment,bundle2)

                            }
                            else if (map["notificationType"]?.toInt() == 1){
                                bundle2.putInt("GLOBAL_CHAT", 2)
                                bundle2.putSerializable("FRIEND_ID", map["fromId"])
                                navController.navigate(R.id.action_splashFragment_to_chatFragment,bundle2)

                            }
                            else{
                                navController.navigate(R.id.action_loadingActivity_to_dashboardActivity)


                            }
                        }else {


                            navController.navigate(R.id.action_loadingActivity_to_dashboardActivity)

                        }



                    }
                    else{
                        navController.navigate(R.id.action_loadingActivity_to_signInFragment)
                    }
                }

            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
        Handler(Looper.getMainLooper()).postDelayed({
            binding.image.visibility = View.VISIBLE
        }, 2000)

        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.move_downward)
        binding.image.animation = anim

        binding.lottieLoader.visibility = View.VISIBLE
        binding.lottieLoader.playAnimation()
    }

    private fun showSettingsDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                openSettings()
            })

        builder.setCancelable(false)
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package",requireContext().packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun getThemeApi(){
        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.getTheme(
                object : NetworkListener<ThemeModel> {
                    override fun successFul(t: ThemeModel) {

                        activity?.runOnUiThread {
                            apiHit = true

                            if (t.status == 1) {

                                URLConstant.themeModel = t
//                            URLConstant.check = true


                            } else {
//                                StaticFields.toastClass("${t.message}")
                            }

                            if (!binding.lottieLoader.isAnimating) {

                                if (sharedPreference.isLogin("LOGIN")) {
                                    navController.navigate(R.id.action_loadingActivity_to_dashboardActivity)
                                } else {
                                    navController.navigate(R.id.action_loadingActivity_to_signInFragment)
                                }
                            }
                        }
                    }
                    override fun failure() {
                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail get theme")
                        }
                    }
                }
            )
        }

    }



    private fun coreLoop(){
        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.coreLoop(
                StaticFields.key,
                object : NetworkListener<ForgotModel> {
                    override fun successFul(t: ForgotModel) {
                        activity?.runOnUiThread {
                            loading.isDismiss()

                            if (t.status == 1) {


                                StaticFields.toastClass(t.message)

                            }
                            else{
                                StaticFields.toastClass(t.message)
                            }
                        }



                    }


                    override fun failure() {

                        activity?.runOnUiThread {
                            loading.isDismiss()

                            StaticFields.toastClass("Api syncing fail coreloop")
                        }
                    }
                }
            )
        }

    }



}