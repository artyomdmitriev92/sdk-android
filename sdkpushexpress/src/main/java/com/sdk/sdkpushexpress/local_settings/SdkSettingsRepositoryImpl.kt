package com.sdk.sdkpushexpress.local_settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.*

class SdkSettingsRepositoryImpl(private val context: Context) : SdkSettingsRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sdk.pushexpress")
    private val installTs = longPreferencesKey(INSTALL_TS)
    private val pxiId = stringPreferencesKey(PXI_ID)
    private val onScreenCnt = intPreferencesKey(ONSCREEN_CNT)
    private val sdkId = stringPreferencesKey(SDK_ID)

    init {
        runBlocking {
            if (isSdkWasNotInitialized()) {
                context.dataStore.edit { settings ->
                    settings[onScreenCnt] = 1
                    settings[installTs] = System.currentTimeMillis() / 1000
                    settings[pxiId] = UUID.randomUUID().toString()
                    settings[sdkId] = ""
                }
            }
        }
    }

    override suspend fun updateScreenCountValue() {
        context.dataStore.edit { settings ->
            val currentCounterValue = settings[onScreenCnt] ?: 0
            settings[onScreenCnt] = currentCounterValue + 1
        }
    }

    override suspend fun saveSdkExpressId(sdkId: String) {
        context.dataStore.edit { settings ->
            settings[this.sdkId] = sdkId
        }
    }

    override suspend fun getSdkSettings(): SdkSettings {
        return context.dataStore.data.map {
            SdkSettings(
                it[installTs] ?: 0,
                it[pxiId].orEmpty(),
                it[onScreenCnt] ?: 0,
                it[sdkId].orEmpty()
            )
        }.first()
    }

    private suspend fun isSdkWasNotInitialized() = context.dataStore.data.first()[installTs] == null

    companion object {
        private const val INSTALL_TS = "install_ts"
        private const val PXI_ID = "pxi_id"
        private const val ONSCREEN_CNT = "onscreen_cnt"
        private const val SDK_ID = "sdk_id"
    }
}