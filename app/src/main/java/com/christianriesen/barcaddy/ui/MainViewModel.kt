package com.christianriesen.barcaddy.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.christianriesen.barcaddy.BarcaddyApp
import com.christianriesen.barcaddy.data.Card
import com.christianriesen.barcaddy.data.CardRepository
import com.christianriesen.barcaddy.data.Settings
import com.christianriesen.barcaddy.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    app: Application,
    private val cards: CardRepository,
    private val settings: SettingsRepository,
) : AndroidViewModel(app) {

    val cardList: StateFlow<List<Card>> = cards.observeAll()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val settingsState: StateFlow<Settings> = settings.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, Settings())

    fun saveCard(card: Card) = viewModelScope.launch { cards.save(card) }
    fun deleteCard(id: String) = viewModelScope.launch { cards.delete(id) }
    fun deleteAll() = viewModelScope.launch { cards.deleteAll() }
    fun reorder(ids: List<String>) = viewModelScope.launch { cards.reorder(ids) }
    fun replaceAll(list: List<Card>) = viewModelScope.launch { cards.replaceAll(list) }
    suspend fun snapshot(): List<Card> = cards.all()
    suspend fun findCard(id: String): Card? = cards.findById(id)

    fun setDarkMode(on: Boolean) = viewModelScope.launch { settings.setDarkMode(on) }
    fun setKeepAwake(on: Boolean) = viewModelScope.launch { settings.setKeepAwake(on) }
    fun setBoostBrightness(on: Boolean) = viewModelScope.launch { settings.setBoostBrightness(on) }
    fun setShowCodeValue(on: Boolean) = viewModelScope.launch { settings.setShowCodeValue(on) }

    companion object {
        fun factory(app: BarcaddyApp): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    MainViewModel(app, app.cardRepository, app.settingsRepository) as T
            }
    }
}
