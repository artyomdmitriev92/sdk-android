package com.sdk.sdkpushexpress.local_settings

interface SdkSettingsRepository {

    suspend fun updateScreenCountValue()

    suspend fun saveSdkExpressId(sdkId: String)

    suspend fun getSdkSettings(): SdkSettings
}
