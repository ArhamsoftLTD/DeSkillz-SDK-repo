package com.arhamsoft.deskilz.ui.fragment.navBarFragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.withCreated
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alphaCareInc.app.room.UserDatabase
import com.arhamsoft.deskilz.R
import com.arhamsoft.deskilz.databinding.DialogDepositBinding
import com.arhamsoft.deskilz.databinding.DialogHead2headBinding
import com.arhamsoft.deskilz.ui.fragment.HeadToHeadDetailFragment
import com.arhamsoft.deskilz.databinding.FragmentPlayScreenBinding
import com.arhamsoft.deskilz.domain.listeners.NetworkListener
import com.arhamsoft.deskilz.domain.repository.NetworkRepo
import com.arhamsoft.deskilz.networking.networkModels.*
import com.arhamsoft.deskilz.networking.retrofit.URLConstant
import com.arhamsoft.deskilz.ui.adapter.*
import com.arhamsoft.deskilz.utils.LoadingDialog
import com.arhamsoft.deskilz.utils.StaticFields
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Url

class PlayScreenFragment : Fragment() {

    private lateinit var binding: FragmentPlayScreenBinding
    private lateinit var navController: NavController
    lateinit var loading: LoadingDialog
    private var headToHeadList: ArrayList<GetTournamentsListData> = ArrayList()
    private var practiceList: ArrayList<GetTournamentsListData> = ArrayList()
    private var eventList: ArrayList<EventsModelData> = ArrayList()
    private var bracketsList: ArrayList<GetTournamentsListData> = ArrayList()
    private var oneToOneList: ArrayList<GetTournamentsListData> = ArrayList()
    private lateinit var rvAdapterPractice: RVAdapterPractice
    private lateinit var rvAdapterH2H: RVAdapterHeadToHead
    private lateinit var rvAdapterBrackets: RVAdapterBrackets
    private lateinit var rvAdapterEvents: RVAdapterEvents
    private lateinit var rvAdapterOneToOne: RVAdapterOne2One


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayScreenBinding.inflate(LayoutInflater.from(context))
        navController = findNavController()
        loading = LoadingDialog(requireContext() as Activity)

        if (!(StaticFields.isNetworkConnected(requireContext()))) {
            StaticFields.toastClass("Check your network connection")
        } else {
            loading.startLoading()
//        URLConstant.check = false
            getTournaments()
//            getEvents()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initClicks()

//        CoroutineScope(Dispatchers.IO).launch {
//            val userId = UserDatabase.getDatabase(requireContext()).userDao().getUser()
//            if (userId != null) {
//                u_id = userId.userId
//            }
//        }


        binding.waiting.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardActivity_to_pendingRequestFragment)
        }


//        binding.recyclerViewEvents.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


//        rvAdapterEvents =
//            RVAdapterEvents(requireContext(), eventList, object : RVAdapterEvents.OnItemClick {
//                override fun onClick(click: EventsModelData, position: Int) {
////                    val bundle = bundleOf()
////                    bundle.putSerializable("GET_MATCHES_OBJ",click)
////                    URLConstant.eventId = click.eventID
////                    GetTournamentsListData(false,click.gamePlay,click.eventID,click.eventName,"","",click.entryFee,click.playerCount.toLong(),"","","",
////                        listOf(PrizesModel("","","","","",0L)))
////                    findNavController().navigate(R.id.action_dashboardActivity_to_findCompetitiveFragment)
//
////                    showDialog()
//
//                }
//            })
//
//        binding.recyclerViewEvents.adapter = rvAdapterEvents


    }

    private fun getEvents() {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.getEvent(
                object : NetworkListener<EventsModel> {
                    override fun successFul(t: EventsModel) {
                        loading.isDismiss()
                        if (t.status == 1) {

                            activity?.runOnUiThread {

                                if (t.data.isNotEmpty()) {

//                                    binding.eventLayout.visibility = View.GONE

                                    eventList.addAll(t.data)

                                    rvAdapterEvents.addData(eventList)
                                }
//                            URLConstant.check = true
                            }

                        } else {
                            activity?.runOnUiThread {
                                StaticFields.toastClass(" get events status 0 ")
                            }
                        }


                    }

//                            activity?.runOnUiThread {
//                                rvAdapter.addData(marketList)
//                                Toast.makeText(requireContext(), ""+marketLoadMoreList.size, Toast.LENGTH_SHORT).show()
//                                rvAdapterAllCategory.addData(marketLoadMoreList)
//                            }


                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail get events")
                        }
                    }
                }
            )
        }

    }


    private fun checkTournamentParticipation(click: GetTournamentsListData) {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.checkTournamentParticipation(
                URLConstant.u_id!!,
                if (URLConstant.oneToOne)
                {""}
                else
                {click.tournamentID!!},
                object : NetworkListener<ForgotModel> {
                    override fun successFul(t: ForgotModel) {
                        loading.isDismiss()

                        activity?.runOnUiThread {

                            if (!t.success) {


                                val obj = Gson().toJson(click)

                                val bundle = bundleOf()
                                bundle.putSerializable("GET_MATCHES_OBJ", obj)
//                    URLConstant.tournamentId = click.tournamentID
//                    URLConstant.Fee = click.entryFee
//                    URLConstant.player_count = click.playerCount
//                    loading.startLoading()

                                findNavController().navigate(
                                    R.id.action_dashboardActivity_to_findCompetitiveFragment,
                                    bundle
                                )

                            } else {
                                StaticFields.toastClass("Your Previous Tournament result hasn't announced yet.")
                            }

                        }
                    }

//                            activity?.runOnUiThread {
//                                rvAdapter.addData(marketList)
//                                Toast.makeText(requireContext(), ""+marketLoadMoreList.size, Toast.LENGTH_SHORT).show()
//                                rvAdapterAllCategory.addData(marketLoadMoreList)
//                            }


                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail check tourn part")
                        }
                    }
                }
            )
        }

    }


    private fun getTournaments() {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkRepo.getMatches(
                object : NetworkListener<GetTournaments> {
                    override fun successFul(t: GetTournaments) {
                        loading.isDismiss()
                        if (t.status == 1) {

                            activity?.runOnUiThread {
                                binding.mainTournament.visibility = View.VISIBLE
                                tournamentsApiDataSet(t)
                            }

//                            URLConstant.check = true

                        }
                    }

//                            requireActivity().runOnUiThread {
//                                rvAdapter.addData(marketList)
//                                Toast.makeText(requireContext(), ""+marketLoadMoreList.size, Toast.LENGTH_SHORT).show()
//                                rvAdapterAllCategory.addData(marketLoadMoreList)
//                            }


                    override fun failure() {
                        loading.isDismiss()

                        activity?.runOnUiThread {
                            StaticFields.toastClass("Api syncing fail get tournaments")
                        }
                    }
                }
            )
        }

    }



    private fun tournamentsApiDataSet(t: GetTournaments) {
        if (t.data.Practice.isNotEmpty()) {
            binding.practiceLayout.visibility = View.VISIBLE

            practiceList.addAll(t.data.Practice)
            setpracticeAdapter()
        }

        if (t.data.oneToOne.isNotEmpty()) {
            binding.onetooneLayout.visibility = View.VISIBLE
            oneToOneList.addAll(t.data.oneToOne)

            setOne2OneAdapter()


        }

        if (t.data.headToHead.isNotEmpty()) {

            binding.headtoheadLayout.visibility = View.VISIBLE

            headToHeadList.addAll(t.data.headToHead)

            setH2HAdapetr()

        }
        if (t.data.brackets.isNotEmpty()) {

            binding.bracketsLayout.visibility = View.VISIBLE

            bracketsList.addAll(t.data.brackets)

            setBracketsAdapter()

        }
    }


    fun setOne2OneAdapter() {

        binding.recyclerViewOne2One.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        rvAdapterOneToOne =
            RVAdapterOne2One(requireContext(), oneToOneList, object : RVAdapterOne2One.OnItemClick {
                override fun onClick(click: GetTournamentsListData, position: Int) {
                    loading.startLoading()

                    URLConstant.oneToOne = true


                    checkTournamentParticipation(click)

                }
            })

        binding.recyclerViewOne2One.adapter = rvAdapterOneToOne

        rvAdapterOneToOne.addData(oneToOneList)

    }

    fun setBracketsAdapter() {

        binding.recyclerViewBrackets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvAdapterBrackets = RVAdapterBrackets(
            requireContext(),
            bracketsList,
            object : RVAdapterBrackets.OnItemClick {
                override fun onClick(click: GetTournamentsListData, position: Int) {
                    loading.startLoading()
                    URLConstant.oneToOne = false

                    checkTournamentParticipation(click)


//                    showDialog()


                }
            })

        binding.recyclerViewBrackets.adapter = rvAdapterBrackets

        rvAdapterBrackets.addData(bracketsList)

    }

    fun setpracticeAdapter() {

        binding.recyclerViewPractice.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvAdapterPractice = RVAdapterPractice(
            requireContext(),
            practiceList,
            object : RVAdapterPractice.OnItemClick {
                override fun onClick(click: GetTournamentsListData, position: Int) {
                    loading.startLoading()
                    URLConstant.oneToOne = false

                    checkTournamentParticipation(click)

                }
            })

        binding.recyclerViewPractice.adapter = rvAdapterPractice

        rvAdapterPractice.addData(practiceList)


    }

    fun setH2HAdapetr() {


        binding.recyclerViewHeadtoHead.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvAdapterH2H = RVAdapterHeadToHead(
            requireContext(),
            headToHeadList,
            object : RVAdapterHeadToHead.OnItemClick {
                override fun onClick(click: GetTournamentsListData, position: Int) {
                    loading.startLoading()
                    URLConstant.oneToOne = false

                    checkTournamentParticipation(click)

                }
            })

        binding.recyclerViewHeadtoHead.adapter = rvAdapterH2H

        rvAdapterH2H.addData(headToHeadList)
    }


/*
    private fun initClicks() {

        binding.h2h.setOnClickListener {
            navController.navigate(R.id.action_dashboardActivity_to_findCompetitiveFragment)
            //startActivity(Intent(requireContext(), HeadToHeadDetailFragment::class.java))
        }
        binding.realTimeMatches.setOnClickListener {
            navController.navigate(R.id.action_dashboardActivity_to_findCompetitiveFragment)
            //startActivity(Intent(requireContext(), HeadToHeadDetailFragment::class.java))
        }
        binding.practiceMatch.setOnClickListener {
            navController.navigate(R.id.action_dashboardActivity_to_findCompetitiveFragment)
            //startActivity(Intent(requireContext(), HeadToHeadDetailFragment::class.java))
        }


    }*/
}





