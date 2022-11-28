package com.arhamsoft.deskilz.ui.fragment

import android.Manifest
import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.arhamsoft.deskilz.utils.CustomSharedPreference
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.FragmentLoadingScreenBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.ForgotModel
import com.arhamsoft.deskilz.networking.networkModels.ThemeModel
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
private const val REQUEST_CHECK_SETTINGS = 10001


class LoadingActivity : Fragment() {

    private lateinit var binding: FragmentLoadingScreenBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreference: CustomSharedPreference
    lateinit var loading:LoadingDialog
    private var locationRequest: LocationRequest? = null

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
        if(isLocationEnabled(requireContext())) {
            Dexter.withContext(requireContext())
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {

                        if (p0?.areAllPermissionsGranted() == true) {

                            getLatitudeLongitude()

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "You have to enable permissions from App settings",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }
                })
                .check()

        }

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
                        navController.navigate(R.id.action_loadingActivity_to_dashboardActivity)
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


    fun getLatitudeLongitude(){

        val locationManager: LocationManager = requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        val locationListener: LocationListener = object : LocationListener {

            override fun onLocationChanged(location: Location) {
//
//                URLConstant.lat = location.latitude
//                URLConstant.long = location.longitude
//
                URLConstant.lat = 27.0
                URLConstant.long = 78.9

//                URLConstant.getEventsFinalUrl = URLConstant.getEvents + URLConstant.long + URLConstant.lat


//                Log.e("location", "Latitute: $latitude ; Longitute: $longitude ; city: $cityName ; country: $country ; address:$address")
            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0.0f, locationListener)
        } catch (ex:SecurityException) {
            Toast.makeText(requireContext(), "error in capturing lat/long", Toast.LENGTH_SHORT).show()
        }
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
                                StaticFields.toastClass("abcd")
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

    private fun isLocationEnabled(context: Context): Boolean {
        var locationMode = 0
        val locationProviders: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            turnOnLocation()

            try {
                locationMode =
                    Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                turnOnLocation()

            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
//                turnOnLocation()

            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } else {
//            turnOnLocation()
            locationProviders =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_MODE)
            !TextUtils.isEmpty(locationProviders)
        }
    }


    private fun turnOnLocation()  {

        locationRequest = LocationRequest.create()
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.setInterval(5000)
        locationRequest!!.setFastestInterval(2000)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        builder.setAlwaysShow(true)
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(
            requireContext()
        )
            .checkLocationSettings(builder.build())
        result.addOnCompleteListener(OnCompleteListener<LocationSettingsResponse?> { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                Toast.makeText(requireContext(), "GPS Turned on", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            requireActivity(),
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (ex: IntentSender.SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                }
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    Toast.makeText(requireContext(), "GPS is Turned on", Toast.LENGTH_SHORT).show()
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    turnOnLocation()
                }
            }
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