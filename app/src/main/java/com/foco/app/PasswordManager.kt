package com.foco.app

import android.content.Context
import java.security.MessageDigest

object PasswordManager {
    private const val PREFS = "foco_prefs"
    private const val KEY_PASS_HASH = "admin_pass_hash"

    private fun hash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isPasswordSet(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.contains(KEY_PASS_HASH)
    }

    fun setPassword(context: Context, password: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PASS_HASH, hash(password)).apply()
    }

    fun checkPassword(context: Context, password: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_PASS_HASH, null) ?: return false
        return stored == hash(password)
    }
}
