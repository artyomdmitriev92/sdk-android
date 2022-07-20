package com.sdk.pushexpress.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.sdk.pushexpress.MainActivity

internal class SDKExpressFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        MainActivity.firebaseToken = token
    }
}