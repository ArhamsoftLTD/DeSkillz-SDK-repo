package com.arhamsoft.deskilz.networking.networkModels

import java.io.Serializable

data class GetMatchesRecordData(
    val isWin: Boolean,
    val placePosition: String,
    val positionImage: String,
    val listofOpponent: List<ListofOpponentsMatchesRecord>,
    val time: String,
    val entryFee: Long,
    val xpValueUser: Int,
    val loosingPrize: Int,
    val matchId: String,
    val matchTitle: String,
    val isTournament: Boolean,
    val tournamentName: String

):Serializable