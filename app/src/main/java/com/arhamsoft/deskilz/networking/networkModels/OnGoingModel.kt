package com.arhamsoft.deskilz.networking.networkModels

data class OnGoingModel(
    val tournamentRecords: List<PlayerWaitingModelData>,
    val matchRecords: List<PlayerWaitingModelData>
)
