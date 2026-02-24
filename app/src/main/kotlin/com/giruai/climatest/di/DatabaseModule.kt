package com.giruai.climatest.di

import android.content.Context
import androidx.room.Room
import com.giruai.climatest.data.local.database.ClimaDatabase
import com.giruai.climatest.data.local.database.FavoriteCityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ClimaDatabase =
        Room.databaseBuilder(
            context,
            ClimaDatabase::class.java,
            "clima_db"
        ).build()

    @Provides
    fun provideFavoriteCityDao(database: ClimaDatabase): FavoriteCityDao =
        database.favoriteCityDao()
}
