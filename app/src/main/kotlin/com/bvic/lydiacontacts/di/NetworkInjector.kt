package com.bvic.lydiacontacts.di

import android.content.Context
import com.bvic.lydiacontacts.BuildConfig
import com.bvic.lydiacontacts.core.network.AndroidNetworkMonitor
import com.bvic.lydiacontacts.core.network.NetworkMonitor
import com.bvic.lydiacontacts.data.remote.api.RandomUserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkInjector {
    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS),
            )
        }
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit.Builder =
        Retrofit
            .Builder()
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)

    @Singleton
    @Provides
    fun provideRandomUserApi(retrofitBuilder: Retrofit.Builder): RandomUserApi =
        retrofitBuilder
            .baseUrl("https://randomuser.me/")
            .build()
            .create(RandomUserApi::class.java)

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context,
    ): NetworkMonitor =
        AndroidNetworkMonitor(
            context = context,
        )
}
