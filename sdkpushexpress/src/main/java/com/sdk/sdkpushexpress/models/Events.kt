package com.sdk.sdkpushexpress.models

enum class Events(val event: String) {
    HBEAT("hbeat"),
    ONSCREEN("onscreen"),
    BACKGROUND("background"),
    CLOSED("closed")
}