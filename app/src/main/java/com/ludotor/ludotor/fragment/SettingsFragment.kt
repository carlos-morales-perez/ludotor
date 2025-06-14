package com.ludotor.ludotor.fragment

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ludotor.ludotor.R
import com.ludotor.ludotor.worker.NotificationWorker
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val KEY_THEME = "theme_preference"
        const val KEY_NOTIFICATION_TIME = "notification_time_preference"
        const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val timePreference: Preference? = findPreference(KEY_NOTIFICATION_TIME)
        timePreference?.setOnPreferenceClickListener {
            showTimePickerDialog()
            true
        }
        updateNotificationTimeSummary()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateThemeSummary()
        updateNotificationTimeSummary()
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_THEME -> {
                val themeValue = sharedPreferences?.getString(KEY_THEME, "system")
                applyTheme(themeValue)
                updateThemeSummary()
            }
            KEY_NOTIFICATION_TIME -> {
                updateNotificationTimeSummary()
                scheduleOrCancelDailyReminder()
            }
            KEY_NOTIFICATIONS_ENABLED -> {
                val enabled = sharedPreferences?.getBoolean(KEY_NOTIFICATIONS_ENABLED, true) ?: true
                if (enabled) {
                    scheduleOrCancelDailyReminder()
                } else {
                    scheduleOrCancelDailyReminder()
                }
            }
        }
    }

    private fun applyTheme(themeValue: String?) {
        when (themeValue) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun updateThemeSummary() {
        val themePreference: ListPreference? = findPreference(KEY_THEME)
        themePreference?.summary = themePreference.entry
    }

    private fun showTimePickerDialog() {
        val currentHour = sharedPreferences.getInt("notification_hour", 20)
        val currentMinute = sharedPreferences.getInt("notification_minute", 0)

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(if (DateFormat.is24HourFormat(requireContext())) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTitleText("Seleccionar hora del recordatorio")
            .build()

        picker.addOnPositiveButtonClickListener {
            sharedPreferences.edit()
                .putInt("notification_hour", picker.hour)
                .putInt("notification_minute", picker.minute)
                .apply()
            updateNotificationTimeSummary()
            scheduleOrCancelDailyReminder()
        }
        picker.show(parentFragmentManager, "TIME_PICKER_TAG")
    }

    private fun updateNotificationTimeSummary() {
        val timePreference: Preference? = findPreference(KEY_NOTIFICATION_TIME)
        if (sharedPreferences.contains("notification_hour")) {
            val hour = sharedPreferences.getInt("notification_hour", 20)
            val minute = sharedPreferences.getInt("notification_minute", 0)
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            // Formatear la hora para mostrarla en el resumen
            val timeFormat = if (DateFormat.is24HourFormat(requireContext())) "HH:mm" else "hh:mm a"
            timePreference?.summary = SimpleDateFormat(timeFormat,
                Locale.getDefault()).format(calendar.time)
        } else {
            timePreference?.summary = "No establecida"
        }
    }

    private fun scheduleOrCancelDailyReminder() {
        val workManager = WorkManager.getInstance(requireContext())
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)

        val uniqueWorkName = "dailyLoanReminder"

        if (notificationsEnabled) {
            val hour = sharedPreferences.getInt("notification_hour", 20)
            val minute = sharedPreferences.getInt("notification_minute", 0)

            val currentTime = Calendar.getInstance()
            val dueTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (dueTime.before(currentTime)) {
                dueTime.add(Calendar.HOUR_OF_DAY, 24)
            }

            val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis

            val constraints = Constraints.Builder()
//                .setRequiresDeviceIdle(true)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            Log.d("Scheduler", "Current Time: ${currentTime.time}")
            Log.d("Scheduler", "Due Time Set: ${dueTime.time}")
            Log.d("Scheduler", "Calculated Initial Delay (ms): $initialDelay")
            Log.d("Scheduler", "Calculated Initial Delay (minutes): ${TimeUnit.MILLISECONDS.toMinutes(initialDelay)}")

            val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
        } else {
            workManager.cancelUniqueWork(uniqueWorkName)
        }
    }
}