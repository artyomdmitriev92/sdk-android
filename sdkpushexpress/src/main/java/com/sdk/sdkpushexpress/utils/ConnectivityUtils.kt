package com.sdk.sdkpushexpress.utils

import android.content.Context
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


internal object ConnectivityUtils {

    fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    fun isConnectedWifi(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        } else {
            val info = getNetworkInfo(context)
            info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
        }
    }

    fun isConnectedMobile(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
        } else {
            val info = getNetworkInfo(context)
            info != null && info.isConnected && info.type == ConnectivityManager.TYPE_MOBILE
        }
    }
}