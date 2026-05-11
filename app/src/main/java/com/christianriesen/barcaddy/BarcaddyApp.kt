package com.christianriesen.barcaddy

import android.app.Application
import com.christianriesen.barcaddy.data.CardDatabase
import com.christianriesen.barcaddy.data.CardRepository
import com.christianriesen.barcaddy.data.SettingsRepository

class BarcaddyApp : Application() {
    lateinit var cardRepository: CardRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        cardRepository = CardRepository(CardDatabase.get(this).cardDao())
        settingsRepository = SettingsRepository(this)
    }
}
