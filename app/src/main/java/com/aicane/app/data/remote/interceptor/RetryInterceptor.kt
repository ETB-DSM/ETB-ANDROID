package com.aicane.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class RetryInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            val response = chain.proceed(request)
            if (response.code in 500..599) {
                response.close()
                chain.proceed(request)
            } else {
                response
            }
        } catch (e: IOException) {
            chain.proceed(request)
        }
    }
}
