package com.giruai.climatest.di

import com.giruai.climatest.data.repository.FavoritesRepositoryImpl
import com.giruai.climatest.data.repository.WeatherRepositoryImpl
import com.giruai.climatest.domain.repository.FavoritesRepository
import com.giruai.climatest.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        impl: FavoritesRepositoryImpl
    ): FavoritesRepository
}
