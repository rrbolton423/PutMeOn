package com.romellbolton.putmeon.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.romellbolton.putmeon.controller.LoginActivity
import java.util.*

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

    val userDetails: HashMap<String, String>
        get() {
            val user = HashMap<String, String>()
            user["id"] = pref.getString("id", null)
            return user
        }

    fun checkLogin() {
        if (!isLoggedIn) {
            val i = Intent(context, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context!!.startActivity(i)
        }
    }

    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)

    fun logoutUser() {
        editor.clear()
        editor.commit()
        val i = Intent(context, LoginActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(i)
    }

    companion object {
        private lateinit var pref: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private val context: Context? = null
        private const val IS_LOGIN = "IsLoggedIn"
        const val clientId = "8740928683fe4ab6be03091a875ac618"
    }
}