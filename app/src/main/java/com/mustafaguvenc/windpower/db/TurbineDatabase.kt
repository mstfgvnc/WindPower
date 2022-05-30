package com.mustafaguvenc.windpower.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mustafaguvenc.windpower.model.TurbineModel

@Database(entities = arrayOf(TurbineModel::class),version = 1)
abstract class TurbineDatabase : RoomDatabase(){

    abstract fun turbineDao (): TurbineDao

    companion object{
        @Volatile private var instance : TurbineDatabase? = null
        private val lock = Any()

        operator fun invoke(context : Context) = instance ?: synchronized(lock){
            instance?: makeDatabese(context).also {
                instance=it
            }
        }

        private fun makeDatabese(context : Context) = Room.databaseBuilder(context.applicationContext
            ,TurbineDatabase::class.java,"turbinedatabase").build()

    }
}