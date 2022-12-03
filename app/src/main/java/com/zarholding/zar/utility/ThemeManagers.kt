package com.zarholding.zar.utility

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject


@Module
@InstallIn(SingletonComponent::class)
class ThemeManagers @Inject constructor(@ApplicationContext private val context: Context, private val sharedPreferences: SharedPreferences) {

    private val sharePreferencesKey = "themeSharedPreferences"

    //______________________________________________________________________________________________ saveThemeToSharePreferences
    private fun saveThemeToSharePreferences(theme: Int): Boolean {
        val editor = sharedPreferences.edit()
        editor.putInt(sharePreferencesKey, theme)
        editor.apply()
        return true
    }
    //______________________________________________________________________________________________ saveThemeToSharePreferences


    //______________________________________________________________________________________________ getThemFromSharePreferences
    private fun getThemFromSharePreferences(): Int {
        return sharedPreferences.getInt(sharePreferencesKey, -1)
    }
    //______________________________________________________________________________________________ getThemFromSharePreferences


    //______________________________________________________________________________________________ applicationTheme
    fun applicationTheme(): Int {
        val theme = getThemFromSharePreferences()
        return if (theme != -1)
            theme
        else
            return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    }
    //______________________________________________________________________________________________ applicationTheme


    //______________________________________________________________________________________________ changeApplicationTheme
    fun changeApplicationTheme(theme: Int) {
        when (theme) {
            Configuration.UI_MODE_NIGHT_YES -> {
                saveThemeToSharePreferences(Configuration.UI_MODE_NIGHT_YES)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                saveThemeToSharePreferences(Configuration.UI_MODE_NIGHT_NO)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
    //______________________________________________________________________________________________ changeApplicationTheme


}
