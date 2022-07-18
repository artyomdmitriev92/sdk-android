package com.sdk.sdkpushexpress.models

data class EventsRequest(
    val app_id: String,
    val pxi_id: String,
    val event: String
)
