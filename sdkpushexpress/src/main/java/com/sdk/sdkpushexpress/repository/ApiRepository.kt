package com.sdk.sdkpushexpress.repository


import com.sdk.sdkpushexpress.models.DevicesResponse
import com.sdk.sdkpushexpress.models.Events
import com.sdk.sdkpushexpress.models.SDKDebugResponse

interface ApiRepository {
    suspend fun requestDevices(sdkExpressId: String, fireBaseToken: String): DevicesResponse

    suspend fun updateEvents(event: Events)

    suspend fun requestDevicesDebug(sdkExpressId: String, fireBaseToken: String): SDKDebugResponse
}