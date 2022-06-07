package com.donfuy.android.today.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.COMPLETED_TO_BOTTOM
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.DAYS_TO_KEEP_TASKS
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.SHOW_COMPLETED
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.SORT_ORDER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

enum class SortOrder {
    NONE,
    BY_CHECKED,
    BY_CREATION,
    BY_CHECKED_AND_CREATION
}

class UserPreferencesRepository @Inject constructor(private val userPreferencesStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val COMPLETED_TO_BOTTOM = booleanPreferencesKey("completed_to_bottom")
        val DAYS_TO_KEEP_TASKS = intPreferencesKey("days_to_keep_tasks")
    }

    suspend fun fetchInitialPreferences() = userPreferencesStore.data.first().toPreferences()

    // UGLY
    val daysToKeepSync = runBlocking {
        userPreferencesStore.data.map { it[DAYS_TO_KEEP_TASKS] }
            .first() ?: 3
    }

    val daysToKeep: Flow<Int> = userPreferencesStore.data.map { preferences ->
        preferences[DAYS_TO_KEEP_TASKS] ?: 3
    }

    suspend fun updateDaysToKeep(daysToKeep: Int) {
        userPreferencesStore.edit { preferences ->
            preferences[DAYS_TO_KEEP_TASKS] = daysToKeep
        }
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