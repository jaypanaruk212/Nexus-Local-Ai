package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

  private val moshi: Moshi by lazy {
    Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .writeTimeout(60, TimeUnit.SECONDS)
      .addInterceptor(
        HttpLoggingInterceptor().apply {
          level = HttpLoggingInterceptor.Level.BODY
        }
      )
      .build()
  }

  val huggingFaceApi: HuggingFaceApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://huggingface.co/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(HuggingFaceApiService::class.java)
  }

  val webSearchApi: WebSearchApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://html.duckduckgo.com/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(WebSearchApiService::class.java)
  }
}
