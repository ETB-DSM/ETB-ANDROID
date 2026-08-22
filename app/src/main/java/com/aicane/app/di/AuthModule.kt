package com.aicane.app.di

import com.aicane.app.data.remote.api.AuthApi
import com.aicane.app.data.repository.AuthRepositoryImpl
import com.aicane.app.di.qualifier.AppClient
import com.aicane.app.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(@AppClient retrofit: Retrofit): AuthApi =
            retrofit.create(AuthApi::class.java)
    }
}
