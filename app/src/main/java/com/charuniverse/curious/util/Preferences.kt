package com.charuniverse.curious.util

import android.content.Context
import android.content.SharedPreferences

object Preferences {

    private const val NAME = "CuriousAppAndroid"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    private val _userId = Pair("userId", "")

    var userId: String
        get() = preferences.getString(_userId.first, _userId.second) ?: ""
        set(value) = preferences.edit {
            it.putString(_userId.first, value)
        }

}
