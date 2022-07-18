package com.sdk.sdkpushexpress.retrofit

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import com.sdk.sdkpushexpress.models.DevicesRequest
import com.sdk.sdkpushexpress.models.DevicesResponse
import com.sdk.sdkpushexpress.models.EventsRequest
import com.sdk.sdkpushexpress.models.IPResponse

internal interface ApiService {

    @POST("devices?origin=droid")
    suspend fun requestDevices( @Body request: DevicesRequest): DevicesResponse

    @POST("events?origin=droid")
    suspend fun updateEvent(@Body request: EventsRequest)

    @GET
    suspend fun getExternalIP(@Url url: String): IPResponse
}