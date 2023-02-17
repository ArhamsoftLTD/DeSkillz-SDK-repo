package com.arhamsoft.deskilz.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.alphaCareInc.app.room.UserDatabase
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.DialogUserChatBinding
import com.arhamsoft.deskilz.databinding.FragmentChatBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.*
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.services.SocketHandler
import com.arhamsoft.deskilz.ui.adapter.RVAdapterComment
import com.arhamsoft.deskilz.ui.adapter.RecyclerViewLoadMoreScroll
import com.arhamsoft.deskilz.utils.CustomSharedPreference
import com.arhamsoft.deskilz.utils.InternetConLiveData
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var navController: NavController
    lateinit var loading: LoadingDialog
    lateinit var rvLoadMore: RecyclerViewLoadMoreScroll
    private lateinit var rvAdapter: RVAdapterComment
    private var mSocket: Socket? = null
    var receivedChat: ArrayList<ReceivedDataFromSocket> = ArrayList()
    var roomID: Int = 0
    var u_id: String? = ""
    private lateinit var connection: InternetConLiveData
    var id: Int? = 0
    lateinit var sharedPreference: CustomSharedPreference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        navController = findNavController()
        loading = LoadingDialog(requireContext() as Activity)
        sharedPreference = CustomSharedPreference(requireContext())

        checkNetworkConnection()


        if (!(StaticFields.isNetworkConnected(requireContext()))) {
            StaticFields.toastClass("Check your network connection")
        } else {
            connectSocket()
        }

        id = sharedPreference.returnCurrentLoginID("user")


        runBlocking {
            val user = UserDatabase.getDatabase(requireContext()).userDao().getUser(id!!)
            if (user != null) {
                URLConstant.u_id = user.userId
            }
        }



        mSocket?.io()?.on(Socket.EVENT_DISCONNECT) { args ->
            Log.e(
                "Disconnect",
                "onCreate:${args.contentToString()} "
            )
        }


        return binding.root
    }

    private fun checkNetworkConnection() {

        connection = InternetConLiveData(requireContext())

        connection.observe(viewLifecycleOwner) { isConnected ->

            if (isConnected) {
                binding.noint.noInternet.visibility = View.GONE
            } else {
                binding.noint.noInternet.visibility = View.VISIBLE

            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {

            navController.popBackStack()
            URLConstant.joinType = 9
            SocketHandler.closeConnection()


        }


        binding.postComment.setOnClickListener {


            val checked = WebSocketSendMsgModel(
                roomID,
                URLConstant.u_id,
                binding.comment.text.toString()
            )

            val gson: JsonObject = JsonParser.parseString(Gson().toJson(checked)).asJsonObject

            if (binding.comment.text.isEmpty()) {
                StaticFields.toastClass("write a comment.")
            } else {
                mSocket?.emit(
                    "sendMessage",
                    gson
                )
                binding.comment.setText("")
            }

        }

        binding.addFriends.setOnClickListener {

            navController.navigate(R.id.action_chatFragment_to_chatHeadsFragment)
            URLConstant.joinType = 9
        }

        binding.recycleListComment.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)



        rvAdapter =
            RVAdapterComment(requireContext(),
                receivedChat,
                SimpleDateFormat("dd-MM-yyyy").format((Calendar.getInstance()).time.time),
                object : RVAdapterComment.OnItemClick {
                    override fun onOthersClick(click: ReceivedDataFromSocket, position: Int) {

                        loading.startLoading()
                        checkFriend(click.userId, click.userImage!!, click.senderName)

                    }

                    override fun onUserClick(click: ReceivedDataFromSocket, position: Int) {

                    }
                })
        binding.recycleListComment.adapter = rvAdapter

    }


    private fun showDialog(
        checkFrnd: Boolean,
        checkReq: Boolean,
        img: String,
        name: String,
        playerId: String
    ) {


        val dialog = Dialog(requireContext())
        val dialogBinding = DialogUserChatBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setCancelable(true)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.oppoenetImg.load(img) {
            placeholder(R.drawable.ic_baseline_person_24)
            error(R.drawable.ic_baseline_person_24)
        }
        dialogBinding.oppoenetName.text = name



        if (!checkFrnd && checkReq) {
            dialogBinding.addFriendBtn.text = "pending"
            dialogBinding.addFriendBtn.isEnabled = false
        } else if (checkFrnd && !checkReq) {
            dialogBinding.addFriendBtn.text = "Added"
            dialogBinding.addFriendBtn.isEnabled = false
        } else if (checkFrnd && checkReq) {
            dialogBinding.addFriendBtn.text = "Added"
            dialogBinding.addFriendBtn.isEnabled = false
        } else if (!checkFrnd && !checkReq) {
            dialogBinding.addFriendBtn.setOnClickListener {
                loading.startLoading()
                addFriendsApi(playerId)
                dialogBinding.addFriendBtn.text = "pending"
                dialogBinding.addFriendBtn.isEnabled = false
            }
        }

        dialog.show()
    }


    private fun addFriendsApi(playerId: String) {
//
//        val checked = chatPost(
//            10,
//            off + 1,
//            "1234564789"
//        )

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.addFriends(
                URLConstant.u_id!!,
                playerId,
                object : NetworkListener<AddFriendModel> {
                    override fun successFul(t: AddFriendModel) {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            if (t.status == 1) {

                                StaticFields.toastClass(t.message)

                            } else {
                                StaticFields.toastClass(t.message)

                            }
                        }
                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Apisyncing fail add friend")
                        }
                    }
                }
            )
        }

    }

    fun checkFriend(playerId: String, img: String, name: String) {
//
//        val checked = chatPost(
//            10,
//            off + 1,
//            "1234564789"
//        )

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.checkFriend(
                URLConstant.u_id!!,
                playerId,
                object : NetworkListener<ForgotModel> {
                    override fun successFul(t: ForgotModel) {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            if (t.status == 1) {

                                StaticFields.toastClass(t.message)


                                showDialog(t.isFriend, t.isFriendRequest, img, name, playerId)

                            } else {
                                StaticFields.toastClass(t.message)
                                showDialog(t.isFriend, t.isFriendRequest, img, name, playerId)


                            }
                        }
                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing failed check friend")
                        }
                    }
                }
            )
        }

    }


    private fun joinRoomForGLobalChat() {
        if (mSocket?.connected() == true) {
            Log.e("connected", "onCreate:bami ")


            val checked = WebSocketJoinRoomModel(
                StaticFields.key,
                URLConstant.u_id,
                "",
                0
            )

            val gson: JsonObject = JsonParser.parseString(Gson().toJson(checked)).asJsonObject

            mSocket?.emit("joinRoom", gson)
        } else {
            Log.e("notconnected", "onCreate:bami ")
        }

        mSocket?.on("joinRoom") { args ->
            Log.e(
                "JOINROOM",
                "onCreate:${args.contentToString()} "
            )
        }

    }

    private fun joinRoomForOne2OneChat(friend_id: String) {
        if (mSocket?.connected() == true) {
            Log.e("connected", "onCreate:bami ")


            val checked = WebSocketJoinRoomModel(
                StaticFields.key,
                URLConstant.u_id,
                friend_id,
                1
            )

            val gson: JsonObject = JsonParser.parseString(Gson().toJson(checked)).asJsonObject

            mSocket?.emit("joinRoom", gson)

        } else {
            Log.e("notconnected", "onCreate:bami ")
        }

        mSocket?.on("joinRoom") { args ->
            Log.e(
                "JOINROOM",
                "onCreate:${args.contentToString()} "
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

            val bundle = arguments
            if (bundle != null) {

                if (bundle.getInt("GLOBAL_CHAT") == 1) {
                    joinRoomForGLobalChat()
                    URLConstant.joinType = 0

                } else {

                    joinRoomForOne2OneChat(bundle.getSerializable("FRIEND_ID") as String)
                    URLConstant.joinType = 1

                }


                mSocket?.on(
                    "RetrieveChatRoomData"
                ) { args ->

                    try {
                        receivedChat = ArrayList()

                        val arr = (args[0] as JSONObject).get("messages") as JSONArray
                        roomID = (args[0] as JSONObject).get("roomId") as Int

                        Log.e("chatArray= ", arr.toString())

                        for (index in 0 until arr.length()) {

                            receivedChat.add(
                                Gson().fromJson(
                                    arr.getJSONObject(index).toString(),
                                    ReceivedDataFromSocket::class.java
                                )
                            )
                        }
                        receivedChat.reverse()
                        activity?.runOnUiThread {
                            rvAdapter.addData(receivedChat)
                            binding.recycleListComment.smoothScrollToPosition(0)
                            loading.isDismiss()
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "exception=",
                            "$e"
                        )
                    }


                    Log.e(
                        "retrieve",
                        "onCreate:${args.contentToString()} "
                    )
                }

            }

        }
    }

    override fun onStop() {
        super.onStop()

        SocketHandler.closeConnection()
        URLConstant.joinType = 9
    }
}