package com.sdk.sdkpushexpress.retrofit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import com.sdk.sdkpushexpress.models.DevicesRequest
import com.sdk.sdkpushexpress.models.DevicesResponse
import com.sdk.sdkpushexpress.models.IPResponse

internal interface ApiService {

    @POST
    suspend fun requestDevices(@Url url: String, @Body request: DevicesRequest): DevicesResponse

    @GET
    suspend fun getExternalIP(@Url url: String): IPResponse
}