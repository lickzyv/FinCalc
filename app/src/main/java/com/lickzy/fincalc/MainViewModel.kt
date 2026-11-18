package com.lickzy.fincalc

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lickzy.fincalc.data.local.AppSettings
import com.lickzy.fincalc.data.local.ThemeChoice
import com.lickzy.fincalc.data.repository.CurrencyRepository
import com.lickzy.fincalc.data.repository.QuoteResult
import com.lickzy.fincalc.data.repository.RateQuote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CurrencyUiState(
    val base: String = "INR",
    val quote: String = "USD",
    val rate: RateQuote? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as FinCalcApplication
    private val _settings = MutableStateFlow(AppSettings())
    val settings = _settings.asStateFlow()
    private val _currency = MutableStateFlow(CurrencyUiState())
    val currency = _currency.asStateFlow()

    init {
        viewModelScope.launch {
            app.settingsStore.settings.collect { _settings.value = it }
        }
    }

    fun setTheme(choice: ThemeChoice) {
        viewModelScope.launch { app.settingsStore.setTheme(choice) }
    }

    fun setDefaultCurrency(code: String) {
        viewModelScope.launch { app.settingsStore.setDefaultCurrency(code) }
    }

    fun refreshCurrency(base: String, quote: String) {
        val current = _currency.value
        if (current.base == base && current.quote == quote && current.isLoading) return
        viewModelScope.launch {
            _currency.value = CurrencyUiState(base = base, quote = quote, isLoading = true)
            when (val result = app.currencyRepository.getRate(base, quote)) {
                is QuoteResult.Success -> _currency.value = CurrencyUiState(base, quote, rate = result.quote)
                is QuoteResult.Failure -> _currency.value = CurrencyUiState(base, quote, error = result.message)
            }
        }
    }
}
