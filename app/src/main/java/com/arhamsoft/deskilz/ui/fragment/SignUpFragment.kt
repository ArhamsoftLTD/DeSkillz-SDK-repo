package com.arhamsoft.deskilz.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arhamsoft.deskilz.databinding.FragmentSignUpBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.SignUpModel
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    lateinit var loading: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        loading = LoadingDialog(requireContext() as Activity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.setOnClickListener {

            registerApiCalling()

        }

        binding.gotoSignIn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.backToSignIn.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    fun registerApiCalling() {


        if (binding.mail.text!!.isEmpty() && binding.password.text!!.isEmpty() && binding.repassword.text!!.isEmpty()) {
            Toast.makeText(requireContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show()

        } else {
            if (binding.mail.text!!.isEmpty()) {
                binding.mail.requestFocus()
                binding.mail.error = "Email Field is Empty"
            } else if (binding.password.text!!.isEmpty()) {
                binding.password.requestFocus()
                binding.password.error = "pass Field is Empty"

            } else if (binding.repassword.text!!.isEmpty()) {
                binding.repassword.requestFocus()
                binding.repassword.error = "repass Field is Empty"

            } else if (binding.password.text.toString() != binding.repassword.text.toString()) {
                StaticFields.toastClass("You Have Entered a Wrong Re-Password")
            } else if (!binding.consentCheck.isChecked) {
                StaticFields.toastClass("Accept terms and condition in order to continue")
            } else if (!(isValidEmail(binding.mail.text.toString()))) {
                binding.mail.error = "Email Format is Incorrect"
                binding.mail.requestFocus()

            } else if (binding.password.text.toString() == binding.repassword.text.toString()) {

                loading.startLoading()
                CoroutineScope(Dispatchers.IO).launch {

                    NetworkRepo.register(
                        binding.username.text.toString(),
                        binding.mail.text.toString(),
                        binding.password.text.toString(),
                        "Pakistan",
                        object : NetworkListener<SignUpModel> {
                            override fun successFul(t: SignUpModel) {

                                activity?.runOnUiThread {
                                    if (t.success == 1) {
                                        loading.isDismiss()

                                        StaticFields.toastClass(t.message)

                                        findNavController().popBackStack()
                                    } else {
                                        loading.isDismiss()
                                        StaticFields.toastClass(t.message)
                                        findNavController().popBackStack()


                                    }
                                }

                            }

                            override fun failure() {
                                activity?.runOnUiThread {

                                    loading.isDismiss()
                                    StaticFields.toastClass("api syncing failed signup")
                                }
                            }
                        }
                    )
                }

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

}