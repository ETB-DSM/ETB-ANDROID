package com.aicane.app.di

import com.aicane.app.data.remote.api.EmbeddedApi
import com.aicane.app.di.qualifier.EmbeddedClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmbeddedModule {

    @Provides
    @Singleton
    fun provideEmbeddedApi(@EmbeddedClient retrofit: Retrofit): EmbeddedApi =
        retrofit.create(EmbeddedApi::class.java)
}
