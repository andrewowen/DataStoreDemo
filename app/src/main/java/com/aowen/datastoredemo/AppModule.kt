package com.aowen.datastoredemo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aowen.datastore.UserPrefs
import com.aowen.datastoredemo.data.UserPreferencesDataStoreRepository
import com.aowen.datastoredemo.data.UserPreferencesProtoDataStoreRepository
import com.aowen.datastoredemo.data.UserPreferencesRepository
import com.aowen.datastoredemo.data.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    // just my preference of naming including the package name
    name = "user_preferences"
)

val Context.userPrefsDataStore: DataStore<UserPrefs> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer()
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.userDataStore

    @Provides
    @Singleton
    fun providesPreferencesProtoDataStore(
        @ApplicationContext context: Context,
    ): DataStore<UserPrefs> = context.userPrefsDataStore

    @Provides
    fun providesUserPreferencesRepository(
        dataStore: DataStore<UserPrefs>
    ): UserPreferencesRepository = UserPreferencesProtoDataStoreRepository(dataStore)

//    @Provides
//    fun providesUserPreferencesDataStoreRepository(
//        dataStore: DataStore<Preferences>
//    ): UserPreferencesRepository = UserPreferencesDataStoreRepository(dataStore)
}