package com.example.homedeal.utils

import android.app.Activity
import android.content.Context
import com.example.homedeal.R

object ThemeUtils {
    private const val THEME_PREFS = "ThemePrefs"
    private const val THEME_KEY = "theme"
    private const val THEME_DEFAULT = "light"

    fun setTheme(context: Context, theme: String) {
        val sharedPreferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(THEME_KEY, theme)
        editor.apply()
    }

    fun getTheme(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(THEME_KEY, THEME_DEFAULT) ?: THEME_DEFAULT
    }

    fun applyTheme(activity: Activity) {
        val theme = getTheme(activity)
        if (theme == "dark") {
            activity.setTheme(R.style.AppTheme_Dark)
        } else {
            activity.setTheme(R.style.AppTheme)
        }
    }
}