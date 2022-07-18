package com.sdk.sdkpushexpress.repository

import android.content.Context
import android.telephony.TelephonyManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo
import com.sdk.sdkpushexpress.local_settings.SdkSettingsRepository
import com.sdk.sdkpushexpress.models.DevicesRequest
import com.sdk.sdkpushexpress.models.Events
import com.sdk.sdkpushexpress.models.EventsRequest
import com.sdk.sdkpushexpress.models.SDKDebugResponse
import com.sdk.sdkpushexpress.retrofit.RetrofitBuilder
import com.sdk.sdkpushexpress.utils.retryIO
import kotlinx.coroutines.*
import java.util.*


internal class ApiRepositoryImpl(
    private val context: Context,
    private val settingsRepository: SdkSettingsRepository
) : ApiRepository {

    private val builder = RetrofitBuilder(SDK_COMMON__URL)
    private val sdkApi = builder.sdkService
    private var h_beat: Long? = null
    private var devices_beat: Long? = null
    private var devicesJob: Job = Job()
    private var eventsJob: Job = Job()
    private var commonJob: Job = SupervisorJob()
    private val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }
    private val scope = CoroutineScope(Dispatchers.IO + commonJob + handler)

    override suspend fun requestDevices(sdkExpressId: String, fireBaseToken: String) =
        withContext(scope.coroutineContext) {
            devicesJob.cancel()
            eventsJob.cancel()
            settingsRepository.saveSdkExpressId(sdkExpressId)
            val res =
                retryIO { sdkApi.requestDevices(createDevicesRequest(sdkExpressId, fireBaseToken)) }
            h_beat = res.hbeat_intvl
            devices_beat = res.device_intvl
            repeatRequestDevices(sdkExpressId, fireBaseToken)
            repeatRequestEvents()
            res
        }

    override suspend fun updateEvents(event: Events) {
        val settings = settingsRepository.getSdkSettings()
        scope.launch {
            sdkApi.updateEvent(
                EventsRequest(
                    app_id = settings.sdk_id,
                    pxi_id = settings.pxi_id,
                    event = event.event
                )
            )
        }
    }

    override suspend fun requestDevicesDebug(
        sdkExpressId: String,
        fireBaseToken: String
    ): SDKDebugResponse {
        devicesJob.cancel()
        eventsJob.cancel()
        return withContext(scope.coroutineContext) {
            settingsRepository.saveSdkExpressId(sdkExpressId)
            val request = createDevicesRequest(sdkExpressId, fireBaseToken)
            val res = retryIO { SDKDebugResponse(request, sdkApi.requestDevices(request)) }
            h_beat = res.response.hbeat_intvl
            devices_beat = res.response.device_intvl
            repeatRequestDevices(sdkExpressId, fireBaseToken)
            repeatRequestEvents()
            res
        }
    }

    private fun repeatRequestDevices(sdkExpressId: String, fireBaseToken: String) {
        devicesJob = scope.launch {
            devices_beat?.let {
                delay(it * 1000)
                ensureActive()
                requestDevices(sdkExpressId, fireBaseToken)
            }
        }
    }

    private fun repeatRequestEvents() {
        eventsJob = scope.launch {
            h_beat?.let {
                while (isActive) {
                    delay(it * 1000)
                    ensureActive()
                    updateEvents(Events.HBEAT)
                }
            }
        }
    }


    private fun getCountryCode(): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkCountryIso.uppercase()
    }

    private suspend fun createDevicesRequest(
        sdkExpressId: String,
        fireBaseToken: String
    ): DevicesRequest {
        val addId = try {
            getAdvertisingIdInfo(context).id.orEmpty()
        } catch (e: Exception) {
            null
        }
        val sdkSettings = settingsRepository.getSdkSettings()

        return DevicesRequest(
            app_id = sdkExpressId,
            lang = Locale.getDefault().language,
            country_net = getCountryCode(),
            country_sim = getCountrySim().uppercase(),
            timezone = TimeZone.getDefault().rawOffset / 1000,
            install_ts = sdkSettings.install_ts,
            fcm = fireBaseToken,
            ad_id = addId.orEmpty(),
            pxi_id = sdkSettings.pxi_id,
            onscreen_cnt = sdkSettings.onscreen_cnt
        )
    }

    private fun getCountrySim(): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.simCountryIso
    }

    private suspend fun getWifiIp() = sdkApi.getExternalIP(EXTERNAL_IP_URL).ip

    companion object {
        private const val SDK_COMMON__URL = "https://api.scheduler-push.online/sdk/v1/"
        private const val EXTERNAL_IP_URL = "https://api.ipify.org?format=json"
    }
}