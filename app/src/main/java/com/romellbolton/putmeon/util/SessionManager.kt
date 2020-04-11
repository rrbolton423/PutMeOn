package com.romellbolton.putmeon.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager {
    var PRIVATE_MODE = 0
    fun createLoginSession(context: Context, apiUserName: String?) {
        pref = context.getSharedPreferences(apiUserName, Context.MODE_MULTI_PROCESS or PRIVATE_MODE)
        editor = pref.edit()
        editor.clear()
        editor.putBoolean(IS_LOGIN, true)
        editor.putString("id", apiUserName)
        editor.commit()
    }

    companion object {
        private lateinit var pref: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private val context: Context? = null
        private const val IS_LOGIN = "IsLoggedIn"
        const val clientId = "8740928683fe4ab6be03091a875ac618"
    }
}