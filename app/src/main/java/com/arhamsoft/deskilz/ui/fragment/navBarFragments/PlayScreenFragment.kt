package com.arhamsoft.deskilz.ui.fragment.navBarFragments

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
import com.arhamsoft.deskilz.R
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
            getTournaments()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.waiting.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardActivity_to_pendingRequestFragment)
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

                                findNavController().navigate(
                                    R.id.action_dashboardActivity_to_findCompetitiveFragment,
                                    bundle
                                )

                            } else {
                                StaticFields.toastClass("Your Previous Tournament result hasn't announced yet.")
                            }

                        }
                    }

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

                        }
                    }

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
                    URLConstant.oneToOne = false

                    val obj = Gson().toJson(click)

                    val bundle = bundleOf()
                    bundle.putSerializable("GET_MATCHES_OBJ", obj)

                    findNavController().navigate(
                        R.id.action_dashboardActivity_to_findCompetitiveFragment,
                        bundle
                    )

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





