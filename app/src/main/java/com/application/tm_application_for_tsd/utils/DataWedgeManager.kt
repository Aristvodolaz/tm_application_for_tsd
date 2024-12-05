package com.application.tm_application_for_tsd.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle

class DataWedgeManager(private val context: Context) {

    fun createAndConfigureProfile(profileName: String, packageName: String, intentAction: String) {
        // Создание профиля
        val createProfileIntent = Intent("com.symbol.datawedge.api.ACTION")
        createProfileIntent.putExtra("com.symbol.datawedge.api.CREATE_PROFILE", profileName)
        context.sendBroadcast(createProfileIntent)

        // Настройка профиля
        val configureIntent = Intent("com.symbol.datawedge.api.ACTION")
        configureIntent.putExtra("com.symbol.datawedge.api.SET_CONFIG", Bundle().apply {
            putString("PROFILE_NAME", profileName)
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "UPDATE")

            // Barcode Input plugin configuration
            putParcelableArray("PLUGIN_CONFIG", arrayOf(Bundle().apply {
                putString("PLUGIN_NAME", "BARCODE")
                putString("RESET_CONFIG", "true")
                putBundle("PARAM_LIST", Bundle().apply {
                    putString("scanner_selection", "auto")
                })
            }))

            // Intent Output plugin configuration
            putParcelableArray("APP_LIST", arrayOf(Bundle().apply {
                putString("PACKAGE_NAME", packageName)
                putString("ACTIVITY_LIST", "*")
                putString("intent_output_enabled", "true")
                putString("intent_action", intentAction)
                putString("intent_delivery", "2") // Broadcast intent
            }))
        })
        context.sendBroadcast(configureIntent)
    }

    fun toggleDataWedge(enable: Boolean) {
        val intent = Intent("com.symbol.datawedge.api.ACTION")
        intent.putExtra("com.symbol.datawedge.api.ENABLE_DATAWEDGE", enable)
        context.sendBroadcast(intent)
    }

    fun queryActiveProfile() {
        val intent = Intent("com.symbol.datawedge.api.ACTION")
        intent.putExtra("com.symbol.datawedge.api.GET_ACTIVE_PROFILE", true)
        context.sendBroadcast(intent)
    }
}
