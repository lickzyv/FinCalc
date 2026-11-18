package com.lickzy.fincalc.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.math.RoundingMode
import kotlin.math.abs

/** Formats amounts with Indian grouping for rupees and conventional international grouping otherwise. */
fun formatCurrency(amount: Double, currencyCode: String, decimals: Int? = null): String {
    require(amount.isFinite()) { "Amount must be finite." }
    val currency = Currency.getInstance(currencyCode)
    val locale = if (currencyCode == "INR") Locale.forLanguageTag("en-IN") else Locale.US
    val digits = (decimals ?: currency.defaultFractionDigits).coerceIn(0, 6)
    if (currencyCode == "INR") {
        val numberText = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = digits
            maximumFractionDigits = digits
            roundingMode = RoundingMode.HALF_UP
        }.format(abs(amount))
        val pieces = numberText.split('.')
        val whole = pieces[0].replace(",", "")
        val indianWhole = if (whole.length <= 3) whole else {
            val head = whole.dropLast(3)
            val groupedHead = head.reversed().chunked(2).map { it.reversed() }.asReversed().joinToString(",")
            "$groupedHead,${whole.takeLast(3)}"
        }
        val sign = if (amount < 0.0) "-" else ""
        val fraction = pieces.getOrNull(1)?.let { ".$it" }.orEmpty()
        return "$sign${currency.getSymbol(locale)}$indianWhole$fraction"
    }
    return NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = currency
        minimumFractionDigits = digits
        maximumFractionDigits = digits
        roundingMode = RoundingMode.HALF_UP
    }.format(amount)
}

fun formatNumber(amount: Double, decimals: Int = 2): String {
    require(amount.isFinite()) { "Number must be finite." }
    return NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN")).apply {
        minimumFractionDigits = decimals.coerceIn(0, 6)
        maximumFractionDigits = decimals.coerceIn(0, 6)
    }.format(amount)
}

fun parsePositiveNumber(raw: String, allowZero: Boolean = false): Double? {
    val parsed = raw.trim().replace(",", "").toDoubleOrNull() ?: return null
    if (!parsed.isFinite()) return null
    return parsed.takeIf { if (allowZero) it >= 0.0 else it > 0.0 }
}
