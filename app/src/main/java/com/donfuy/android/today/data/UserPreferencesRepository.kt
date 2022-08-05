package com.donfuy.android.today.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.COMPLETED_TO_BOTTOM
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.DAYS_TO_KEEP_TASKS
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.HOUR_TO_DELETE_TASKS
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.MIN_TO_DELETE_TASKS
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.SHOW_COMPLETED
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.SORT_ORDER
import com.donfuy.android.today.data.UserPreferencesRepository.PreferencesKeys.USE_DYNAMIC_THEME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
        val USE_DYNAMIC_THEME = booleanPreferencesKey("use_dynamic_theme")
        val HOUR_TO_DELETE_TASKS = intPreferencesKey("hour_to_delete_tasks")
        val MIN_TO_DELETE_TASKS = intPreferencesKey("min_to_delete_tasks")
    }

    val minToDeleteTasks: Flow<Int> = userPreferencesStore.data.map { prefs ->
        prefs[MIN_TO_DELETE_TASKS] ?: 0
    }

    suspend fun updateMinToDeleteTasks(minToDeleteTasks: Int) {
        userPreferencesStore.edit { prefs ->
            prefs[MIN_TO_DELETE_TASKS] = minToDeleteTasks
        }
    }

    val hourToDeleteTasks: Flow<Int> = userPreferencesStore.data.map { preferences ->
        preferences[HOUR_TO_DELETE_TASKS] ?: 3
    }

    suspend fun updateHourToDeleteTasks(hourToDeleteTasks: Int) {
        userPreferencesStore.edit { prefs ->
            prefs[HOUR_TO_DELETE_TASKS] = hourToDeleteTasks
        }
    }

    val useDynamicTheme: Flow<Boolean> = userPreferencesStore.data.map { preferences ->
        preferences[USE_DYNAMIC_THEME] ?: false
    }

    val daysToKeep: Flow<Int> = userPreferencesStore.data.map { preferences ->
        preferences[DAYS_TO_KEEP_TASKS] ?: 3
    }

    val completedToBottom: Flow<Boolean> = userPreferencesStore.data.map { preferences ->
        preferences[COMPLETED_TO_BOTTOM] ?: true
    }


    val showCompleted: Flow<Boolean> = userPreferencesStore.data.map { preferences ->
        preferences[SHOW_COMPLETED] ?: false
    }

    suspend fun updateUseDynamicTheme(useDynamicTheme: Boolean) {
        userPreferencesStore.edit { preferences ->
            preferences[USE_DYNAMIC_THEME] = useDynamicTheme
        }
    }

    suspend fun updateDaysToKeep(daysToKeep: Int) {
        userPreferencesStore.edit { preferences ->
            preferences[DAYS_TO_KEEP_TASKS] = daysToKeep
        }
    }


    suspend fun updateShowCompleted(showCompleted: Boolean) {
        userPreferencesStore.edit { preferences ->
            preferences[SHOW_COMPLETED] = showCompleted
        }
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