package com.arhamsoft.deskilz.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arhamsoft.deskilz.databinding.FragmentPendingRequestBinding
import com.arhamsoft.deskilz.networking.networkModels.PlayerWaitingModelData
import com.arhamsoft.deskilz.ui.adapter.RecyclerViewLoadMoreScroll
import com.arhamsoft.deskilz.utils.LoadingDialog

class PendingRequestFragment : Fragment() {

    lateinit var binding: FragmentPendingRequestBinding
    lateinit var loading: LoadingDialog
    var u_id: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingRequestBinding.inflate(LayoutInflater.from(context))
        loading = LoadingDialog(requireContext() as Activity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


    }

}