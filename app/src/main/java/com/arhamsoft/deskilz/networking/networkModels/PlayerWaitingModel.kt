package com.arhamsoft.deskilz.networking.networkModels

data class PlayerWaitingModel(
    val status: Int,
    val data: OnGoingModel,
    val message: String
)
