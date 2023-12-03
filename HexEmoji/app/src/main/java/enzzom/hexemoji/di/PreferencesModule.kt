package enzzom.hexemoji.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import enzzom.hexemoji.R
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {

    @Provides
    fun provideAppPreferences(
        @ApplicationContext applicationContext: Context
    ): SharedPreferences = applicationContext.getSharedPreferences(
        applicationContext.resources.getString(R.string.preferences_file_key),
        Context.MODE_PRIVATE
    )

    @Provides
    @Named("preference_key_show_board_tutorial")
    fun provideBoardTutorialPreferenceKey(
        @ApplicationContext applicationContext: Context
    ): String = applicationContext.resources.getString(R.string.preference_key_show_board_tutorial)
}