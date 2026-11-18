package com.lickzy.fincalc.data.repository

import com.lickzy.fincalc.data.api.FrankfurterApi
import com.lickzy.fincalc.data.local.SettingsStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class RateQuote(
    val base: String,
    val quote: String,
    val rate: Double,
    val rateDate: String,
    val fetchedAtMillis: Long,
    val isCached: Boolean,
)

sealed interface QuoteResult {
    data class Success(val quote: RateQuote) : QuoteResult
    data class Failure(val message: String) : QuoteResult
}

class CurrencyRepository(
    private val settingsStore: SettingsStore,
    private val api: FrankfurterApi = FrankfurterApi(),
) {
    suspend fun getRate(base: String, quote: String): QuoteResult = withContext(Dispatchers.IO) {
        if (base == quote) {
            return@withContext QuoteResult.Success(
                RateQuote(base, quote, 1.0, "same currency", System.currentTimeMillis(), isCached = false),
            )
        }
        try {
            val onlineQuote = api.fetchRate(base, quote)
            settingsStore.saveQuote(onlineQuote)
            QuoteResult.Success(onlineQuote)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            cachedOfflineResult(settingsStore.cachedQuote(base, quote))
        }
    }
}

fun cachedOfflineResult(cached: RateQuote?): QuoteResult = cached?.let {
    QuoteResult.Success(it.copy(isCached = true))
} ?: QuoteResult.Failure("Rates could not be refreshed. Check your connection and try again.")

/** A pure helper keeps conversion and stale-data labeling easy to test. */
fun convertCurrencyAmount(amount: Double, rate: Double): Double {
    require(amount >= 0.0 && amount.isFinite()) { "Amount must be a valid non-negative number." }
    require(rate > 0.0 && rate.isFinite()) { "Exchange rate must be greater than zero." }
    val converted = amount * rate
    require(converted.isFinite()) { "Converted amount is too large to display." }
    return converted
}
