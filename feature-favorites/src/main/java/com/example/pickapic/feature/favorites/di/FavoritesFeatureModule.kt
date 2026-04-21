package com.example.pickapic.feature.favorites.di

import android.content.Context
import androidx.room.Room
import com.example.pickapic.feature.favorites.data.AddToFavoritesUseCaseImpl
import com.example.pickapic.feature.favorites.data.FavoritePictureDatabase
import com.example.pickapic.feature.favorites.data.FavoritePicturesRepository
import com.example.pickapic.feature.favorites.data.FavoritePicturesRepositoryImpl
import com.example.pickapic.feature.favorites.data.PictureDao
import com.gsgroup.feature_favorites_api.AddToFavoritesUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoritesDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FavoritePictureDatabase =
        Room.databaseBuilder(
            context, FavoritePictureDatabase::class.java, "favorite_pictures_db"
        ).build()

    @Provides
    fun providePictureDao(db: FavoritePictureDatabase): PictureDao {
        return db.imageDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface FavoritesBindingModule {

    @Binds
    @Singleton
    fun bindFavoritePicturesRepository(
        impl: FavoritePicturesRepositoryImpl
    ): FavoritePicturesRepository

    @Binds
    fun bindAddToFavoritesUseCase(
        impl: AddToFavoritesUseCaseImpl
    ): AddToFavoritesUseCase
}
