package com.arhamsoft.deskilz.ui.fragment.navBarFragments

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.FragmentHomeScreenBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.*
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.ui.adapter.AdapterHomeScreen
import com.arhamsoft.deskilz.ui.adapter.RVAdapterOngoinOnetoOne
import com.arhamsoft.deskilz.utils.InternetConLiveData
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.tablitsolutions.crm.activities.OnLoadMoreListener
import com.arhamsoft.deskilz.ui.adapter.RecyclerViewLoadMoreScroll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeScreenFragment : Fragment() {

    private lateinit var binding: FragmentHomeScreenBinding
    private lateinit var adapterHomeScreen: AdapterHomeScreen
    private lateinit var navController: NavController
    lateinit var loading: LoadingDialog
    lateinit var recyclerView: RecyclerView
    lateinit var rvLoadMore: RecyclerViewLoadMoreScroll
    var completedList: ArrayList<GetMatchesRecordData> = ArrayList()
    private lateinit var connection: InternetConLiveData
    private lateinit var rvAdapterOnetoOne: RVAdapterOngoinOnetoOne

    var oneToOneAndTournamentList: ArrayList<PlayerWaitingModelData> = ArrayList()
    var u_id: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)
        navController = findNavController()
        loading = LoadingDialog(requireContext() as Activity)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        CoroutineScope(Dispatchers.IO).launch {
//            val user = UserDatabase.getDatabase(requireContext()).userDao().getUser()
//            if (user != null) {
//                u_id = user.userId
//            }
//        }
//        checkNetworkConnection()

        if (!(StaticFields.isNetworkConnected(requireContext()))) {
            StaticFields.toastClass("Check your network connection")
        } else {
            loading.startLoading()
//        URLConstant.check = false
//        getGameCustomData()
            getUserDetailedInfo()
            getWaitingList()
            getMatchesRecord(0, false)
        }


        initClicks()

//        initScrollListener()

        binding.recycleListcompleted.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        adapterHomeScreen =
            AdapterHomeScreen(object : AdapterHomeScreen.OnItemClickListenerHandler {

                override fun onItemClicked(click: GetMatchesRecordData, position: Int) {


                    val bundle = bundleOf()
                    bundle.putSerializable("MATCHRECORDOBJECT", click)
//                bundle.putString("MATCHID",click.matchId)
//                bundle.putLong("TICKETS",click.cash)
//                bundle.putLong("XPOPPONENT",click.xpValueOther)
//                bundle.putLong("XPUSER",click.xpValueUser)


                    navController.navigate(
                        R.id.action_dashboardActivity_to_gameResultFragment,
                        bundle
                    )
                }

            })
        binding.recycleListcompleted.adapter = adapterHomeScreen




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


    private fun initClicks() {
        binding.deskillzLevelLayout.setOnClickListener {
            navController.navigate(R.id.action_dashboardActivity_to_levelScreenFragment)
        }

        binding.battleFieldLayout.setOnClickListener {
            navController.navigate(R.id.action_dashboardActivity_to_levelScreenFragment)
        }

//        binding.bronzeTiersLayout.setOnClickListener {
//            navController.navigate(R.id.action_dashboardActivity_to_rewardFragment)
//        }

    }

    private fun getUserDetailedInfo() {
        CoroutineScope(Dispatchers.IO).launch {

            NetworkRepo.getUserDetailedInfo(
                URLConstant.u_id!!,
                object : NetworkListener<UserDetailedInfoModel> {
                    override fun successFul(userDetailedInfoModel: UserDetailedInfoModel) {
                        loading.isDismiss()
                        if (userDetailedInfoModel.status == 1) {

                            activity?.runOnUiThread {

//                                sharedPreference.saveValue(
//                                    "USERIMG",
//                                    userDetailedInfoModel.data.userData.userImage
//                                )
//                                sharedPreference.saveValue("USERNAME", userDetailedInfoModel.data.userData.userName)
//                                sharedPreference.saveValue("SHOUTOUT", userDetailedInfoModel.data.userData.userShoutOut)

//                                binding.userImg.load(userDetailedInfoModel.data.userData.userImage) {
//                                    placeholder(R.drawable.ic_baseline_person_24)
//                                    error(R.drawable.ic_baseline_person_24)
//                                }
//                                binding.Flag.load(userDetailedInfoModel.data.userData.userCountryFlag)
//                                binding.userName.text = userDetailedInfoModel.data.userData.userName
                                binding.deskillzLevel.text = userDetailedInfoModel.data.deskillzLevel.toString()

                                ObjectAnimator.ofInt(binding.deskillzLevelBar, "progress", userDetailedInfoModel.data.deskillzLevel)
                                    .setDuration(500)
                                    .start()
//                                val anim = ProgressAnim(binding.deskillzLevelBar, 0.0f, 25.0f)
//                                anim.duration = 300
//                                binding.deskillzLevelBar.startAnimation(anim)
                                binding.gameRank.text = userDetailedInfoModel.data.currentGameRank.toString()
                                ObjectAnimator.ofInt(binding.gameRankBar, "progress", userDetailedInfoModel.data.currentGameRank)
                                    .setDuration(500)
                                    .start()



                                binding.trophies.text = userDetailedInfoModel.data.earnedTrophies.size.toString()
                                ObjectAnimator.ofInt(binding.progressBar3, "progress", userDetailedInfoModel.data.earnedTrophies.size)
                                    .setDuration(500)
                                    .start()
//                                binding.progressXp3.text = userDetailedInfoModel.data.currentGameRank.toString()
//                                binding.coinNo.text = userDetailedInfoModel.data.progressXp.toString()
////                                binding.ticketsNo.text = "0"
//                                binding.dollar.text = "0.0"
//                                binding.shoutout.text = userDetailedInfoModel.data.userData.userShoutOut
//
//                                binding.winStreak.text = userDetailedInfoModel.data.winStreak.toString()
//                                binding.matchesWon.text = userDetailedInfoModel.data.userWin.toString()
////                                    binding.shoutout.text = t.data.userData.userShoutOut
//                                binding.win.text = userDetailedInfoModel.data.userWin.toString()
//                                binding.lose.text = userDetailedInfoModel.data.userLoose.toString()

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


    private fun getMatchesRecord(off: Int, isLoadMore: Boolean) {


        val checked = ChatPost(
            5,
            off + 1,
            URLConstant.u_id!!
        )

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.getMatchesRecord(
                checked,
                object : NetworkListener<GetMatchesRecord> {
                    override fun successFul(t: GetMatchesRecord) {
                        loading.isDismiss()
                        if (t.status == 1) {


                            activity?.runOnUiThread {

                                if (t.data.tournamentRecords.isNotEmpty() || t.data.matchRecords.isNotEmpty()) {


                                    completedList.addAll(t.data.matchRecords)
                                    completedList.addAll(t.data.tournamentRecords)

                                    completedList.reverse()
                                    adapterHomeScreen.setData(completedList)

//                                if (isLoadMore) {
//                                    rvLoadMore.setLoaded()
//                                }

//                                binding.progressBar.visibility = View.GONE
                                } else {

                                    adapterHomeScreen.setData(completedList)
                                }
                            }
//                                URLConstant.check = true
                        }

                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail get match record")
                        }
                    }
                }
            )
        }

    }

    private fun initScrollListener() {
        rvLoadMore =
            RecyclerViewLoadMoreScroll(binding.recycleListcompleted.layoutManager as LinearLayoutManager)
        rvLoadMore.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                loadMore()
            }
        })
        binding.recycleListcompleted.addOnScrollListener(rvLoadMore)
    }

    private fun loadMore() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
//                binding.progressBar.visibility = View.VISIBLE
            }
            getMatchesRecord((completedList.size), true)
        }
    }


    private fun getWaitingList() {

        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.playerWaiting(
                URLConstant.u_id!!,
                object : NetworkListener<PlayerWaitingModel> {
                    override fun successFul(t: PlayerWaitingModel) {
                        loading.isDismiss()
                        if (t.status == 1) {
                            activity?.runOnUiThread {
                                if (t.data.matchRecords.isNotEmpty() || t.data.tournamentRecords.isNotEmpty()) {


                                    binding.ongoingLayout.visibility = View.VISIBLE

                                    oneToOneAndTournamentList.addAll(t.data.matchRecords)
                                    oneToOneAndTournamentList.addAll(t.data.tournamentRecords)


                                    binding.recycleListOngoingOneToOne.layoutManager =
                                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

                                    rvAdapterOnetoOne = RVAdapterOngoinOnetoOne(object : RVAdapterOngoinOnetoOne.OnItemClickListenerHandler {
                                        override fun onItemClicked(click: Any, position: Int) {
                                        }
                                    })

                                    binding.recycleListOngoingOneToOne.adapter = rvAdapterOnetoOne

                                    oneToOneAndTournamentList.reverse()

                                    rvAdapterOnetoOne.setData(oneToOneAndTournamentList)
                                }
                                else{
                                    binding.ongoingLayout.visibility = View.GONE


                                }

                            }
                        }
                    }

                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail pending request")
                        }
                    }
                }
            )
        }
    }
}