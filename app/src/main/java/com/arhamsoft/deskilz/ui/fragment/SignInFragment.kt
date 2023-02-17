package com.arhamsoft.deskilz.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alphaCareInc.app.room.User
import com.alphaCareInc.app.room.UserDatabase
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.ForgotDialogBinding
import com.arhamsoft.deskilz.databinding.FragmentSignInBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.ForgotModel
import com.arhamsoft.deskilz.networking.networkModels.LoginModel
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.utils.CustomSharedPreference
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding
    lateinit var loadingDialog: LoadingDialog
    private lateinit var database: UserDatabase
    private var device_id: String? = null
    lateinit var sharedPreference: CustomSharedPreference
    private var fcmToken: String? = null
    var id: Int? = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        database = UserDatabase.getDatabase(requireContext())
        loadingDialog = LoadingDialog(requireContext() as Activity)
        sharedPreference = CustomSharedPreference(requireContext())


        cancelNotification()

        StaticFields.fcmToken()


        fcmToken = sharedPreference.returnValue("TOKEN")

        device_id =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        sharedPreference.saveValue("DEVICE_ID", device_id!!)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.setOnClickListener {
            loginApiCall()
        }


        binding.forgot.setOnClickListener {
            forgotDialog()
        }

        binding.gotoSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

    }

    fun cancelNotification() {
        val ns = NOTIFICATION_SERVICE
        val nMgr = activity?.applicationContext?.getSystemService(ns) as NotificationManager
        if (nMgr.activeNotifications.isNotEmpty()) {
            nMgr.cancelAll()

        }
    }

    private fun loginApiCall() {

        if (binding.mail.text!!.isEmpty() && binding.password.text!!.isEmpty()) {
            binding.mail.error = "Email Field is Empty"
            binding.password.error = "Password Field is Empty"
        } else if (binding.mail.text!!.isEmpty()) {
            binding.mail.requestFocus()
            binding.mail.error = "Email Field is Empty"

        } else if (binding.password.text!!.isEmpty()) {
            binding.password.requestFocus()
            binding.password.error = "Password Field is Empty"

        } else if (!(isValidEmail(binding.mail.text.toString()))) {
            binding.mail.error = "Email Format is Incorrect"
            binding.mail.requestFocus()

        } else {


            loadingDialog.startLoading()
            CoroutineScope(Dispatchers.IO).launch {

                NetworkRepo.login(
                    binding.mail.text.toString(),
                    binding.password.text.toString(),
                    device_id!!,
                    fcmToken!!,
                    object : NetworkListener<LoginModel> {
                        override fun successFul(t: LoginModel) {
                            activity?.runOnUiThread {

                                if (t.status == 1) {

                                    if (t.data.userID == URLConstant.u_id) {
                                        loadingDialog.isDismiss()
                                        StaticFields.toastClass("Current User Already Login")

                                    } else {


                                        val user = User()
                                        user.accessToken = t.data.authToken
                                        user.userId = t.data.userID
                                        user.userName = t.data.userName
                                        user.userEmail = binding.mail.text.toString()
                                        insertData(user)

                                        StaticFields.toastClass(t.message)

                                        sharedPreference.saveValue("USERIMG", t.data.userImage)
                                        sharedPreference.saveValue("USERNAME", t.data.userName)

                                        sharedPreference.saveLogin("LOGIN", true)
                                        //RetrofitClient.updateInstance()

                                        loadingDialog.isDismiss()

                                        findNavController().navigate(R.id.action_signInFragment_to_dashboardActivity)

                                    }

                                } else {
                                    loadingDialog.isDismiss()
                                    StaticFields.toastClass(t.message)

                                }
                            }
                        }

                        override fun failure() {
                            activity?.runOnUiThread {

                                loadingDialog.isDismiss()
                                StaticFields.toastClass("api syncing failed login")

                            }
                        }
                    }
                )
            }
        }
    }


    fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }


    private fun forgotDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = ForgotDialogBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)



        dialogBinding.submitButton.setOnClickListener {


            loadingDialog.startLoading()
            forgotPassApiCall(dialogBinding.forgotemail.text.toString())

        }
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun insertData(user: User) {

        val th = Thread(Runnable {
            val already = database.userDao().getPreviousUser(user.userId!!)

            if (already == null) {
                val returnId = database.userDao().insertUser(user)
                URLConstant.currentLoginId = returnId.toInt()
            } else {
                already.userName = user.userName
                already.userEmail = user.userEmail
                already.accessToken = user.accessToken
                database.userDao().updateUser(already)
                URLConstant.currentLoginId = already.id
            }
            sharedPreference.saveCurrentLoginID("user", URLConstant.currentLoginId)

            NetworkRepo.updateRetrofitClientInstance()
        })
        th.start()
        th.join()
    }


    fun forgotPassApiCall(mail: String) {

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.forgot(
                mail,
                device_id!!,
                object : NetworkListener<ForgotModel> {
                    override fun successFul(t: ForgotModel) {
                        loadingDialog.isDismiss()

                        activity?.runOnUiThread {


                            if (t.status == 1) {
                                StaticFields.toastClass(t.message)
                            } else {
                                StaticFields.toastClass(t.message)

                            }
                        }
                    }

                    override fun failure() {
                        loadingDialog.isDismiss()

                        activity?.runOnUiThread {

                            StaticFields.toastClass("api syncing failed forgot pass")

                        }
                    }
                }
            )
        }

    }


}