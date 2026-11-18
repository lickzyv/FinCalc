package com.lickzy.fincalc

import android.app.Application
import com.lickzy.fincalc.data.local.SettingsStore
import com.lickzy.fincalc.data.repository.CurrencyRepository

class FinCalcApplication : Application() {
    val settingsStore by lazy { SettingsStore(this) }
    val currencyRepository by lazy { CurrencyRepository(settingsStore) }
}
