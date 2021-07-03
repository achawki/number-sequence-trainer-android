package com.achawki.sequencetrainer.di

import android.content.Context
import com.achawki.sequencetrainer.data.AppDatabase
import com.achawki.sequencetrainer.data.SequenceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideSequenceDao(appDatabase: AppDatabase): SequenceDao {
        return appDatabase.sequenceDao()
    }
}