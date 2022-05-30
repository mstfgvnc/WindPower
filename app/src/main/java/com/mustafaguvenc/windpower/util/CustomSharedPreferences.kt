package com.mustafaguvenc.windpower.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class CustomSharedPreferences {
    companion object{
        private var sharedPreferences: SharedPreferences? = null
        private val PREFERENCES_TIME= "preferences_time"
        private val PREFERENCES_TIME_TURBİNE= "preferences_time_turbine"
        private val LATITUDE= "latitude"
        private val LONGITUDE= "longitude"
        private val ALTITUDE= "altitude"

        @Volatile private var instance: CustomSharedPreferences? = null
        private val lock = Any()


        operator fun invoke(context : Context) : CustomSharedPreferences =  instance ?: synchronized(lock){
            instance ?: makeCustomSharedPreferences(context).also {
                instance = it
            }
        }

        private fun makeCustomSharedPreferences(context : Context): CustomSharedPreferences{
            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context)
            return CustomSharedPreferences()
        }
    }

    fun saveTime(time : Long){
        sharedPreferences?.edit(commit =  true){
            putLong(PREFERENCES_TIME,time)

        }
    }

    fun saveTimeTurbin(time : Long){
        sharedPreferences?.edit(commit =  true){
            putLong(PREFERENCES_TIME_TURBİNE,time)

        }
    }

    fun saveLatitude(lat : Float){
        sharedPreferences?.edit(commit =  true){
            putFloat(LATITUDE, lat)

        }
    }

    fun saveLongitude(lon : Float){
        sharedPreferences?.edit(commit =  true){
            putFloat(LONGITUDE,lon)

        }
    }

    fun saveAltitude(altitude : Float){
        sharedPreferences?.edit(commit =  true){
            putFloat(ALTITUDE,altitude)

        }
    }

    fun getTime()= sharedPreferences?.getLong(PREFERENCES_TIME,0)

    fun getTimeTurbin()= sharedPreferences?.getLong(PREFERENCES_TIME_TURBİNE,0)

    fun getLatitude()= sharedPreferences?.getFloat(LATITUDE,0f)

    fun getLongitude()= sharedPreferences?.getFloat(LONGITUDE,0f)

    fun getAltitude()= sharedPreferences?.getFloat(ALTITUDE,0f)

}