package enzzom.hexemoji.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import enzzom.hexemoji.data.AppDatabase
import enzzom.hexemoji.data.source.ChallengesDAO
import enzzom.hexemoji.data.source.EmojiDAO
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
    fun provideChallengesDAO(appDatabase: AppDatabase): ChallengesDAO = appDatabase.challengesDAO()
}