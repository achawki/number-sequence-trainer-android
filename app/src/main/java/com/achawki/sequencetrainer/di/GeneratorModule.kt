package com.achawki.sequencetrainer.di

import com.achawki.sequencetrainer.generator.LocalSequenceGenerator
import com.achawki.sequencetrainer.generator.SequenceGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object GeneratorModule {

    @Singleton
    @Provides
    fun provideGenerator(): SequenceGenerator {
        return LocalSequenceGenerator()
    }
}