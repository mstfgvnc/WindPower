package com.mustafaguvenc.windpower.di

import android.content.Context
import com.mustafaguvenc.windpower.db.TurbineDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun provideTurbinbeDatabase(@ApplicationContext appContext: Context) = TurbineDatabase.invoke(appContext)

    @Singleton
    @Provides
    fun provideTurbinbeDao(db: TurbineDatabase) = db.turbineDao()

}