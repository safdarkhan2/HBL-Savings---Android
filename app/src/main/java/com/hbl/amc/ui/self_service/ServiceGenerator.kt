package com.hbl.amc.ui.self_service

import com.hbl.amc.BuildConfig
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)

    private val builder = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>?): S {
        val retrofit: Retrofit =
            builder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }
}