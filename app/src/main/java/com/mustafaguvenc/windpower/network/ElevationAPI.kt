package com.mustafaguvenc.windpower.network

import com.mustafaguvenc.windpower.model.ElevationModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface  ElevationAPI {
//https://api.airmap.com/elevation/v1/ele?points={Array of LatLng Points}

    @GET("elevation/v1/ele?")
    fun getElevation(@Query("points") points : String): Single<ElevationModel>

}