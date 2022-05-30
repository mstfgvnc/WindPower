package com.mustafaguvenc.windpower.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TurbineModel(
    @ColumnInfo(name="power")
    val power : Double?,
    @ColumnInfo(name="cutin")
    val cutin: Double?,
    @ColumnInfo(name="cutout")
    val cutout: Double?,
    @ColumnInfo(name="nom")
    val nom: Double?,
    @ColumnInfo(name="h")
    val h: Double?

)
{
    @PrimaryKey(autoGenerate = true)
    var uuid :Int =0
}