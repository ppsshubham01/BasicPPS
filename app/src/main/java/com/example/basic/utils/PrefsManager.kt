package com.example.basic.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.basic.utils.Keys.SharedPrefrence_name
import com.example.basic.utils.Keys.onBoarding_Finished
import com.example.basic.utils.Keys.profile_screen_shown

object PreferenceManager {

    private var pref: SharedPreferences? = null

    fun init(context: Context) {
        if (pref == null) {
            pref = context.getSharedPreferences(SharedPrefrence_name, Context.MODE_PRIVATE)
        } else {
            throw RuntimeException("Preference already initialized")
        }
    }

    private val editor: SharedPreferences.Editor
        get() = pref!!.edit()

    fun delete(key: String) {
        if (pref!!.contains(key)) {
            editor.remove(key).apply()
        }
    }

    fun clear() {
        editor.clear().apply()
    }

    fun <T> getPref(key: String): T? {
        return pref!!.all[key] as T?
    }

    fun <T> getPref(key: String, defValue: T): T {
        val value = pref!!.all[key]
        return if (value != null) value as T else defValue
    }

    fun savePref(key: String, value: Any?) {
        val editor = editor
        if (value is Boolean) {
            editor.putBoolean(key, (value as Boolean?)!!)
        } else if (value is Int) {
            editor.putInt(key, (value as Int?)!!)
        } else if (value is Float) {
            editor.putFloat(key, (value as Float?)!!)
        } else if (value is Long) {
            editor.putLong(key, (value as Long?)!!)
        } else if (value is String) {
            editor.putString(key, value as String?)
        } else if (value is Enum<*>) {
            editor.putString(key, value.toString())
        } else if (value != null) {
            throw RuntimeException("Attempting to save non-primitive preference")
        }
        editor.apply()
    }

    //------------------------

    var isOnBoardingCompleted: Boolean
        get() =
            getPref(onBoarding_Finished, false)
        set(value) {
            savePref(onBoarding_Finished, value)
        }

//    var isProfileScreenShown: Boolean
//        get() =
//            getPref(profile_screen_shown, false)
//        set(value) {
//            savePref(profile_screen_shown, value)
//        }

}

object Keys {

    val SharedPrefrence_name="My_Basic_App"
    val onBoarding_Finished="onBoarding_Finished"
    val profile_screen_shown="profile_screen_shown"
}