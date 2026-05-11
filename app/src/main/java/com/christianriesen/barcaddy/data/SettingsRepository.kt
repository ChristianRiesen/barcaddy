package com.christianriesen.barcaddy.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "barcaddy_prefs")

data class Settings(
    val darkMode: Boolean = false,
    val keepAwake: Boolean = true,
    val boostBrightness: Boolean = true,
    val showCodeValue: Boolean = false,
    val lastViewedCardId: String? = null,
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val DARK = booleanPreferencesKey("dark_mode")
        val KEEP_AWAKE = booleanPreferencesKey("keep_awake")
        val BOOST = booleanPreferencesKey("boost_brightness")
        val SHOW_CODE = booleanPreferencesKey("show_code_value")
        val LAST_VIEWED = stringPreferencesKey("last_viewed_card_id")
    }

    val settings: Flow<Settings> = context.dataStore.data.map { p ->
        Settings(
            darkMode = p[Keys.DARK] ?: false,
            keepAwake = p[Keys.KEEP_AWAKE] ?: true,
            boostBrightness = p[Keys.BOOST] ?: true,
            showCodeValue = p[Keys.SHOW_CODE] ?: false,
            lastViewedCardId = p[Keys.LAST_VIEWED],
        )
    }

    suspend fun snapshot(): Settings = settings.first()

    suspend fun setDarkMode(on: Boolean) = context.dataStore.edit { it[Keys.DARK] = on }
    suspend fun setKeepAwake(on: Boolean) = context.dataStore.edit { it[Keys.KEEP_AWAKE] = on }
    suspend fun setBoostBrightness(on: Boolean) = context.dataStore.edit { it[Keys.BOOST] = on }
    suspend fun setShowCodeValue(on: Boolean) = context.dataStore.edit { it[Keys.SHOW_CODE] = on }
    suspend fun setLastViewedCardId(id: String?) = context.dataStore.edit { prefs ->
        if (id == null) prefs.remove(Keys.LAST_VIEWED) else prefs[Keys.LAST_VIEWED] = id
    }
}
