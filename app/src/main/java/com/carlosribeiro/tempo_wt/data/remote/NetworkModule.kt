package com.carlosribeiro.tempo_wt.data.remote

import com.carlosribeiro.tempo_wt.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/"

    // Interceptor para adicionar a API key e configs em toda request
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val originalUrl = original.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("appid", BuildConfig.OWM_API_KEY) // chave da API
            .addQueryParameter("units", "metric")                // Celsius
            .addQueryParameter("lang", "pt_br")                  // Português
            .build()

        val newRequest = original.newBuilder().url(newUrl).build()
        chain.proceed(newRequest)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}
