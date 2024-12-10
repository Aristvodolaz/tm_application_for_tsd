package com.application.tm_application_for_tsd.utils

import android.content.Context
import android.content.SharedPreferences

class SPHelper(context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "TSDAppPreferences"
        private const val TASK_NAME = "task_name"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)


    // Save task name
    fun saveTaskName(taskName: String) {
        sharedPreferences.edit().putString(TASK_NAME, taskName).apply()
    }

    // Retrieve task name
    fun getTaskName(): String? {
        return sharedPreferences.getString(TASK_NAME, null)
    }

    // Clear all preferences
    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}
