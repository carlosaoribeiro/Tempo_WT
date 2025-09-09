package com.carlosribeiro.tempo_wt.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://api.openweathermap.org/"

    private val client = OkHttpClient.Builder().build()

    fun <T> create(service: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }
}
