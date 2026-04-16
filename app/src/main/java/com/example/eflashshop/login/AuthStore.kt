package com.example.eflashshop.login

import android.content.Context

object AuthStore {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_CURRENT_USER = "current_user"
    private const val KEY_ACTIVE_CART_PREFIX = "active_cart_"

    fun registerUser(context: Context, username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = "user_$username"
        if (prefs.contains(key)) {
            return false
        }
        prefs.edit().putString(key, password).apply()
        return true
    }

    fun loginUser(context: Context, username: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString("user_$username", null)
        if (stored == password) {
            prefs.edit().putString(KEY_CURRENT_USER, username).apply()
            return true
        }
        return false
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getString(KEY_CURRENT_USER, null).isNullOrBlank()
    }

    fun getCurrentUser(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENT_USER, null)
    }

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }

    fun setActiveCartId(context: Context, cartId: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val username = prefs.getString(KEY_CURRENT_USER, null) ?: return
        prefs.edit().putLong("$KEY_ACTIVE_CART_PREFIX$username", cartId).apply()
    }

    fun getActiveCartId(context: Context): Long? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val username = prefs.getString(KEY_CURRENT_USER, null) ?: return null
        val key = "$KEY_ACTIVE_CART_PREFIX$username"
        return if (prefs.contains(key)) prefs.getLong(key, -1L) else null
    }

    fun clearActiveCartForCurrentUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val username = prefs.getString(KEY_CURRENT_USER, null) ?: return
        prefs.edit().remove("$KEY_ACTIVE_CART_PREFIX$username").apply()
    }
}
