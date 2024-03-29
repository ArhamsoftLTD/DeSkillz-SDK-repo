package com.arhamsoft.deskilz.ui.fragment.navBarFragments

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.FragmentProfileBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.EarnedTrophiesModel
import com.arhamsoft.deskilz.networking.networkModels.UserDetailedInfoModel
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.ui.adapter.AdapterTrophies
import com.arhamsoft.deskilz.utils.CustomSharedPreference
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var loading: LoadingDialog
    lateinit var sharedPreference: CustomSharedPreference

    lateinit var rvAdapter: AdapterTrophies
    var trophyList: ArrayList<EarnedTrophiesModel> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(LayoutInflater.from(context))
        loading = LoadingDialog(requireContext() as Activity)
        sharedPreference = CustomSharedPreference(requireContext())

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycleListTrophies.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        rvAdapter = AdapterTrophies(object : AdapterTrophies.OnItemClickListenerHandler {
            override fun onItemClicked(click: EarnedTrophiesModel, position: Int) {

            }
        })

        binding.recycleListTrophies.adapter = rvAdapter


        binding.backtoDashboard.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.userImg.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_updateProfileFragment)
        }

        getUserDetailedInfo()

    }

    private fun getUserDetailedInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                loading.startLoading()
            }
            NetworkRepo.getUserDetailedInfo(
                URLConstant.u_id!!,
                object : NetworkListener<UserDetailedInfoModel> {
                    override fun successFul(userDetailedInfoModel: UserDetailedInfoModel) {
                        loading.isDismiss()
                        if (userDetailedInfoModel.status == 1) {

                            activity?.runOnUiThread {

                                sharedPreference.saveValue(
                                    "USERIMG",
                                    userDetailedInfoModel.data.userData.userImage
                                )
                                sharedPreference.saveValue(
                                    "USERNAME",
                                    userDetailedInfoModel.data.userData.userName
                                )
                                sharedPreference.saveValue(
                                    "SHOUTOUT",
                                    userDetailedInfoModel.data.userData.userShoutOut
                                )

                                trophyList.addAll(userDetailedInfoModel.data.earnedTrophies)
                                rvAdapter.setData(trophyList)
                                trophyList = ArrayList()

                                binding.allTrophies.setOnClickListener {

                                    val bundle = bundleOf()
                                    bundle.putString(
                                        "allTrophyList",
                                        Gson().toJson(userDetailedInfoModel.data.allTrophies)
                                    )
                                    bundle.putString(
                                        "earnedTrophyList",
                                        Gson().toJson(userDetailedInfoModel.data.earnedTrophies)
                                    )
                                    findNavController().navigate(
                                        R.id.action_profileFragment_to_trophyScreenDetailFragment,
                                        bundle
                                    )
                                }

                                binding.userImg.load(userDetailedInfoModel.data.userData.userImage) {
                                    placeholder(R.drawable.ic_baseline_person_24)
                                    error(R.drawable.ic_baseline_person_24)
                                }
                                binding.Flag.load(userDetailedInfoModel.data.userData.userCountryFlag)
                                binding.userName.text = userDetailedInfoModel.data.userData.userName
                                binding.deskillzLevel.text =
                                    userDetailedInfoModel.data.deskillzLevel.toString()

                                ObjectAnimator.ofInt(
                                    binding.deskillzLevelBar,
                                    "progress",
                                    userDetailedInfoModel.data.deskillzLevel
                                )
                                    .setDuration(500)
                                    .start()
//                                val anim = ProgressAnim(binding.deskillzLevelBar, 0.0f, 25.0f)
//                                anim.duration = 300
//                                binding.deskillzLevelBar.startAnimation(anim)
                                binding.gameRank.text =
                                    userDetailedInfoModel.data.currentGameRank.toString()
                                ObjectAnimator.ofInt(
                                    binding.gameRankBar,
                                    "progress",
                                    userDetailedInfoModel.data.currentGameRank
                                )
                                    .setDuration(500)
                                    .start()

                                binding.trophies.text =
                                    userDetailedInfoModel.data.earnedTrophies.size.toString()
                                ObjectAnimator.ofInt(
                                    binding.progressBar3,
                                    "progress",
                                    userDetailedInfoModel.data.earnedTrophies.size
                                )
                                    .setDuration(500)
                                    .start()

                                binding.coinNo.text =
                                    userDetailedInfoModel.data.deskillzCoin.toString()
//                                binding.ticketsNo.text = "0"
                                binding.dollar.text = "0.0"
                                binding.shoutout.text =
                                    userDetailedInfoModel.data.userData.userShoutOut

                                binding.winStreak.text =
                                    userDetailedInfoModel.data.winStreak.toString()
                                binding.matchesWon.text =
                                    userDetailedInfoModel.data.userWin.toString()
//                                    binding.shoutout.text = t.data.userData.userShoutOut
                                binding.win.text = userDetailedInfoModel.data.userWin.toString()
                                binding.lose.text = userDetailedInfoModel.data.userLoose.toString()

                            }
                        }
                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail user detail info ")
                        }
                    }
                }
            )
        }

    }
}