package com.arhamsoft.deskilz.networking.networkModels

data class UserDetailedInfoModelData(
    val deskillzLevel: Int,
    val deskillzCoin: Int,
    val earnedTrophies: List<EarnedTrophiesModel>,
    val allTrophies: List<AllTrophiesModel>,
    val currentGameRank: Int,
    val currentGameImag: String,

    val progressXp: Int,

    val userWin: Int,
    val userLoose: Int,
    val winStreak: Int,
//    val userPlayedGamesInfo: List<Any?>,
    val userData: UpdateProfileModelData
)
