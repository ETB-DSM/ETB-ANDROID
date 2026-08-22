package com.aicane.app.di

import com.aicane.app.data.remote.api.DestinationApi
import com.aicane.app.data.repository.DestinationRepositoryImpl
import com.aicane.app.di.qualifier.AppClient
import com.aicane.app.domain.repository.DestinationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DestinationModule {

    @Binds
    @Singleton
    abstract fun bindDestinationRepository(impl: DestinationRepositoryImpl): DestinationRepository

    companion object {
        @Provides
        @Singleton
        fun provideDestinationApi(@AppClient retrofit: Retrofit): DestinationApi =
            retrofit.create(DestinationApi::class.java)
    }
}
