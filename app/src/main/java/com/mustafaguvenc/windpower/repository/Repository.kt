package com.mustafaguvenc.windpower.repository

import com.mustafaguvenc.windpower.db.TurbineDao
import com.mustafaguvenc.windpower.model.ElevationModel
import com.mustafaguvenc.windpower.model.TimeModel
import com.mustafaguvenc.windpower.model.TurbineModel
import com.mustafaguvenc.windpower.model.WindModel
import com.mustafaguvenc.windpower.network.ElevationAPI
import com.mustafaguvenc.windpower.network.TimeAPI
import com.mustafaguvenc.windpower.network.TurbineAPI
import com.mustafaguvenc.windpower.network.WindSpeedsAPI
import io.reactivex.Single
import javax.inject.Inject

class Repository @Inject constructor(private val turbineAPI: TurbineAPI,
                                     private val turbineDao: TurbineDao,
                                     private val elevationAPI: ElevationAPI,
                                     private val windSpeedsAPI: WindSpeedsAPI,
                                     private val timeAPI: TimeAPI
                                     )  {

    fun getTurbineFromApi(): Single<List<TurbineModel>> {
        return turbineAPI.getTurbine()
    }

    fun getWindSpeedsFromApi(lat:String,lon:String,start_date:String,end_date:String,key:String): Single<WindModel> {
        return windSpeedsAPI.getWindSpeeds(lat, lon, start_date, end_date, key)
    }

     fun getElevationFromApi(points :String): Single<ElevationModel> {
        return elevationAPI.getElevation(points)
    }

    fun getTimeFromApi(): Single<TimeModel> {
        return timeAPI.getTime()
    }

    suspend fun insertAll(vararg turbine : TurbineModel):List<Long>{
        return turbineDao.insertAll(*turbine)
    }

    suspend fun getTurbineFromDatabase():List<TurbineModel>{
        return turbineDao.getAllTurbines()
    }

    suspend fun getTurbine(turbineId:Int):TurbineModel{
        return turbineDao.getTurbine(turbineId)
    }

    suspend fun deleteAllTurbinesFromDatabase(){
        return turbineDao.deleteAllTurbines()
    }
}