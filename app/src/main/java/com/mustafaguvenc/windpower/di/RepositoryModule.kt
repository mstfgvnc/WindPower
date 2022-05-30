package com.mustafaguvenc.windpower.di

import com.mustafaguvenc.windpower.db.TurbineDao
import com.mustafaguvenc.windpower.network.ElevationAPI
import com.mustafaguvenc.windpower.network.TimeAPI
import com.mustafaguvenc.windpower.network.TurbineAPI
import com.mustafaguvenc.windpower.network.WindSpeedsAPI
import com.mustafaguvenc.windpower.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: TurbineAPI,
                          localDataSource: TurbineDao,
                          remoteDataSourceElevation: ElevationAPI,
                          remoteDataSourceForWindSpeeds: WindSpeedsAPI,
                          remoteDataSourceTime: TimeAPI
    ) =
        Repository(remoteDataSource,localDataSource,remoteDataSourceElevation,remoteDataSourceForWindSpeeds ,remoteDataSourceTime)
}