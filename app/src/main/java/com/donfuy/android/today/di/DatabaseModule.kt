package com.donfuy.android.today.di

import android.content.Context
import com.donfuy.android.today.data.TaskDao
import com.donfuy.android.today.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.taskItemDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): TaskDatabase {
        return TaskDatabase.getDatabase(context = appContext)
    }
}