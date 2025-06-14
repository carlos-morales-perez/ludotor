package com.ludotor.ludotor.fragment

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ludotor.ludotor.R
import java.util.Calendar
import java.util.Locale

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

        // Configurar el TimePicker para la preferencia de hora
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
        updateThemeSummary() // Actualizar resumen del tema al mostrar
        updateNotificationTimeSummary() // Actualizar resumen de la hora
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
                // Aquí deberías (re)programar tus notificaciones con la nueva hora
                // scheduleDailyReminder(requireContext()) // Ejemplo
            }
            KEY_NOTIFICATIONS_ENABLED -> {
                // Si se deshabilitan, cancelar notificaciones programadas
                // Si se habilitan, programarlas (quizás con la hora guardada)
                val enabled = sharedPreferences?.getBoolean(KEY_NOTIFICATIONS_ENABLED, true) ?: true
                if (enabled) {
                    // scheduleDailyReminder(requireContext())
                } else {
                    // cancelDailyReminder(requireContext())
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
        // No es necesario recrear la actividad aquí si el tema se aplica en toda la app
        // y se maneja correctamente en el Application o Activity base.
    }

    private fun updateThemeSummary() {
        val themePreference: ListPreference? = findPreference(KEY_THEME)
        themePreference?.summary = themePreference?.entry // Muestra la opción seleccionada
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentHour = sharedPreferences.getInt("notification_hour", 20) // Hora por defecto 8 PM
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
            // onSharedPreferenceChanged será llamado para KEY_NOTIFICATION_TIME si lo registras como Preference
            // o llamas a updateNotificationTimeSummary manualmente
            updateNotificationTimeSummary()
            // Aquí deberías (re)programar tus notificaciones con la nueva hora
            // scheduleDailyReminder(requireContext()) // Ejemplo
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

    // --- Funciones de ejemplo para programar/cancelar notificaciones ---
    // Deberás implementar esto usando WorkManager para notificaciones persistentes
    // private fun scheduleDailyReminder(context: Context) {
    //     val hour = sharedPreferences.getInt("notification_hour", 20)
    //     val minute = sharedPreferences.getInt("notification_minute", 0)
    //     Log.d("SettingsFragment", "Programando recordatorio para las $hour:$minute")
    //     // Implementar con WorkManager
    // }

    // private fun cancelDailyReminder(context: Context) {
    //     Log.d("SettingsFragment", "Cancelando recordatorio diario")
    //     // Implementar con WorkManager
    // }
}