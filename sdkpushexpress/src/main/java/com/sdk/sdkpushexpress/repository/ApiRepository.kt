package com.sdk.sdkpushexpress.repository


import com.sdk.sdkpushexpress.models.DevicesRequest
import com.sdk.sdkpushexpress.models.DevicesResponse

interface ApiRepository {
    suspend fun requestDevices(fireBaseToken: String): DevicesResponse

    suspend fun requestDevicesPair(
        fireBaseToken: String,
        callback: (DevicesRequest) -> Unit
    ): DevicesResponse
}