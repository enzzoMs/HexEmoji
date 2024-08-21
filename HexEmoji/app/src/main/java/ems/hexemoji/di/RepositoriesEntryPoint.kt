package ems.hexemoji.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ems.hexemoji.data.repositories.EmojiRepository
import ems.hexemoji.data.repositories.PreferencesRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoriesEntryPoint {

    fun getEmojisRepository(): EmojiRepository
    fun getPreferencesRepository(): PreferencesRepository
}