package com.sdk.sdkpushexpress.models

data class DevicesRequest(
    val app_id: String,
    val pxi_id: String,
    val fcm: String,
    val lang: String,
    val ad_id: String,
    val country_net: String,
    val country_sim: String,
    val timezone: Int,
    val install_ts: Long,
    val onscreen_cnt: Int
)
