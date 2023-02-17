package com.arhamsoft.deskilz.services

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

object SocketHandler {

     var mSocket: Socket? = null

    @Synchronized
    fun setSocket() {
        try {
            val options = IO.Options()
            options.reconnection = true //reconnection
            options.forceNew = true

            options.transports = arrayOf("websocket")
            options.path = "/socket.io/?EIO=4&"

            mSocket = IO.socket("https://deskillz-dev.arhamsoft.org/",options)


        } catch (e: Exception) {
            Log.e("exception", "setSocket: ")
        }
    }

    @Synchronized
    fun getSocket(): Socket? {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket?.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket?.disconnect()
    }
}