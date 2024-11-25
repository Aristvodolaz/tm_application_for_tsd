package com.application.tm_application_for_tsd.di

import android.content.Context
import com.application.tm_application_for_tsd.utils.ScannerController
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
    fun provideScannerController(
        @ApplicationContext context: Context
    ): ScannerController {
        return ScannerController(context, object : ScannerController.ScannerCallback {
            override fun onDataReceived(barcodeData: String) {
                // Логика обработки данных сканера
            }

            override fun onScanFailed(errorMessage: String) {
                // Логика обработки ошибок сканера
            }
        })
    }
}
