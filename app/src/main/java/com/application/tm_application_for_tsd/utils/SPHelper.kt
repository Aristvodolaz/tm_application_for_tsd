package com.application.tm_application_for_tsd.utils

import android.content.Context
import android.content.SharedPreferences

class SPHelper(context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "TSDAppPreferences"
        private const val TASK_NAME = "task_name"
        private const val SHK_WORK = "shk_work"
        private const val ARTICLE_WORK = "article_work"
        private const val NAME_STUFF_WORK = "name_stuff_work"
        private const val NAME_EMPLOYER = "name_employer"
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

    // Save SHK
    fun setShkWork(shk: String) {
        sharedPreferences.edit().putString(SHK_WORK, shk).apply()
    }

    // Retrieve SHK
    fun getShkWork(): String? {
        return sharedPreferences.getString(SHK_WORK, null)
    }

    // Save Article
    fun setArticuleWork(article: String) {
        sharedPreferences.edit().putString(ARTICLE_WORK, article).apply()
    }

    // Retrieve Article
    fun getArticuleWork(): String? {
        return sharedPreferences.getString(ARTICLE_WORK, null)
    }

    // Save Name of Stuff
    fun setNameStuffWork(name: String) {
        sharedPreferences.edit().putString(NAME_STUFF_WORK, name).apply()
    }

    // Retrieve Name of Stuff
    fun getNameStuffWork(): String? {
        return sharedPreferences.getString(NAME_STUFF_WORK, null)
    }

    // Save Name of Employer
    fun setNameEmployer(name: String) {
        sharedPreferences.edit().putString(NAME_EMPLOYER, name).apply()
    }

    // Retrieve Name of Employer
    fun getNameEmployer(): String? {
        return sharedPreferences.getString(NAME_EMPLOYER, null)
    }

    // Clear all preferences
    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}