package com.giruai.climatest.di

import android.content.Context
import androidx.room.Room
import com.giruai.climatest.data.local.dao.FavoriteCityDao
import com.giruai.climatest.data.local.database.ClimaDatabase
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
    fun provideClimaDatabase(
        @ApplicationContext context: Context
    ): ClimaDatabase {
        return Room.databaseBuilder(
            context,
            ClimaDatabase::class.java,
            "clima_database"
        )
            .fallbackToDestructiveMigration() // For v1, simple strategy
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteCityDao(database: ClimaDatabase): FavoriteCityDao {
        return database.favoriteCityDao()
    }
}
