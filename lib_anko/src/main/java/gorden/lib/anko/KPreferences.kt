package gorden.lib.anko

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences存储
 */


class KPreferences private constructor() {
    companion object {
        private val FILE_NAME = "teacher_preferences"
        private lateinit var mPreferences: SharedPreferences
        fun get(context: Context): KPreferences {
            mPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            return KPreferences()
        }
    }


    fun setValue(param: Pair<String, Any?>) {
        val key = param.first
        val value = param.second
        when (value) {
            is Int -> mPreferences.edit().putInt(key, value).apply()
            is Long -> mPreferences.edit().putLong(key, value).apply()
            is Float -> mPreferences.edit().putFloat(key, value).apply()
            is String -> mPreferences.edit().putString(key, value).apply()
            is Boolean -> mPreferences.edit().putBoolean(key, value).apply()
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String, defValue: T): T = with(mPreferences) {
        val result: Any? = when (defValue) {
            is Int -> getInt(key, defValue)
            is Long -> getLong(key, defValue)
            is Float -> getFloat(key, defValue)
            is String -> getString(key, defValue)
            is Boolean -> getBoolean(key, defValue)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
        result as T
    }

    fun remove(key: String) = mPreferences.edit().remove(key).apply()

    fun clear() = mPreferences.edit().clear().apply()

    fun contains(key: String) = mPreferences.contains(key)
}