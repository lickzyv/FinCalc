package com.lickzy.fincalc.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lickzy.fincalc.data.repository.RateQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.finCalcDataStore by preferencesDataStore(name = "fincalc_preferences")

enum class ThemeChoice { SYSTEM, LIGHT, DARK }

data class AppSettings(
    val theme: ThemeChoice = ThemeChoice.SYSTEM,
    val defaultCurrency: String = "INR",
)

/** Persists the few user preferences and last successful exchange quotes. */
class SettingsStore(context: Context) {
    private val store = context.applicationContext.finCalcDataStore
    private val themeKey = stringPreferencesKey("theme")
    private val currencyKey = stringPreferencesKey("default_currency")

    val settings: Flow<AppSettings> = store.data.map { preferences ->
        AppSettings(
            theme = preferences[themeKey]?.let { runCatching { ThemeChoice.valueOf(it) }.getOrNull() }
                ?: ThemeChoice.SYSTEM,
            defaultCurrency = preferences[currencyKey]?.takeIf { it in supportedCurrencyCodes } ?: "INR",
        )
    }

    suspend fun setTheme(theme: ThemeChoice) {
        store.edit { it[themeKey] = theme.name }
    }

    suspend fun setDefaultCurrency(code: String) {
        require(code in supportedCurrencyCodes)
        store.edit { it[currencyKey] = code }
    }

    suspend fun saveQuote(quote: RateQuote) {
        val key = stringPreferencesKey(cacheKey(quote.base, quote.quote))
        store.edit { preferences ->
            preferences[key] = "${quote.rate}|${quote.rateDate}|${quote.fetchedAtMillis}"
        }
    }

    suspend fun cachedQuote(base: String, quote: String): RateQuote? {
        val exactKey = stringPreferencesKey(cacheKey(base, quote))
        val reverseKey = stringPreferencesKey(cacheKey(quote, base))
        val preferences = store.data.first()
        return parseQuote(preferences[exactKey], base, quote)
            ?: parseQuote(preferences[reverseKey], quote, base)?.let {
                it.copy(base = base, quote = quote, rate = 1.0 / it.rate)
            }
    }

    private fun parseQuote(value: String?, base: String, quote: String): RateQuote? {
        val parts = value?.split('|') ?: return null
        if (parts.size != 3) return null
        val rate = parts[0].toDoubleOrNull()?.takeIf { it > 0.0 && it.isFinite() } ?: return null
        val fetchedAt = parts[2].toLongOrNull() ?: return null
        return RateQuote(base, quote, rate, parts[1], fetchedAt, isCached = true)
    }

    private fun cacheKey(base: String, quote: String) = "rate_${base.lowercase()}_${quote.lowercase()}"
}

val supportedCurrencyCodes = setOf("INR", "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "SGD", "NZD", "HKD")
