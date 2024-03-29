package com.arhamsoft.deskilz.ui.fragment.navBarFragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.FragmentMatchScoreBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.*
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class FragmentMatchScore : Fragment() {

    lateinit var binding:FragmentMatchScoreBinding
    lateinit var loading:LoadingDialog
    var u_id:String? =null
    lateinit var time: CountDownTimer
//     var mUnityPlayer // don't change the name of this variable; referenced from native code
//            : UnityPlayer? = null
    var progressionList:ArrayList<ProgressPost> = ArrayList()
    var gameCustomDataList: ArrayList<CustomPlayerModelData> = ArrayList()
    private lateinit var prog:HashMap<*,*>
    val someActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            URLConstant.score= data?.extras?.get("matchScore") as Long

            if (!(StaticFields.isNetworkConnected(requireContext()))){
                StaticFields.toastClass("Check your network connection")
            }
            else{

            if (URLConstant.isPractice){
                binding.btnSubmit.setOnClickListener {
                    navigateTO()
                }
            }
            else{
                prog =  data.extras?.get("progression") as HashMap<*,*>
                Log.d("dataResult", data.toString())
                countdownTimer()
                getGameCustomData()
                loading.startLoading()
                updateScore()

            }

            }




        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMatchScoreBinding.inflate(LayoutInflater.from(context))
        loading = LoadingDialog(requireContext() as Activity)


//
         //// for native
        val myClass = Class.forName(URLConstant.gameActivity)
        val intent = Intent(requireContext(), myClass)
        someActivity.launch(intent)

        return binding.root
    }



    private fun getGameCustomData(){
        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.getGameCustomData(
                object : NetworkListener<CustomPlayerModel> {
                    override fun successFul(t: CustomPlayerModel) {
                        activity?.runOnUiThread {
//                            loading.isDismiss()

                            if (t.status == 1) {


                                StaticFields.toastClass(t.message)

                                gameCustomDataList.addAll(t.data)


                                for (item in gameCustomDataList){
                                    for (i in prog.keys){
                                        if (item.keyName == i){

                                            progressionList.add(ProgressPost(item.keyName,prog[i]!!))

                                        }
                                    }


                                }
                                progressionData()

                            }
                            else{
                                StaticFields.toastClass(t.message)
                            }
                        }



                    }


                    override fun failure() {

                        activity?.runOnUiThread {
//                            loading.isDismiss()

                            StaticFields.toastClass("Api syncing fail getCustomgameDAta")
                        }
                    }
                }
            )
        }

    }

    private fun countdownTimer() {
        time = object : CountDownTimer(5000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {

                //Convert milliseconds into hour,minute and seconds
                //Convert milliseconds into hour,minute and seconds
                val hms = java.lang.String.format(
                    "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),

                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
                binding.btnText.text = hms //set text


//                binding.countdown.text = ""+millisUntilFinished / 1000
            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                activity?.runOnUiThread {
                navigateTO()}

            }
        }
    }

    private fun navigateTO() {
        activity?.runOnUiThread {

            findNavController().navigate(R.id.action_fragmentMatchScore_to_dashboardActivity)
        }
    }



    private fun updateScore() {

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.updateScore(
                URLConstant.score,
                URLConstant.u_id!!,
                URLConstant.matchId!!,
                object : NetworkListener<UpdateMatchScoreModel> {
                    override fun successFul(t: UpdateMatchScoreModel) {
                        loading.isDismiss()
                        activity?.runOnUiThread {

                        if (t.status ==1) {
                            time.start()
                            StaticFields.toastClass(t.message)

                            binding.btnSubmit.setOnClickListener {
                                time.cancel()

                                    navigateTO()
                            }
                        }
                        else{
                            StaticFields.toastClass(t.message)
                        }

                        }

                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("api syncing failed update score")
                        }
                    }
                }
            )}

    }


    private fun progressionData() {

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.getProgressionData(
                progressionList,
                object : NetworkListener<ProgressionModel> {
                    override fun successFul(t: ProgressionModel) {
//                        loading.isDismiss()
                        activity?.runOnUiThread {
                            if (t.status ==1){
                                StaticFields.toastClass("progression hit")
                                binding.icTopImg.load(t.data.badgeImage)
                                binding.icSmallImg.load(t.data.entryPointImage)
                                binding.icBackImg.load(t.data.backgroundImage)
                                binding.entryPointName.text = t.data.entryPointName
                                binding.text1.text = t.data.title
                                binding.text2.text = t.data.subtitle1
                                binding.text3.text = t.data.subtitle2
                            }
                            else{
                                StaticFields.toastClass(t.message)

                            }
                            Log.e("response", " ${t.data}" )
                        }
                    }

                    override fun failure() {
//                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("api syncing failed progression")
                        }
                    }
                }
            )}

    }


    override fun onResume() {
        super.onResume()
        binding.score.text = URLConstant.score.toString()

    }


}