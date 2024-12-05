package com.application.tm_application_for_tsd.di

import android.content.Context
import android.util.Log
import com.application.tm_application_for_tsd.utils.DataWedgeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataWedgeManager(
        @ApplicationContext context: Context
    ): DataWedgeManager {
        return DataWedgeManager(context)
    }
}
