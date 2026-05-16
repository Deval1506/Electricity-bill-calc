package com.electricitybill.calculator

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataStore {

    private const val PREFS_NAME = "electricity_prefs"
    private const val KEY_RATE   = "ebrate"
    private const val KEY_HIST   = "ebhist"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Rate ──────────────────────────────────────────────────────────────

    fun getRate(ctx: Context): Double =
        prefs(ctx).getFloat(KEY_RATE, 7.5f).toDouble()

    fun saveRate(ctx: Context, rate: Double) {
        prefs(ctx).edit().putFloat(KEY_RATE, rate.toFloat()).apply()
    }

    // ── History ───────────────────────────────────────────────────────────

    fun getHistory(ctx: Context): MutableList<BillRecord> {
        val json = prefs(ctx).getString(KEY_HIST, null) ?: return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<BillRecord>>() {}.type
            Gson().fromJson<MutableList<BillRecord>>(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun saveHistory(ctx: Context, hist: List<BillRecord>) {
        prefs(ctx).edit().putString(KEY_HIST, Gson().toJson(hist)).apply()
    }

    fun clearHistory(ctx: Context) {
        prefs(ctx).edit().remove(KEY_HIST).apply()
    }
}
