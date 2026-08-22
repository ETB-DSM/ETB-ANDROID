package com.aicane.app.di

import com.aicane.app.data.remote.api.NavigationApi
import com.aicane.app.data.repository.NavigationRepositoryImpl
import com.aicane.app.di.qualifier.AppClient
import com.aicane.app.domain.repository.NavigationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindNavigationRepository(impl: NavigationRepositoryImpl): NavigationRepository

    companion object {
        @Provides
        @Singleton
        fun provideNavigationApi(@AppClient retrofit: Retrofit): NavigationApi =
            retrofit.create(NavigationApi::class.java)
    }
}
