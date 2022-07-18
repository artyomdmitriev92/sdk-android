package com.sdk.sdkpushexpress.app_startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.sdk.sdkpushexpress.lifecycle.UILifecycleObserver
import com.sdk.sdkpushexpress.local_settings.SdkSettingsRepositoryImpl
import com.sdk.sdkpushexpress.repository.ApiRepositoryImpl
import com.sdk.sdkpushexpress.sdk.SDKExpress

internal class SDKExpressInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val settings = SdkSettingsRepositoryImpl(context)
        SDKExpress.sdkApi = ApiRepositoryImpl(context, settings)
        (context as? Application)?.registerActivityLifecycleCallbacks(
            UILifecycleObserver(
                settings,
                SDKExpress.sdkApi
            )
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}