package com.foco.app

import android.content.Context
import java.util.concurrent.TimeUnit

/**
 * Gerencia o estado do bloqueio de auto-controle.
 * Tudo fica em SharedPreferences comuns: o usuário (dono do celular) sempre
 * pode ver e, com fricção proposital, encerrar o bloqueio. Nada é escondido
 * do dono do aparelho.
 */
object LockManager {
    private const val PREFS = "foco_prefs"
    private const val KEY_TARGET_PACKAGE = "target_package"
    private const val KEY_START_TIME = "start_time"
    private const val KEY_DURATION_DAYS = "duration_days"
    private const val KEY_ACTIVE = "active"

    fun startLock(context: Context, targetPackage: String, durationDays: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_TARGET_PACKAGE, targetPackage)
            .putLong(KEY_START_TIME, System.currentTimeMillis())
            .putInt(KEY_DURATION_DAYS, durationDays)
            .putBoolean(KEY_ACTIVE, true)
            .apply()
    }

    fun stopLock(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ACTIVE, false).apply()
    }

    fun isActive(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ACTIVE, false)) return false
        // Expira automaticamente ao fim do prazo
        if (daysRemaining(context) <= 0) {
            stopLock(context)
            return false
        }
        return true
    }

    fun getTargetPackage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_TARGET_PACKAGE, "") ?: ""
    }

    fun daysRemaining(context: Context): Long {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val start = prefs.getLong(KEY_START_TIME, 0)
        val durationDays = prefs.getInt(KEY_DURATION_DAYS, 0)
        if (start == 0L) return 0
        val elapsedMs = System.currentTimeMillis() - start
        val elapsedDays = TimeUnit.MILLISECONDS.toDays(elapsedMs)
        val remaining = durationDays - elapsedDays
        return if (remaining < 0) 0 else remaining
    }
}
