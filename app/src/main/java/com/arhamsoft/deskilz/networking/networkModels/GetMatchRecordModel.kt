package com.arhamsoft.deskilz.networking.networkModels

data class GetMatchRecordModel(
    val tournamentRecords: List<GetMatchesRecordData>,
    val matchRecords: List<GetMatchesRecordData>
)
