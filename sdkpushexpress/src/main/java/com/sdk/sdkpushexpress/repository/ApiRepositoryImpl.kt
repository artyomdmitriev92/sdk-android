package com.sdk.sdkpushexpress.repository

import android.content.Context
import android.telephony.TelephonyManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.sdk.sdkpushexpress.models.DevicesRequest
import com.sdk.sdkpushexpress.models.DevicesResponse
import com.sdk.sdkpushexpress.retrofit.RetrofitBuilder
import java.util.*


internal class ApiRepositoryImpl(private val context: Context) : ApiRepository {

    private val builder = RetrofitBuilder()
    private val sdkApi = builder.sdkService

    override suspend fun requestDevices(fireBaseToken: String): DevicesResponse {
        return sdkApi.requestDevices(DEVICES_URL, createDevicesRequest(fireBaseToken))
    }

    override suspend fun requestDevicesPair(
        fireBaseToken: String,
        callback: (DevicesRequest) -> Unit
    ): DevicesResponse {
        val request = createDevicesRequest(fireBaseToken)
        callback.invoke(request)
        return sdkApi.requestDevices(DEVICES_URL, request)
    }

    private fun getCountryCode(): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkCountryIso.uppercase()
    }

    private suspend fun createDevicesRequest(fireBaseToken: String): DevicesRequest {
        return withContext(Dispatchers.IO) {

            val addId = try {
                AdvertisingIdClient.getAdvertisingIdInfo(context).id.orEmpty()
            } catch (e: Exception) {
                null
            }

            DevicesRequest(
                app_id = context.packageName,
                lang = Locale.getDefault().language,
                country = getCountryCode(),
                country_sim = getCountrySim(),
                timezone = TimeZone.getDefault().rawOffset,
                install_ts = Calendar.getInstance().time.time / 1000L,
                ip = getWifiIp(),
                fcm = fireBaseToken,
                ad_id = addId.orEmpty()
            )
        }
    }

    private fun getCountrySim(): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.simCountryIso
    }

    private suspend fun getWifiIp() = sdkApi.getExternalIP(EXTERNAL_IP_URL).ip

    companion object {
        private const val DEVICES_URL = "https://api.push.express/v1/devices"
        private const val EXTERNAL_IP_URL = "https://api.ipify.org?format=json"
    }
}
