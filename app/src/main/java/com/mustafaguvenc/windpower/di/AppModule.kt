package com.mustafaguvenc.windpower.di

import com.mustafaguvenc.windpower.model.TurbineModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideList() : ArrayList<TurbineModel> = ArrayList<TurbineModel>()


}