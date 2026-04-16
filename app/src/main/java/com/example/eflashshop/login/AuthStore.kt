package com.example.eflashshop.login

import android.content.Context

object AuthStore {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_CURRENT_USER = "current_user"
    private const val KEY_CURRENT_EMAIL = "current_email"
    private const val KEY_CURRENT_ROLE = "current_role"
    private const val KEY_ACTIVE_CART_PREFIX = "active_cart_"
    private const val USER_PASSWORD_PREFIX = "user_"
    private const val USER_ROLE_PREFIX = "role_"
    private const val USER_NAME_PREFIX = "name_"

    const val ROLE_ADMIN = "admin"
    const val ROLE_BUYER = "buyer"
    const val ADMIN_USERNAME = "admin"
    const val ADMIN_EMAIL = "admin@eflashshop.local"
    const val ADMIN_PASSWORD = "admin123"

    fun registerUser(context: Context, username: String, email: String, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        ensureSystemAccounts(context)
        val normalizedEmail = email.trim().lowercase()
        val key = "$USER_PASSWORD_PREFIX$normalizedEmail"
        if (prefs.contains(key)) {
            return false
        }
        val displayName = username.trim().ifBlank { normalizedEmail.substringBefore("@") }
        prefs.edit()
            .putString(key, password)
            .putString("$USER_ROLE_PREFIX$normalizedEmail", ROLE_BUYER)
            .putString("$USER_NAME_PREFIX$normalizedEmail", displayName)
            .apply()
        return true
    }

    fun loginUser(context: Context, email: String, password: String): Boolean {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val normalizedEmail = email.trim().lowercase()
        val stored = prefs.getString("$USER_PASSWORD_PREFIX$normalizedEmail", null)
        if (stored == password) {
            val role = prefs.getString("$USER_ROLE_PREFIX$normalizedEmail", ROLE_BUYER) ?: ROLE_BUYER
            val displayName = prefs.getString(
                "$USER_NAME_PREFIX$normalizedEmail",
                normalizedEmail.substringBefore("@")
            ) ?: normalizedEmail.substringBefore("@")
            prefs.edit()
                .putString(KEY_CURRENT_USER, displayName)
                .putString(KEY_CURRENT_EMAIL, normalizedEmail)
                .putString(KEY_CURRENT_ROLE, role)
                .apply()
            return true
        }
        return false
    }

    fun isLoggedIn(context: Context): Boolean {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return !prefs.getString(KEY_CURRENT_EMAIL, null).isNullOrBlank()
    }

    fun getCurrentUser(context: Context): String? {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENT_USER, null)
    }

    fun getCurrentEmail(context: Context): String? {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENT_EMAIL, null)
    }

    fun getCurrentRole(context: Context): String {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val activeRole = prefs.getString(KEY_CURRENT_ROLE, null)
        if (!activeRole.isNullOrBlank()) {
            return activeRole
        }

        val currentEmail = prefs.getString(KEY_CURRENT_EMAIL, null)
        if (currentEmail.isNullOrBlank()) {
            return ROLE_BUYER
        }

        val derivedRole = prefs.getString("$USER_ROLE_PREFIX$currentEmail", ROLE_BUYER) ?: ROLE_BUYER
        prefs.edit().putString(KEY_CURRENT_ROLE, derivedRole).apply()
        return derivedRole
    }

    fun isAdmin(context: Context): Boolean = getCurrentRole(context) == ROLE_ADMIN

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_CURRENT_USER)
            .remove(KEY_CURRENT_EMAIL)
            .remove(KEY_CURRENT_ROLE)
            .apply()
    }

    fun setActiveCartId(context: Context, cartId: Long) {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val accountKey = prefs.getString(KEY_CURRENT_EMAIL, null)
            ?: prefs.getString(KEY_CURRENT_USER, null)
            ?: return
        prefs.edit().putLong("$KEY_ACTIVE_CART_PREFIX$accountKey", cartId).apply()
    }

    fun getActiveCartId(context: Context): Long? {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val accountKey = prefs.getString(KEY_CURRENT_EMAIL, null)
            ?: prefs.getString(KEY_CURRENT_USER, null)
            ?: return null
        val key = "$KEY_ACTIVE_CART_PREFIX$accountKey"
        return if (prefs.contains(key)) prefs.getLong(key, -1L) else null
    }

    fun clearActiveCartForCurrentUser(context: Context) {
        ensureSystemAccounts(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val accountKey = prefs.getString(KEY_CURRENT_EMAIL, null)
            ?: prefs.getString(KEY_CURRENT_USER, null)
            ?: return
        prefs.edit().remove("$KEY_ACTIVE_CART_PREFIX$accountKey").apply()
    }

    fun ensureSystemAccounts(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains("$USER_PASSWORD_PREFIX$ADMIN_EMAIL")) {
            prefs.edit()
                .putString("$USER_PASSWORD_PREFIX$ADMIN_EMAIL", ADMIN_PASSWORD)
                .putString("$USER_ROLE_PREFIX$ADMIN_EMAIL", ROLE_ADMIN)
                .putString("$USER_NAME_PREFIX$ADMIN_EMAIL", ADMIN_USERNAME)
                .apply()
        }
    }

    fun resetToAdminOnly(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        ensureSystemAccounts(context)
    }
}
