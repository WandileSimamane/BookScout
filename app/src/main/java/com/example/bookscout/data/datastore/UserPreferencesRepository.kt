package com.example.bookscout.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.bookscout.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Extension property to instantiate DataStore as a singleton instance across the Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bookscout_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val GRID_VIEW = booleanPreferencesKey("use_grid_view")
    }

    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppSettings(
                isDarkMode = preferences[PreferencesKeys.DARK_MODE] ?: false,
                useGridView = preferences[PreferencesKeys.GRID_VIEW] ?: false
            )
        }

    suspend fun toggleDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    suspend fun toggleLayoutView(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GRID_VIEW] = enabled
        }
    }
}