package com.sdk.sdkpushexpress.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.*
import javax.net.ssl.*


internal class RetrofitBuilder {

    private var retrofit: Retrofit
    var sdkService: ApiService
        private set

    init {
        retrofit = Retrofit.Builder()
            .client(getUnsafeOkHttpClient())
            .baseUrl("https://google.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sdkService = retrofit.create(ApiService::class.java)
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + Arrays.toString(
                trustManagers
            )
        }
        val trustManager = trustManagers[0] as X509TrustManager
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .hostnameVerifier { hostname, session -> true }
            .sslSocketFactory(sslSocketFactory, trustManager).build()
    }
}