package com.arhamsoft.deskilz.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.DialogDepositBinding
import com.arhamsoft.deskilz.databinding.FragmentFindCompetitiveBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.*
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.services.SocketHandler
import com.arhamsoft.deskilz.ui.adapter.AdapterOpponents
import com.arhamsoft.deskilz.utils.CustomSharedPreference
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class FindCompetitiveFragment : Fragment() {

    private lateinit var binding: FragmentFindCompetitiveBinding
    lateinit var loading: LoadingDialog
    var u_id: String? = null
    var opponentList: ArrayList<ListofOpponentModel> = ArrayList()
    private lateinit var rvAdapter: AdapterOpponents
    lateinit var time: CountDownTimer
    lateinit var sharedPreference: CustomSharedPreference
    private var mSocket: Socket? = null
    var backtoHomeORExitAPP: Boolean = false
    lateinit var gson: JsonObject
    var click: GetTournamentsListData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindCompetitiveBinding.inflate(LayoutInflater.from(context))
        loading = LoadingDialog(requireContext() as Activity)
        sharedPreference = CustomSharedPreference(requireContext())
        backtoHomeORExitAPP = false

        binding.cancelMatch.isClickable = false
        binding.cancelMatch.isEnabled = false
        binding.cancelMatch.alpha = 0.5f


        binding.exitMatch.isClickable = false
        binding.exitMatch.isEnabled = false
        binding.exitMatch.alpha = 0.5f


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StaticFields.toastClass("Please wait, while we are matching opponent for you.")

        countdownTimer()
        userNameandImg()

        val bundle = arguments
        if (bundle != null) {

            click = Gson().fromJson(
                bundle.getString("GET_MATCHES_OBJ"),
                GetTournamentsListData::class.java
            )

            if (click != null) {
                binding.entryFee.text = click?.entryFee.toString()!!
                binding.pCount.text = "${click?.playerCount!!} players"
            }
        }

        practiceModeCheck()

        if (!(StaticFields.isNetworkConnected(requireContext()))) {
            StaticFields.toastClass("Check your network connection")
        } else {
            connectSocket()
        }

        binding.recycleListOpponent.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        rvAdapter = AdapterOpponents(object : AdapterOpponents.OnItemClickListenerHandler {
            override fun onItemClicked(click: ListofOpponentModel, position: Int) {
            }
        })


        binding.recycleListOpponent.adapter = rvAdapter

        binding.exitMatch.setOnClickListener {

            time.cancel()
            leavePlayer()
            mSocket?.disconnect()
            findNavController().popBackStack()

        }

        binding.cancelMatch.setOnClickListener {

            time.cancel()
            leavePlayer()
            mSocket?.disconnect()
            findNavController().popBackStack()
        }
    }


    private fun countdownTimer() {
        time = object : CountDownTimer(180000, 1000) {

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
                binding.countdown.text = hms //set text


//                binding.countdown.text = (millisUntilFinished / 1000).toString()

//                binding.countdown.text = ""+millisUntilFinished / 1000
            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                activity?.runOnUiThread {

                    showDialog(
                        "Request Timeout. No player found at the moment. Try again later",
                        "",
                        0
                    )
                    SocketHandler.closeConnection()

                }
            }
        }.start()
    }


    private fun practiceModeCheck() {
        if (click!!.isPractice) {

            URLConstant.isPractice = click!!.isPractice

            time.cancel()
            StaticFields.toastClass("Practice Match is about to begin.")

            Handler(Looper.getMainLooper()).postDelayed(
                Runnable {
                    showDialog("Practice match will start in a momment, Please Wait.", "Deposit", 2)

                }, 1500
            )

            return
        } else {
            URLConstant.isPractice = click!!.isPractice

        }
    }


    private fun userNameandImg() {
        if (sharedPreference.returnValue("USERIMG") != null
            && sharedPreference.returnValue("USERNAME") != null
        ) {
            binding.userImg.load(sharedPreference.returnValue("USERIMG")) {
                placeholder(R.drawable.ic_baseline_person_24)
                error(R.drawable.ic_baseline_person_24)
            }
            //underline text
            val mSpannableString =
                SpannableString(sharedPreference.returnValue("USERNAME") ?: "Name")
            mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
            binding.userName.text = mSpannableString

        }
    }


    private fun showDialog(t: String, h: String, check: Int) {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogDepositBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)
        if (check == 0) {
            dialogBinding.h1.visibility = View.GONE
            dialogBinding.para.text = t
            dialogBinding.price.visibility = View.GONE
            dialogBinding.cancelButton.visibility = View.GONE
            dialogBinding.cancelButton2.text = "Continue Searching"
            dialogBinding.cancelButton2.visibility = View.VISIBLE
            dialogBinding.okButton.text = "return to Play Screen"
        } else {
            dialogBinding.h1.text = h
            dialogBinding.para.text = t
            dialogBinding.price.text = " Entry Fee: ${click?.entryFee}"
            dialogBinding.okButton.visibility = View.GONE
            dialogBinding.cancelButton.visibility = View.GONE
        }

        dialogBinding.cancelButton2.setOnClickListener {
            time.cancel()
            time.start()
            connectSocket()
            dialog.dismiss()
        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.okButton.setOnClickListener {
            if (check == 0) {
                time.cancel()
                leavePlayer()
                mSocket?.disconnect()
                findNavController().popBackStack()
            }
            dialog.dismiss()

        }

        if (check == 1) {
            Handler(Looper.getMainLooper()).postDelayed(
                Runnable {
                    participateInTournament()
                    dialog.dismiss()
                }, 1500
            )
        } else if (check == 2) {
            Handler(Looper.getMainLooper()).postDelayed(
                Runnable {
                    navigateTo()
                    dialog.dismiss()
                }, 1500
            )
        }

        dialog.show()
    }

    fun navigateTo() {
        activity?.runOnUiThread {
            backtoHomeORExitAPP = true
            findNavController().navigate(R.id.action_findCompetitiveFragment_to_fragmentMatchScore)
        }
    }


    private fun participateInTournament() {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.participateTournament(
                URLConstant.u_id!!,
                if (URLConstant.oneToOne) {
                    ""
                } else {
                    click?.tournamentID!!
                },
                "abcd",
                object : NetworkListener<ForgotModel> {
                    override fun successFul(t: ForgotModel) {
//                        loading.isDismiss()

                        activity?.runOnUiThread {

                            if (t.status == 1) {


                                StaticFields.toastClass(t.message)
                                backtoHomeORExitAPP = true
                                findNavController().navigate(R.id.action_findCompetitiveFragment_to_fragmentMatchScore)


                            } else {
                                activity?.runOnUiThread {

                                    StaticFields.toastClass(t.message)

                                }
                            }

                        }
                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail participation")
                        }
                    }
                }
            )
        }
    }


    private fun connectSocket() {
        loading.startLoading()
        SocketHandler.setSocket()
        mSocket = SocketHandler.getSocket()

        SocketHandler.establishConnection()

        mSocket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e(
                "error",
                "onCreate:${args.contentToString()} "
            )
        }

        mSocket?.on(Socket.EVENT_CONNECT) { args ->
            Log.e("connect", "onCreate: ")

            loading.isDismiss()

            joinPlayer()


            mSocket?.on("left1Match")
            { args ->

                activity?.runOnUiThread {

                    try {

                        val json = (args[0] as JSONObject) as JSONObject
                        val gsonobj = Gson()

                        val obj1 = gsonobj.fromJson(json.toString(), SendMsgModelData::class.java)
                        if (obj1.leftStatus) {


                        } else {
                            StaticFields.toastClass("You Can't leave the match now.")
                        }

                    } catch (e: Exception) {
                        Log.e(
                            "exceptionLeftGame=",
                            "$e"
                        )
                    }
                }


            }

            mSocket?.on(
                "tryAgain"
            ) { args ->

                activity?.runOnUiThread {

                    joinPlayer()

                }
            }




            mSocket?.on(
                "getMatchInfo"
            ) { args ->

                activity?.runOnUiThread {


                    try {

                        val json = (args[0] as JSONObject) as JSONObject
//                        roomID = (args[0] as JSONObject).get("roomId") as Int
                        val gsonobj = Gson()

                        val obj1 =
                            gsonobj.fromJson(json.toString(), GetRandomPlayerModelData::class.java)

                        URLConstant.matchId = obj1?.matchID

                        binding.cancelMatch.isClickable = true
                        binding.cancelMatch.isEnabled = true
                        binding.cancelMatch.alpha = 1.0f


                        binding.exitMatch.isClickable = true
                        binding.exitMatch.isEnabled = true
                        binding.exitMatch.alpha = 1.0f
                        //is match id pr report hona hai match lkn hum getmatches record k response ki match id pr report kr rahy jo k thk hai hai


                        if (click?.gamePlay == 1) {
                            if (obj1?.IsPlayable!! && click?.playerCount?.toInt() == obj1?.playerCount) {
                                time.cancel()

                                showDialog(
                                    "Match fee has been deducted from your account. Match is about to start.",
                                    "Deposit",
                                    1
                                )
                            }
                            return@runOnUiThread
                        } else if (click?.gamePlay == 2) {
                            if (obj1?.IsPlayable!!) {
                                time.cancel()

                                showDialog(
                                    "Match fee has been deducted from your account. Match is about to start.",
                                    "Deposit",
                                    1
                                )
                            }

                        }
                    } catch (e: Exception) {
                        Log.e(
                            "exception=",
                            "$e"
                        )
                    }
                }

                Log.e(
                    "retrievematchInfo=",
                    "onCreate:${args.contentToString()} "
                )

            }

            mSocket?.on(
                "getRandomPlayer"
            ) { args ->

                activity?.runOnUiThread {
                    try {

                        val json = (args[0] as JSONArray) as JSONArray

                        opponentList = ArrayList()

                        for (index in 0 until json.length()) {

                            val obj = Gson().fromJson(
                                json.getJSONObject(index).toString(),
                                ListofOpponentModel::class.java
                            )

                            if (obj.opponentId != URLConstant.u_id) {
                                opponentList.add(obj)
                            }

                        }
                        if (click?.gamePlay == 1) {
                            if (opponentList.size > 0) {
                                StaticFields.toastClass("New Player has joined")
                            } else if (opponentList.size == 0 || opponentList.size < click?.playerCount!!) {

                                StaticFields.toastClass(" Please wait while we are searching opponent for you.")

                            }

                            rvAdapter.setData(opponentList)

                        } else if (click?.gamePlay == 2) {
                            opponentList = ArrayList()
                            if (opponentList.size == 0) {
                                StaticFields.toastClass("Its a Non-Live Match. Wait Please.")
                            }
                            rvAdapter.setData(opponentList)
                        }

                    } catch (e: Exception) {
                        Log.e(
                            "exception=",
                            "$e"
                        )

                    }

                }

                Log.e(
                    "retrieveOpponentSocket",
                    "onCreate:${args.contentToString()} "
                )

            }


        }

    }


    private fun joinPlayer() {
        if (mSocket?.connected() == true) {
            Log.e("connected", "onCreate:bami ")


            if (URLConstant.oneToOne) {
                val checked = WebSocketJoinRoomOne2OneModel(
                    StaticFields.key,
                    URLConstant.u_id!!
                )

                gson = JsonParser.parseString(Gson().toJson(checked)).asJsonObject

            } else {
                val checked = WebSocketJoinLeaveModel(
                    click?.tournamentID!!,
                    URLConstant.u_id!!

                )

                gson = JsonParser.parseString(Gson().toJson(checked)).asJsonObject

            }
            mSocket?.emit("joinPlayer", gson)

        } else {
            Log.e("notconnected", "onCreate:bami ")
        }

    }


    private fun leavePlayer() {
        if (mSocket?.connected() == true) {
            Log.e("connected", "onCreate:bami ")


            val checked = WebSocketLeaveModel(
                URLConstant.matchId,
                URLConstant.u_id!!,
            )

            val gsonobj: JsonObject = JsonParser.parseString(Gson().toJson(checked)).asJsonObject

            mSocket?.emit("leavePlayer", gsonobj)
        } else {
            Log.e("notconnected", "onCreate:bami ")
        }

    }

    override fun onPause() {

        if (!backtoHomeORExitAPP) {
            leavePlayer()
            mSocket?.disconnect()
        }
        super.onPause()

    }
}







