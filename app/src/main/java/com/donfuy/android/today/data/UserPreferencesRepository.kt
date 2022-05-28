package com.donfuy.android.today.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.SHOW_COMPLETED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserPreferences(val showCompleted: Boolean)

class UserPreferencesRepository(private val userPreferencesStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    }

    val showCompletedFlow: Flow<Boolean> = userPreferencesStore.data.map { preferences ->
        preferences[SHOW_COMPLETED] ?: false
    }

    suspend fun updateShowCompleted(showCompleted: Boolean) {
        userPreferencesStore.edit { preferences ->
            preferences[SHOW_COMPLETED] = showCompleted
        }
    }
}