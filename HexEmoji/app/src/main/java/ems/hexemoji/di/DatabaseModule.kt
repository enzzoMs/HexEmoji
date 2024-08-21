package ems.hexemoji.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ems.hexemoji.data.AppDatabase
import ems.hexemoji.data.source.ChallengesDAO
import ems.hexemoji.data.source.EmojiDAO
import ems.hexemoji.data.source.StatisticsDAO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext applicationContext: Context
    ): AppDatabase = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "AppDatabase"
    ).createFromAsset(
        "database/hexemoji_database.db"
    ).build()

    @Singleton
    @Provides
    fun provideEmojiDAO(appDatabase: AppDatabase): EmojiDAO = appDatabase.emojiDAO()

    @Singleton
    @Provides
    fun provideStatisticsDAO(appDatabase: AppDatabase): StatisticsDAO = appDatabase.statisticsDAO()

    @Singleton
    @Provides
    fun provideChallengesDAO(appDatabase: AppDatabase): ChallengesDAO = appDatabase.challengesDAO()
}