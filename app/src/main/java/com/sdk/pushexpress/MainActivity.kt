package com.sdk.pushexpress

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.onesignal.OneSignal
import com.sdk.pushexpress.databinding.ActivityMainBinding
import com.sdk.sdkpushexpress.repository.ApiRepository
import com.sdk.sdkpushexpress.sdk.SDKExpress

class MainActivity : AppCompatActivity() {

    private var viewModel: MainViewModel? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFirebase(this)
        initOneSignal(this)
        initViewModel()
    }

    private fun initFirebase(context: Context) {
        FirebaseApp.initializeApp(context)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            firebaseToken = token
        })
    }

    private fun initOneSignal(context: Context) {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(context)
        OneSignal.setAppId(ONESIGNAL_ID)
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(this, Factory(SDKExpress.sdkApi))[MainViewModel::class.java]
        viewModel?.let { vm ->
            binding.button.setOnClickListener {
                binding.code.text = ""
                binding.requestInfo.text = ""
                vm.onButtonClick(firebaseToken ?: " ")
            }
            vm.code.observe(this) { binding.code.text = it }
            vm.request.observe(this) { binding.requestInfo.text = it.toString() }
            vm.showProgress.observe(this) {
                binding.progress.visibility = if (it) VISIBLE else GONE
            }
        }
    }

    private class Factory(private val repo: ApiRepository) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repo) as T
        }
    }

    companion object {
        private const val ONESIGNAL_ID = "1548cef1-a8c8-49e5-a088-8f12a5497d47"
        var firebaseToken: String? = null
    }
}