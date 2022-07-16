package com.sdk.sdkpushexpress.app_startup

import android.content.Context
import androidx.startup.Initializer
import com.sdk.sdkpushexpress.repository.ApiRepositoryImpl
import com.sdk.sdkpushexpress.sdk.SDKExpress

internal class SDKExpressInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        SDKExpress.sdkApi = ApiRepositoryImpl(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}