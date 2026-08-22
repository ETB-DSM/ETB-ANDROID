package com.aicane.app.di

import com.aicane.app.data.remote.api.GuardianApi
import com.aicane.app.data.repository.GuardianRepositoryImpl
import com.aicane.app.di.qualifier.AppClient
import com.aicane.app.domain.repository.GuardianRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GuardianModule {

    @Binds
    @Singleton
    abstract fun bindGuardianRepository(impl: GuardianRepositoryImpl): GuardianRepository

    companion object {
        @Provides
        @Singleton
        fun provideGuardianApi(@AppClient retrofit: Retrofit): GuardianApi =
            retrofit.create(GuardianApi::class.java)
    }
}
