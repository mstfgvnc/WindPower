package com.mustafaguvenc.windpower.di

import com.mustafaguvenc.windpower.network.ElevationAPI
import com.mustafaguvenc.windpower.network.TimeAPI
import com.mustafaguvenc.windpower.network.TurbineAPI
import com.mustafaguvenc.windpower.network.WindSpeedsAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private  val BASE_URL = "https://gist.githubusercontent.com/"
    private  val BASE_URL2 = "https://api.airmap.com"
    private  val BASE_URL3 = "https://history.openweathermap.org"
    private  val BASE_URL4 = "https://worldtimeapi.org"

    @Provides
    @Singleton
    fun provideRetrofit(): TurbineAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TurbineAPI::class.java)

    }

    @Provides
    @Singleton
    fun provideRetrofitForElevation(): ElevationAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL2)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ElevationAPI::class.java)

    }

    @Provides
    @Singleton
    fun provideRetrofitForWindSpeeds(): WindSpeedsAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL3)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WindSpeedsAPI::class.java)

    }

    @Provides
    @Singleton
    fun provideRetrofitForTime(): TimeAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL4)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TimeAPI::class.java)

    }

}