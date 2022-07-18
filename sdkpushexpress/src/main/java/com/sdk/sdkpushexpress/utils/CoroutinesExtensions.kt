package com.sdk.sdkpushexpress.utils

import kotlinx.coroutines.delay
import java.io.IOException
import java.util.concurrent.TimeUnit

suspend fun <T> retryIO(
    times: Int = Int.MAX_VALUE,
    initialDelay: Long = TimeUnit.SECONDS.toMillis(2),
    maxDelay: Long = TimeUnit.SECONDS.toMillis(32),
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: IOException) {

        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}