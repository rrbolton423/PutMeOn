package com.romellbolton.putmeon.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log

class AppStatus {
    var connectivityManager: ConnectivityManager? = null
    private var connected = false
    val isOnline: Boolean
        get() {
            try {
                connectivityManager = context
                        ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager!!.activeNetworkInfo
                connected = networkInfo != null && networkInfo.isAvailable &&
                        networkInfo.isConnected
                return connected
            } catch (e: Exception) {
                println("CheckConnectivity Exception: " + e.message)
                Log.v("connectivity", e.toString())
            }
            return connected
        }

    companion object {
        private val instance = AppStatus()
        private var context: Context? = null

        @JvmStatic
        fun getInstance(ctx: Context): AppStatus {
            context = ctx.applicationContext
            return instance
        }
    }
}