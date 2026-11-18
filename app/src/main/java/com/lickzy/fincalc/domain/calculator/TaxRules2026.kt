package com.lickzy.fincalc.domain.calculator

/** Tax-year settings live here so a future Finance Act update has one obvious place to start. */
object TaxRules2026 {
    const val TAX_YEAR = "2026–27"
    const val OLD_REGIME_REBATE_LIMIT = 500_000.0
    const val OLD_REGIME_MAX_REBATE = 12_500.0
    const val NEW_REGIME_REBATE_LIMIT = 1_200_000.0
    const val NEW_REGIME_MAX_REBATE = 60_000.0
    const val NEW_REGIME_MARGINAL_RELIEF_LIMIT = NEW_REGIME_REBATE_LIMIT
    const val CESS_RATE = 0.04

    // Limits are marginal: the rate applies only to income within each band.
    val oldRegimeSlabs = listOf(
        TaxSlab(250_000.0, 0.00),
        TaxSlab(500_000.0, 0.05),
        TaxSlab(1_000_000.0, 0.20),
        TaxSlab(Double.POSITIVE_INFINITY, 0.30),
    )
    val newRegimeSlabs = listOf(
        TaxSlab(400_000.0, 0.00),
        TaxSlab(800_000.0, 0.05),
        TaxSlab(1_200_000.0, 0.10),
        TaxSlab(1_600_000.0, 0.15),
        TaxSlab(2_000_000.0, 0.20),
        TaxSlab(2_400_000.0, 0.25),
        TaxSlab(Double.POSITIVE_INFINITY, 0.30),
    )

    // These rates apply to ordinary income. Special-rate capital gains/dividend income
    // can have a separate surcharge cap and is intentionally outside this estimator.
    val oldSurchargeBands = listOf(
        SurchargeBand(5_000_000.0, 0.10),
        SurchargeBand(10_000_000.0, 0.10),
        SurchargeBand(20_000_000.0, 0.15),
        SurchargeBand(50_000_000.0, 0.25),
        SurchargeBand(Double.POSITIVE_INFINITY, 0.37),
    )
    val newSurchargeBands = listOf(
        SurchargeBand(5_000_000.0, 0.10),
        SurchargeBand(10_000_000.0, 0.10),
        SurchargeBand(20_000_000.0, 0.15),
        SurchargeBand(Double.POSITIVE_INFINITY, 0.25),
    )
}

data class TaxSlab(val upperLimit: Double, val rate: Double)
data class SurchargeBand(val upperLimit: Double, val rate: Double)
enum class TaxRegime(val displayName: String) { OLD("Old regime"), NEW("New regime") }

data class TaxEstimate(
    val regime: TaxRegime,
    val taxableIncome: Double,
    val slabTax: Double,
    val rebateAndRelief: Double,
    val taxBeforeSurcharge: Double,
    val surcharge: Double,
    val cess: Double,
    val estimatedTax: Double,
) {
    val effectiveRate: Double get() = if (taxableIncome == 0.0) 0.0 else estimatedTax / taxableIncome
}

/**
 * Estimates ordinary income tax for a resident individual below age 60.
 * Input is taxable income after deductions; special-rate income is not included.
 */
fun estimateIndianIncomeTax(taxableIncome: Double, regime: TaxRegime): TaxEstimate {
    require(taxableIncome >= 0.0 && taxableIncome.isFinite()) { "Income cannot be negative." }

    val slabTax = slabTax(taxableIncome, regime)
    val taxAfterRebateAndMarginalRelief = when (regime) {
        TaxRegime.OLD -> if (taxableIncome <= TaxRules2026.OLD_REGIME_REBATE_LIMIT) {
            maxOf(0.0, slabTax - TaxRules2026.OLD_REGIME_MAX_REBATE)
        } else slabTax
        TaxRegime.NEW -> if (taxableIncome <= TaxRules2026.NEW_REGIME_REBATE_LIMIT) {
            maxOf(0.0, slabTax - TaxRules2026.NEW_REGIME_MAX_REBATE)
        } else {
            // The new regime's marginal relief caps tax just above ₹12 lakh at the
            // extra income earned, as described in the Income Tax Department FAQ.
            minOf(slabTax, taxableIncome - TaxRules2026.NEW_REGIME_MARGINAL_RELIEF_LIMIT)
        }
    }
    val band = surchargeBand(taxableIncome, regime)
    val rawSurcharge = taxAfterRebateAndMarginalRelief * band.rate
    val previousLimit = previousSurchargeLimit(taxableIncome, regime)
    val surcharge = if (previousLimit == null || rawSurcharge == 0.0) {
        rawSurcharge
    } else {
        // Marginal relief prevents the surcharge from making the bill jump by more
        // than the income above the band threshold.
        val taxAtLimit = taxAfterReliefBeforeSurcharge(previousLimit, regime)
        minOf(rawSurcharge, maxOf(0.0, taxAtLimit + taxableIncome - previousLimit - taxAfterRebateAndMarginalRelief))
    }
    val taxBeforeSurcharge = taxAfterRebateAndMarginalRelief
    val cess = (taxBeforeSurcharge + surcharge) * TaxRules2026.CESS_RATE
    val relief = slabTax - taxAfterRebateAndMarginalRelief
    return TaxEstimate(
        regime = regime,
        taxableIncome = taxableIncome,
        slabTax = slabTax,
        rebateAndRelief = relief,
        taxBeforeSurcharge = taxBeforeSurcharge,
        surcharge = surcharge,
        cess = cess,
        estimatedTax = taxBeforeSurcharge + surcharge + cess,
    )
}

fun slabTax(income: Double, regime: TaxRegime): Double {
    val slabs = if (regime == TaxRegime.OLD) TaxRules2026.oldRegimeSlabs else TaxRules2026.newRegimeSlabs
    var lowerLimit = 0.0
    var total = 0.0
    for (slab in slabs) {
        val taxableInSlab = (income.coerceAtMost(slab.upperLimit) - lowerLimit).coerceAtLeast(0.0)
        total += taxableInSlab * slab.rate
        if (income <= slab.upperLimit) break
        lowerLimit = slab.upperLimit
    }
    return total
}

private fun taxAfterReliefBeforeSurcharge(income: Double, regime: TaxRegime): Double {
    val rawTax = slabTax(income, regime)
    return when (regime) {
        TaxRegime.OLD -> if (income <= TaxRules2026.OLD_REGIME_REBATE_LIMIT) {
            maxOf(0.0, rawTax - TaxRules2026.OLD_REGIME_MAX_REBATE)
        } else rawTax
        TaxRegime.NEW -> if (income <= TaxRules2026.NEW_REGIME_REBATE_LIMIT) {
            maxOf(0.0, rawTax - TaxRules2026.NEW_REGIME_MAX_REBATE)
        } else minOf(rawTax, income - TaxRules2026.NEW_REGIME_MARGINAL_RELIEF_LIMIT)
    }
}

private fun surchargeBand(income: Double, regime: TaxRegime): SurchargeBand {
    if (income <= 5_000_000.0) return SurchargeBand(5_000_000.0, 0.0)
    val bands = if (regime == TaxRegime.OLD) TaxRules2026.oldSurchargeBands else TaxRules2026.newSurchargeBands
    return bands.first { income <= it.upperLimit }
}

private fun previousSurchargeLimit(income: Double, regime: TaxRegime): Double? {
    val bands = if (regime == TaxRegime.OLD) TaxRules2026.oldSurchargeBands else TaxRules2026.newSurchargeBands
    val index = bands.indexOfFirst { income <= it.upperLimit }
    if (index <= 0) return null
    return bands[index - 1].upperLimit.takeIf { it.isFinite() }
}
