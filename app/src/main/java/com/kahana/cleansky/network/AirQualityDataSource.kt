package com.kahana.cleansky.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AirQualityDataSource {
    private const val auth_token = "8e63d6d1269a09b5e9ab240a31cadee12b98a064"
    private const val BASE_URL = "https://api.waqi.info/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val url = chain
                            .request()
                            .url()
                            .newBuilder()
                            .addQueryParameter("token", auth_token)
                            .build()
                        chain.proceed(chain.request().newBuilder().url(url).build())
                    }
                    .build()
            )
            .build()
    }

    val apiService: AirQualityApi = getRetrofit().create(AirQualityApi::class.java)

}