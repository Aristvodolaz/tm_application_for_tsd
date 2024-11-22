package com.application.tm_application_for_tsd.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.*

class ScannerController(
    context: Context,
    var callback: ScannerCallback
) : EMDKManager.EMDKListener, Scanner.DataListener, Scanner.StatusListener {

    private val TAG = "ScannerController"

    private var emdkManager: EMDKManager? = null
    private var barcodeManager: BarcodeManager? = null
    private var scanner: Scanner? = null
    private val appContext = context.applicationContext
    private var isScannerInitialized = false

    init {
        val results = EMDKManager.getEMDKManager(appContext, this)
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.e(TAG, "Failed to initialize EMDKManager: ${results.statusCode}")
            Toast.makeText(appContext, "Error initializing EMDK: ${results.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOpened(emdkManager: EMDKManager?) {
        this.emdkManager = emdkManager
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
        if (barcodeManager != null) {
            Log.d(TAG, "BarcodeManager initialized.")
        } else {
            Log.e(TAG, "Failed to initialize BarcodeManager.")
            Toast.makeText(appContext, "Error initializing BarcodeManager", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeScanner() {
        if (barcodeManager != null && !isScannerInitialized) {
            try {
                scanner = barcodeManager?.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
                scanner?.apply {
                    addDataListener(this@ScannerController)
                    addStatusListener(this@ScannerController)
                    triggerType = Scanner.TriggerType.HARD
                    enable()
                    configureScanner()
                    isScannerInitialized = true
                    Log.d(TAG, "Scanner initialized and enabled.")
                } ?: run {
                    Log.e(TAG, "Scanner is null.")
                    Toast.makeText(appContext, "Scanner is null.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ScannerException) {
                Log.e(TAG, "Error enabling scanner: ${e.message}", e)
                Toast.makeText(appContext, "Error enabling scanner: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error: ${e.message}", e)
                Toast.makeText(appContext, "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureScanner() {
        try {
            val config = scanner?.config ?: return

            config.decoderParams.apply {
                ean13.enabled = true
                code128.enabled = true
                code128.length1 = 1
                code128.length2 = 100
                code39.enabled = true
                code39.length1 = 1
                code39.length2 = 100
                upca.enabled = true
            }

            scanner?.config = config
            Log.d(TAG, "Scanner configured for barcodes with length from 1 to 100.")
        } catch (e: ScannerException) {
            Log.e(TAG, "Error configuring scanner: ${e.message}")
        }
    }

    fun isScannerActive(): Boolean {
        return scanner?.isEnabled ?: false
    }

    fun releaseScanner() {
        scanner?.let {
            try {
                it.disable()
                isScannerInitialized = false
                Log.d(TAG, "Scanner disabled.")
            } catch (e: ScannerException) {
                Log.e(TAG, "Error disabling scanner: ${e.message}")
                Toast.makeText(appContext, "Error disabling scanner: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            it.removeDataListener(this)
            it.removeStatusListener(this)
            scanner = null
        }
    }

    override fun onData(scanDataCollection: ScanDataCollection?) {
        if (scanDataCollection?.result == ScannerResults.SUCCESS) {
            scanDataCollection.scanData.forEach { data ->
                val barcodeData = data.data
                callback.onDataReceived(barcodeData)
            }
        } else {
            callback.onScanFailed("Failed to read barcode.")
        }
    }

    override fun onStatus(statusData: StatusData?) {
        when (statusData?.state) {
            StatusData.ScannerStates.IDLE -> {
                Log.d(TAG, "Scanner is idle. Ready to scan.")
                startScanning()
            }
            StatusData.ScannerStates.WAITING -> {
                Log.d(TAG, "Scanner is waiting for trigger.")
            }
            StatusData.ScannerStates.SCANNING -> {
                Log.d(TAG, "Scanner is scanning.")
            }
            StatusData.ScannerStates.DISABLED -> {
                Log.d(TAG, "Scanner is disabled.")
            }
            StatusData.ScannerStates.ERROR -> {
                Log.e(TAG, "Scanner error: ${statusData.state.name}")
                callback.onScanFailed("Scanner error occurred.")
            }

            null -> TODO()
        }
    }

    fun startScanning() {
        try {
            scanner?.read()
            Log.d(TAG, "Scanner started scanning.")
        } catch (e: ScannerException) {
            Log.e(TAG, "Error starting scan: ${e.message}")
            callback.onScanFailed("Error starting scan.")
        }
    }

    fun resumeScanner() {
        if (scanner != null) {
            startScanning()
        } else {
            initializeScanner()
        }
    }

    interface ScannerCallback {
        fun onDataReceived(barcodeData: String)
        fun onScanFailed(errorMessage: String)
    }
}
