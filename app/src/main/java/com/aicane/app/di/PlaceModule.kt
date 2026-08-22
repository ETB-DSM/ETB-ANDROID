package com.aicane.app.di

import com.aicane.app.data.remote.api.TmapPoiApi
import com.aicane.app.data.repository.PlaceRepositoryImpl
import com.aicane.app.di.qualifier.TmapClient
import com.aicane.app.domain.repository.PlaceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaceModule {

    @Binds
    @Singleton
    abstract fun bindPlaceRepository(impl: PlaceRepositoryImpl): PlaceRepository

    companion object {
        @Provides
        @Singleton
        fun provideTmapPoiApi(@TmapClient retrofit: Retrofit): TmapPoiApi =
            retrofit.create(TmapPoiApi::class.java)
    }
}
