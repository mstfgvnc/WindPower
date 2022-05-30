package com.mustafaguvenc.windpower.network

import com.mustafaguvenc.windpower.model.WindModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WindSpeedsAPI {

    @GET("/data/2.5/history/city?")
    fun getWindSpeeds(@Query("lat") lat : String,
                      @Query("lon") lon : String,
                      @Query("start") start : String
                      ,@Query("end") end : String
                      ,@Query("appid") appid : String): Single<WindModel>

}

// https://history.openweathermap.org/data/2.5/history/city?lat=41.0096334&lon=28.9651646&start=1600041600&end=1631446458&appid=xxxx