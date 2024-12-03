package com.application.tm_application_for_tsd.utils

import android.content.Context
import android.util.Log
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.StatusData
import com.symbol.emdk.barcode.ScannerResults

class ScannerController(
    context: Context,
    var callback: ScannerCallback
) : EMDKManager.EMDKListener, Scanner.DataListener, Scanner.StatusListener {

    private val TAG = "ScannerController"

    private var emdkManager: EMDKManager? = null
    private var barcodeManager: BarcodeManager? = null
    private var scanner: Scanner? = null
    private var isScannerInitialized = false

    init {
        initializeEMDK(context)
    }

    private fun initializeEMDK(context: Context) {
        val result = EMDKManager.getEMDKManager(context, this)
        if (result.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.e(TAG, "Ошибка инициализации EMDK: ${result.statusCode}")
            callback.onScanFailed("Ошибка инициализации EMDK.")
        }
    }

    override fun onOpened(manager: EMDKManager?) {
        emdkManager = manager
        initBarcodeManager()
        initializeScanner()
    }

    override fun onClosed() {
        releaseScanner()
        emdkManager?.release()
        emdkManager = null
    }

    private fun initBarcodeManager() {
        barcodeManager = emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as? BarcodeManager
        if (barcodeManager == null) {
            callback.onScanFailed("Ошибка инициализации BarcodeManager.")
        }
    }

    private fun initializeScanner() {
        scanner?.triggerType = Scanner.TriggerType.HARD

        try {
            scanner = barcodeManager?.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
            scanner?.apply {
                addDataListener(this@ScannerController)
                addStatusListener(this@ScannerController)
                triggerType = Scanner.TriggerType.HARD
                enable()
                configureScanner()
                isScannerInitialized = true
                Log.d(TAG, "Сканер успешно инициализирован.")
            } ?: callback.onScanFailed("Сканер не найден.")
        } catch (e: ScannerException) {
            callback.onScanFailed("Ошибка инициализации сканера: ${e.message}")
        }
    }

    private fun configureScanner() {
        try {
            val config = scanner?.config ?: return

            config.decoderParams.apply {
                ean13.enabled = true
                code128.enabled = true
                code39.enabled = true
                upca.enabled = true
            }

            scanner?.config = config
            Log.d(TAG, "Сканер настроен.")
        } catch (e: ScannerException) {
            Log.e(TAG, "Ошибка конфигурации сканера: ${e.message}")
        }
    }


    fun startScanning() {
        if (!isScannerInitialized) {
            Log.e(TAG, "Сканер не инициализирован.")
            callback.onScanFailed("Сканер не инициализирован.")
            return
        }
        try {
            scanner?.read()
            Log.d(TAG, "Сканирование запущено.")
        } catch (e: ScannerException) {
            Log.e(TAG, "Ошибка запуска сканирования: ${e.message}")
            callback.onScanFailed("Ошибка запуска сканирования: ${e.message}")
        }
    }
    fun resumeScanner() {
        if (!isScannerInitialized) {
            initializeScanner()
        } else {
            startScanning()
        }
    }

    fun stopScanning() {
        try {
            scanner?.cancelRead()
            Log.d(TAG, "Сканирование остановлено.")
        } catch (e: ScannerException) {
            callback.onScanFailed("Ошибка остановки сканирования: ${e.message}")
        }
    }

    fun releaseScanner() {
        try {
            scanner?.apply {
                removeDataListener(this@ScannerController)
                removeStatusListener(this@ScannerController)
                disable()
            }
            scanner = null
            isScannerInitialized = false
        } catch (e: ScannerException) {
            Log.e(TAG, "Ошибка отключения сканера: ${e.message}")
        }
    }

    override fun onData(scanDataCollection: ScanDataCollection?) {
        if (scanDataCollection?.result == ScannerResults.SUCCESS) {
            scanDataCollection.scanData.forEach {
                callback.onDataReceived(it.data)
            }
        } else {
            callback.onScanFailed("Не удалось считать штрих-код.")
        }
    }

    override fun onStatus(statusData: StatusData?) {
        when (statusData?.state) {
            StatusData.ScannerStates.IDLE -> {
                Log.d(TAG, "Сканер готов. Запускаем сканирование.")
                try {
                    scanner?.read()
                } catch (e: ScannerException) {
                    Log.e(TAG, "Ошибка запуска сканирования в IDLE: ${e.message}")
                    callback.onScanFailed("Ошибка запуска сканирования: ${e.message}")
                }
            }
            StatusData.ScannerStates.SCANNING -> Log.d(TAG, "Сканирование идет.")
            StatusData.ScannerStates.DISABLED -> Log.d(TAG, "Сканер отключен.")
            StatusData.ScannerStates.ERROR -> {
                Log.e(TAG, "Ошибка сканера.")
                callback.onScanFailed("Ошибка сканера.")
            }
            else -> Log.d(TAG, "Неизвестное состояние сканера: ${statusData?.state}")
        }
    }


    interface ScannerCallback {
        fun onDataReceived(barcodeData: String)
        fun onScanFailed(errorMessage: String)
    }
}
