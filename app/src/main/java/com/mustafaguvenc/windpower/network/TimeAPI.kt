package com.mustafaguvenc.windpower.network

import com.mustafaguvenc.windpower.model.TimeModel
import com.mustafaguvenc.windpower.model.TurbineModel
import io.reactivex.Single
import retrofit2.http.GET

interface TimeAPI {

    // "https://worldtimeapi.org/api/timezone/Europe/Istanbul"
    @GET("api/timezone/Europe/Istanbul")
    fun getTime(): Single<TimeModel>

}