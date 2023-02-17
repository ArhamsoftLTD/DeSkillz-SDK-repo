package com.arhamsoft.deskilz.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arhamsoft.deskilz.databinding.FragmentNotificationsBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.ChatPost
import com.arhamsoft.deskilz.networking.networkModels.NotificationModel
import com.arhamsoft.deskilz.networking.networkModels.NotificationModelData
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.ui.adapter.RVAdapterNotification
import com.arhamsoft.deskilz.ui.adapter.RecyclerViewLoadMoreScroll
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.tablitsolutions.crm.activities.OnLoadMoreListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NotificationsFragment : Fragment() {

    lateinit var binding: FragmentNotificationsBinding
    lateinit var loading: LoadingDialog
    lateinit var rvLoadMore: RecyclerViewLoadMoreScroll
    private lateinit var rvAdapter: RVAdapterNotification
    var notificationList: ArrayList<NotificationModelData> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(LayoutInflater.from(context))

        loading = LoadingDialog(requireContext() as Activity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        if (!(StaticFields.isNetworkConnected(requireContext()))) {
            StaticFields.toastClass("Check your network connection")
        } else {
            loading.startLoading()
            getNotifcations(0, false)

        }

        rvAdapter =
            RVAdapterNotification(object : RVAdapterNotification.OnItemClickListenerHandler {
                override fun onItemClicked(click: NotificationModelData, position: Int) {
                }
            })


        binding.recycleListNotifications.adapter = rvAdapter


    }


    private fun getNotifcations(off: Int, isLoadMore: Boolean) {


        val checked = ChatPost(
            10,
            off + 1,
            URLConstant.u_id!!
        )

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.receiveNotifications(
                checked,
                object : NetworkListener<NotificationModel> {
                    override fun successFul(t: NotificationModel) {
                        loading.isDismiss()
                        if (t.status == 1) {


                            activity?.runOnUiThread {

                                if (t.data.isNotEmpty()) {


                                    notificationList.addAll(t.data)

                                    rvAdapter.setData(notificationList)

//                                    if (isLoadMore) {
//                                        rvLoadMore.setLoaded()
//                                    }
                                    binding.progressBar.visibility = View.GONE

                                } else {

                                    rvAdapter.setData(notificationList)
                                }


                            }

                        }

                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail get notifications")
                        }
                    }
                }
            )
        }

    }

    private fun initScrollListener() {
        rvLoadMore =
            RecyclerViewLoadMoreScroll(binding.recycleListNotifications.layoutManager as LinearLayoutManager)
        rvLoadMore.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                loadMore()
            }
        })
        binding.recycleListNotifications.addOnScrollListener(rvLoadMore)
    }

    private fun loadMore() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
            }
            getNotifcations((notificationList.size), true)
        }
    }

}