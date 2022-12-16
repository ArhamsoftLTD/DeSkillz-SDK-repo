package com.arhamsoft.deskilz.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arhamsoft.deskilz.databinding.FragmentTrophyScreenDetailsBinding
import com.arhamsoft.deskilz.networking.networkModels.AllTrophiesModel
import com.arhamsoft.deskilz.networking.networkModels.EarnedTrophiesModel
import com.arhamsoft.deskilz.ui.adapter.AdapterAllTrophies
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TrophyScreenDetailFragment : Fragment() {

    private lateinit var binding: FragmentTrophyScreenDetailsBinding
    lateinit var rvAdapter: AdapterAllTrophies
    var trophyList: ArrayList<AllTrophiesModel> = ArrayList()
    var earnedtrophyList: ArrayList<EarnedTrophiesModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrophyScreenDetailsBinding.inflate(layoutInflater)


        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.allTrophieRecycleList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        rvAdapter = AdapterAllTrophies(object : AdapterAllTrophies.OnItemClickListenerHandler {
            override fun onItemClicked(click: AllTrophiesModel, position: Int) {


            }
        })

        binding.allTrophieRecycleList.adapter = rvAdapter

        val bundle = arguments
        if (bundle != null) {

            val obj = bundle.getString("allTrophyList").toString()
            val obj2 = bundle.getString("earnedTrophyList").toString()

            val alltrophy = getAppsListInPref(obj)


            trophyList.addAll(alltrophy)
            rvAdapter.setData(trophyList)

        }


        return binding.root



    }
    fun getAppsListInPref(model: String): ArrayList<AllTrophiesModel> {
        val type: Type = object : TypeToken<ArrayList<AllTrophiesModel?>?>() {}.type
        return Gson().fromJson(model, type)
    }
    fun getAppsListInPref2(model: String): ArrayList<EarnedTrophiesModel> {
        val type: Type = object : TypeToken<ArrayList<EarnedTrophiesModel?>?>() {}.type
        return Gson().fromJson(model, type)
    }
}