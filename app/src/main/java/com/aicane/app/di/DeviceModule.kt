package com.aicane.app.di

import com.aicane.app.data.remote.api.DeviceApi
import com.aicane.app.data.repository.DeviceRepositoryImpl
import com.aicane.app.di.qualifier.AppClient
import com.aicane.app.domain.repository.DeviceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    companion object {
        @Provides
        @Singleton
        fun provideDeviceApi(@AppClient retrofit: Retrofit): DeviceApi =
            retrofit.create(DeviceApi::class.java)
    }
}
