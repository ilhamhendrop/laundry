package com.greensoft.laundryapp.preference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("CommitPrefEdits")
class PreferenceHelper(context: Context) {

    companion object {
        const val LAUNDRY_APP = "laundryApp"
        const val LOGIN = "login"
    }

    private var sharedPref: SharedPreferences
    private var prefEditor: SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences(LAUNDRY_APP, Context.MODE_PRIVATE)
        prefEditor = sharedPref.edit()
    }

    fun getLogin(): Boolean {
        return sharedPref.getBoolean(LOGIN, false)
    }

    fun saveLogin(keySP: String, value: Boolean){
        prefEditor.putBoolean(keySP, value)
                .commit()
    }
}