package com.hbl.amc.ui.self_service

import com.google.gson.GsonBuilder
import com.hbl.amc.BuildConfig.BASE_URL
import com.hbl.amc.api_service.FileDownloadService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/*object DownloadApiClient {
    fun getClient(): FileDownloadService {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(FileDownloadService::class.java)
    }
}*/

class DownloadApiClient {

    companion object {

        private val retrofit: Retrofit


        init {

            val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build()

            val gson = GsonBuilder().setLenient().create()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                 .addConverterFactory(GsonConverterFactory.create(gson))// - you can add this line, I think.
                .build()
        }

        fun getRetrofit(): Retrofit = retrofit
    }
}