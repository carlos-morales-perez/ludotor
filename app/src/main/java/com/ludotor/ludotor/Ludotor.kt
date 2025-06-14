package com.ludotor.ludotor

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.ludotor.ludotor.fragment.SettingsFragment

class Ludotor : Application() {
    override fun onCreate() {
        super.onCreate()
        applyAppTheme()
    }

    private fun applyAppTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeValue = sharedPreferences.getString(SettingsFragment.KEY_THEME, "system")
        when (themeValue) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}