package com.chatapp.android.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores the current user's session (userId + phone).
 * No tokens needed — identity is carried via X-User-Id header.
 */
@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("chatapp_session", Context.MODE_PRIVATE)
    }

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var userPhone: String?
        get() = prefs.getString(KEY_PHONE, null)
        set(value) = prefs.edit().putString(KEY_PHONE, value).apply()

    var userName: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    fun isLoggedIn() = userId != null

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PHONE   = "user_phone"
        private const val KEY_NAME    = "user_name"
    }
}
