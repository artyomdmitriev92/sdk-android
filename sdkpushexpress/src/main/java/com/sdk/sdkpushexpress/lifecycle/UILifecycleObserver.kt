package com.sdk.sdkpushexpress.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sdk.sdkpushexpress.local_settings.SdkSettingsRepository
import com.sdk.sdkpushexpress.models.Events
import com.sdk.sdkpushexpress.repository.ApiRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class UILifecycleObserver(
    private val settingsRepository: SdkSettingsRepository,
    private val sdkApi: ApiRepository
) :
    Application.ActivityLifecycleCallbacks {

    val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        (activity as? AppCompatActivity)?.let {
            it.lifecycleScope.launch(handler) {
                settingsRepository.updateScreenCountValue()
                sdkApi.updateEvents(Events.ONSCREEN)
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        (activity as? AppCompatActivity)?.let {
            it.lifecycleScope.launch(handler) {
                sdkApi.updateEvents(Events.BACKGROUND)
            }
        }
    }
}