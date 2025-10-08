package com.doctor.eprescription.di.remote

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.doctor.eprescription.BuildConfig
import com.doctor.eprescription.data.remote.ApiNames
import com.doctor.eprescription.data.remote.ApiService
import com.doctor.eprescription.data.remote.HeaderValues
import com.doctor.eprescription.data.remote.HeadersNames
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(context: Context): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
            .callTimeout(25, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(logger)
            .addInterceptor(ChuckerInterceptor(context))
            .addInterceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                    .header(HeadersNames.LANGUAGE, HeaderValues.LANGUAGE)
                    .header(HeadersNames.CHANNEL, HeaderValues.CHANNEL)
                    .method(original.method, original.body)
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiNames.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            ).build()
    }

    @Singleton
    @Provides
    fun provideApiClient(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


}