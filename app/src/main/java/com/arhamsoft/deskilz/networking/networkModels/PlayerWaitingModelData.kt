package com.arhamsoft.deskilz.networking.networkModels

data class PlayerWaitingModelData(
    val _id: String?="",
    val tournamentId: String?="",
    val remainingTime: String?="",
    val waitingFor: String?="",
    val entryFee: String?="",
    val winningPrize: String?="",
    val previousScore: String?="",
    val playerCount: Int?=0,
    val tournamentImage: String?="",
    val tournamentName: String?="",
    val isPlayed: Boolean = false
)
