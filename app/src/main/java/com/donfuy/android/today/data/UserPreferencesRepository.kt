package com.donfuy.android.today.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.COMPLETED_TO_BOTTOM
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.SHOW_COMPLETED
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.SORT_ORDER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class SortOrder {
    NONE,
    BY_CHECKED,
    BY_CREATION,
    BY_CHECKED_AND_CREATION
}

class UserPreferencesRepository(private val userPreferencesStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val COMPLETED_TO_BOTTOM = booleanPreferencesKey("completed_to_bottom")
    }

    val showCompletedFlow: Flow<Boolean> = userPreferencesStore.data.map { preferences ->
        preferences[SHOW_COMPLETED] ?: false
    }

    suspend fun updateShowCompleted(showCompleted: Boolean) {
        userPreferencesStore.edit { preferences ->
            preferences[SHOW_COMPLETED] = showCompleted
        }
    }

    val completedToBottom: Flow<Boolean> = userPreferencesStore.data.map { preferences ->
        preferences[COMPLETED_TO_BOTTOM] ?: true
    }

    suspend fun updateCompletedToBottom(completedToBottom: Boolean) {
        userPreferencesStore.edit { preferences ->
            preferences[COMPLETED_TO_BOTTOM] = completedToBottom
        }
    }

    val sortOrderFlow: Flow<String> = userPreferencesStore.data.map { preferences ->
        preferences[SORT_ORDER] ?: SortOrder.NONE.name
    }

    suspend fun enableSortByChecked(enable: Boolean) {
        userPreferencesStore.edit { preferences ->
            val currentOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.NONE.name
            )

            if (enable) {
                if (currentOrder == SortOrder.BY_CREATION) {
                    preferences[SORT_ORDER] = SortOrder.BY_CHECKED_AND_CREATION.name
                } else {
                    preferences[SORT_ORDER] = SortOrder.BY_CHECKED.name
                }
            } else {
                if (currentOrder == SortOrder.BY_CHECKED_AND_CREATION) {
                    preferences[SORT_ORDER] = SortOrder.BY_CREATION.name
                } else {
                    preferences[SORT_ORDER] = SortOrder.NONE.name
                }
            }

        }
    }

    suspend fun enableSortByCreation(enable: Boolean) {

    }
}