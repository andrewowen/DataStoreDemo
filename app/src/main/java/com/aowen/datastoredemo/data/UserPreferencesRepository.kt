package com.aowen.datastoredemo.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aowen.datastore.UserPrefs
import com.aowen.datastore.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class UserPreferences(
    val userName: String = "Andrew",
    val backgroundColor: Int = Color.Red.toArgb()
)

interface UserPreferencesRepository {

    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun fetchInitialUserPreferences(): UserPreferences
    suspend fun updateUserName(userName: String)
    suspend fun updateBackgroundColor(backgroundColor: Int)
}

class UserPreferencesDataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val userPreferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            mapUserPreferences(it)
        }

    override suspend fun fetchInitialUserPreferences(): UserPreferences =
        mapUserPreferences(dataStore.data.first().toPreferences())

    override suspend fun updateUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME_KEY] = userName
        }
    }

    override suspend fun updateBackgroundColor(backgroundColor: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.BACKGROUND_COLOR_KEY] = backgroundColor
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val userName = preferences[PreferencesKeys.USERNAME_KEY] ?: ""
        val backgroundColor = preferences[PreferencesKeys.BACKGROUND_COLOR_KEY] ?: 0
        return UserPreferences(userName, backgroundColor)
    }

    private object PreferencesKeys {
        val USERNAME_KEY = stringPreferencesKey("username")
        val BACKGROUND_COLOR_KEY = intPreferencesKey("background_color")
    }
}

class UserPreferencesProtoDataStoreRepository @Inject constructor(
    private val dataStore: DataStore<UserPrefs>
) : UserPreferencesRepository {

    override val userPreferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(UserPrefs.getDefaultInstance())
            } else {
                throw exception
            }
        }.map {
            mapUserPreferences(it)
        }

    override suspend fun fetchInitialUserPreferences(): UserPreferences =
        mapUserPreferences(dataStore.data.first())

    override suspend fun updateUserName(userName: String) {
        dataStore.updateData { preferences ->
            preferences.copy {
                username = userName
            }
        }
    }

    override suspend fun updateBackgroundColor(backgroundColor: Int) {
        dataStore.updateData { preferences ->
            preferences.copy {
                color = backgroundColor
            }
        }
    }

    private fun mapUserPreferences(preferences: UserPrefs): UserPreferences =
        UserPreferences(preferences.username, preferences.color)

}