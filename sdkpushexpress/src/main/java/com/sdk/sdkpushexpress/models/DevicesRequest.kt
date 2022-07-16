package com.sdk.sdkpushexpress.models

data class DevicesRequest(
    val app_id: String,
    val fcm: String,
    val lang: String,
    val ad_id: String,
    val country: String,
    val country_sim: String,
    val ip: String,
    val timezone: Int,
    val install_ts: Long
)
