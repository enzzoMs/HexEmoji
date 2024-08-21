package ems.hexemoji.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

private const val PREFERENCES_FILE_KEY = "app_preferences"

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {

    @Provides
    fun provideAppPreferences(
        @ApplicationContext applicationContext: Context
    ): SharedPreferences = applicationContext.getSharedPreferences(
        PREFERENCES_FILE_KEY, Context.MODE_PRIVATE
    )
}