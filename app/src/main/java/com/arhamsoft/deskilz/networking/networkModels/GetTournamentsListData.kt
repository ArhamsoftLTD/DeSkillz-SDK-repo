package com.arhamsoft.deskilz.networking.networkModels


data class GetTournamentsListData(
    val tournamentId: Long?= 0,
    val startDate: String?="",
    val endDate: String?="",
    val isPractice: Boolean=false,
    val winningPrize: String= "0",
    val loosingPrize: Int?=0,
    val gamePlay: Int?=0,
    val tournamentID: String?="",
    val tournamentName: String?="",
    val tournamentImage: String?="",
    val entryFee: Int?=0,
    val playerCount: Int?=0)
