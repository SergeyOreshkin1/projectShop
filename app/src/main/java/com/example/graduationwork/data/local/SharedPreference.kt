package com.example.graduationwork.data.local

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) {
    private val prefName = "saveUID"
    private val sharedPref: SharedPreferences? =
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    fun save(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor? = sharedPref?.edit()
        editor?.putString(KEY_NAME, value)
        editor?.apply()
    }

    fun delete() {
        val editor: SharedPreferences.Editor? = sharedPref?.edit()
        editor?.clear()?.apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return sharedPref?.getString(KEY_NAME, null)
    }
}