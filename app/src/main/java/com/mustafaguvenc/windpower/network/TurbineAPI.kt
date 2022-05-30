package com.mustafaguvenc.windpower.network

import com.mustafaguvenc.windpower.model.TurbineModel
import io.reactivex.Single
import retrofit2.http.GET

interface TurbineAPI {

    // https://gist.githubusercontent.com/mstfgvnc/1276e861a636205d26910e6573e2b2bb/raw/620d8be51893d123a3182479149dad53e9c29a5d/Wind%2520Turbine%2520Models
    @GET("mstfgvnc/1276e861a636205d26910e6573e2b2bb/raw/142559caaf002310f48dbd31a06f347670062230/Wind%2520Turbine%2520Models")
    fun getTurbine(): Single<List<TurbineModel>>

}