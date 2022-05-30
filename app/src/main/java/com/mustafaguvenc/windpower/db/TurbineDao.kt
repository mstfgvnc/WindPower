package com.mustafaguvenc.windpower.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mustafaguvenc.windpower.model.TurbineModel

@Dao
interface TurbineDao {

    @Insert
    suspend fun insertAll(vararg turbine : TurbineModel) : List<Long>

    @Query("SELECT * FROM turbinemodel")
    suspend fun getAllTurbines():List<TurbineModel>

    @Query("SELECT * FROM turbinemodel WHERE uuid = :turbineId")
    suspend fun getTurbine(turbineId : Int) : TurbineModel


    @Query("DELETE FROM turbinemodel")
    suspend fun deleteAllTurbines()
}