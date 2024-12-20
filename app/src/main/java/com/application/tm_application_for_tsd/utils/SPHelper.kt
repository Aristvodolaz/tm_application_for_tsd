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
        private const val PREF = "pref"
        private const val SHK_BOX = "shk_box"
        private const val VLOZHENNOST_BOX = "vlozh_box"
        private const val SHK_PALLET = "shk_pallet"
        private const val ID = "id"
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

    fun setPref(pref: String){
        sharedPreferences.edit().putString(PREF, pref).apply()
    }

    fun getPref(): String?{
        return sharedPreferences.getString(PREF, null)
    }
    fun setVlozh(vlozh: Int){
        sharedPreferences.edit().putInt(VLOZHENNOST_BOX, vlozh).apply()
    }
    fun getVlozh(): Int {
        return sharedPreferences.getInt(VLOZHENNOST_BOX, 0)
    }

    fun setId(id: Long){
        sharedPreferences.edit().putLong(ID, id).apply()
    }
    fun getId(): Long {
        return sharedPreferences.getLong(ID, 0)
    }

    fun setSHKBox(shk: String){
        sharedPreferences.edit().putString(SHK_BOX, shk).apply()
    }
    fun getSHKBox(): String?{
        return sharedPreferences.getString(SHK_BOX, null)
    }

    fun setSHKPallet(shk: String){
        sharedPreferences.edit().putString(SHK_PALLET, shk).apply()
    }
    fun getSHKPallet(): String?{
        return sharedPreferences.getString(SHK_PALLET, null)
    }
    // Clear all preferences
    fun clearPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}
