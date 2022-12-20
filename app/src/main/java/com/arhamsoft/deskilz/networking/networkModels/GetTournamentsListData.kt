package com.arhamsoft.deskilz.networking.networkModels


data class GetTournamentsListData(
    val tournamentId: Long,
    val startDate: String,
    val endDate: String,
    val isPractice: Boolean,
    val winningPrize: Any? = null,
    val loosingPrize: Int,
    val gamePlay: Int,
    val tournamentID: String,
    val tournamentName: String,
    val tournamentImage: String,
    val entryFee: Int,
    val playerCount: Int)
