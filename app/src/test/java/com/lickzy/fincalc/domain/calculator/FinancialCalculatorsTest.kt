package com.lickzy.fincalc.domain.calculator

import com.lickzy.fincalc.data.repository.QuoteResult
import com.lickzy.fincalc.data.repository.RateQuote
import com.lickzy.fincalc.data.repository.cachedOfflineResult
import com.lickzy.fincalc.data.repository.convertCurrencyAmount
import com.lickzy.fincalc.utils.formatCurrency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialCalculatorsTest {
    @Test fun simpleInterestUsesMonthsAsFractionsOfAYear() {
        val result = simpleInterest(100_000.0, 10.0, 6.0, TimeUnit.MONTHS)
        assertEquals(5_000.0, result.interest, 0.001)
        assertEquals(105_000.0, result.finalAmount, 0.001)
    }

    @Test fun compoundInterestUsesSelectedCompoundingFrequency() {
        val result = compoundInterest(100_000.0, 12.0, 2.0, TimeUnit.YEARS, CompoundingFrequency.MONTHLY)
        assertEquals(26_973.46, result.interest, 0.02)
        assertEquals(126_973.46, result.finalAmount, 0.02)
    }

    @Test fun emiCalculatesRepaymentAndZeroRateLoans() {
        val result = calculateEmi(1_000_000.0, 12.0, 1.0, TimeUnit.YEARS)
        assertEquals(88_848.79, result.monthlyPayment, 0.02)
        assertEquals(66_185.46, result.totalInterest, 0.1)
        assertEquals(1_066_185.46, result.totalPayment, 0.1)

        val noInterest = calculateEmi(120_000.0, 0.0, 12.0, TimeUnit.MONTHS)
        assertEquals(10_000.0, noInterest.monthlyPayment, 0.001)
        assertEquals(0.0, noInterest.totalInterest, 0.001)
    }

    @Test fun percentageModesCalculateValuesAndChanges() {
        assertEquals(25.0, calculatePercentage(PercentageMode.PERCENT_OF, 25.0, 100.0).value, 0.001)
        assertEquals(25.0, calculatePercentage(PercentageMode.IS_WHAT_PERCENT, 25.0, 100.0).value, 0.001)
        val increase = calculatePercentage(PercentageMode.INCREASE, 200.0, 10.0)
        assertEquals(220.0, increase.value, 0.001)
        assertEquals(20.0, increase.change!!, 0.001)
        val decrease = calculatePercentage(PercentageMode.DECREASE, 200.0, 25.0)
        assertEquals(150.0, decrease.value, 0.001)
        assertEquals(50.0, decrease.change!!, 0.001)
    }

    @Test fun discountReturnsSavingsAndFinalPrice() {
        val result = calculateDiscount(2_000.0, 15.0)
        assertEquals(300.0, result.discountAmount, 0.001)
        assertEquals(1_700.0, result.finalPrice, 0.001)
        assertEquals(15.0, result.savingsPercent, 0.001)
    }

    @Test fun taxIncludesRebatesMarginalReliefAndCess() {
        val oldRebate = estimateIndianIncomeTax(500_000.0, TaxRegime.OLD)
        assertEquals(0.0, oldRebate.estimatedTax, 0.001)

        val newRebate = estimateIndianIncomeTax(1_200_000.0, TaxRegime.NEW)
        assertEquals(0.0, newRebate.estimatedTax, 0.001)

        val marginal = estimateIndianIncomeTax(1_210_000.0, TaxRegime.NEW)
        assertEquals(61_500.0, marginal.slabTax, 0.001)
        assertEquals(10_000.0, marginal.taxBeforeSurcharge, 0.001)
        assertEquals(400.0, marginal.cess, 0.001)

        val oldTax = estimateIndianIncomeTax(550_000.0, TaxRegime.OLD)
        assertEquals(22_500.0, oldTax.taxBeforeSurcharge, 0.001)
        assertEquals(900.0, oldTax.cess, 0.001)
    }

    @Test fun surchargeMarginalReliefAvoidsAThresholdJump() {
        val justOver = estimateIndianIncomeTax(20_010_000.0, TaxRegime.NEW)
        assertTrue(justOver.surcharge > 0.0)
        assertTrue(justOver.surcharge < justOver.taxBeforeSurcharge * .25)

        val oldAtThreeCrore = estimateIndianIncomeTax(30_000_000.0, TaxRegime.OLD)
        assertEquals(oldAtThreeCrore.taxBeforeSurcharge * .25, oldAtThreeCrore.surcharge, .01)
        val oldAtSixCrore = estimateIndianIncomeTax(60_000_000.0, TaxRegime.OLD)
        assertEquals(oldAtSixCrore.taxBeforeSurcharge * .37, oldAtSixCrore.surcharge, .01)
        val newAtOneAndHalfCrore = estimateIndianIncomeTax(15_000_000.0, TaxRegime.NEW)
        assertEquals(newAtOneAndHalfCrore.taxBeforeSurcharge * .15, newAtOneAndHalfCrore.surcharge, .01)
    }

    @Test fun indianAndInternationalCurrencyFormattingUseExpectedGrouping() {
        assertEquals("₹1,00,000.00", formatCurrency(100_000.0, "INR"))
        assertEquals("$1,234.50", formatCurrency(1_234.5, "USD"))
        assertEquals("¥1,235", formatCurrency(1_234.5, "JPY"))
        assertEquals(200.0, convertCurrencyAmount(250.0, .8), 0.001)
    }

    @Test fun offlineCurrencyFallbackIsMarkedAsCached() {
        val cached = RateQuote("INR", "USD", .012, "2026-11-18", 1_800_000_000_000, isCached = true)
        val result = cachedOfflineResult(cached) as QuoteResult.Success
        assertTrue(result.quote.isCached)
        assertEquals(.012, result.quote.rate, 0.0)
        assertTrue(cachedOfflineResult(null) is QuoteResult.Failure)
    }

    @Test fun invalidInputsFailClearly() {
        assertFalse(runCatching { simpleInterest(0.0, 10.0, 1.0, TimeUnit.YEARS) }.isSuccess)
        assertFalse(runCatching { calculateEmi(1_000.0, -1.0, 1.0, TimeUnit.YEARS) }.isSuccess)
        assertFalse(runCatching { calculatePercentage(PercentageMode.IS_WHAT_PERCENT, 10.0, 0.0) }.isSuccess)
        assertFalse(runCatching { calculateDiscount(100.0, 101.0) }.isSuccess)
        assertFalse(runCatching { estimateIndianIncomeTax(-1.0, TaxRegime.NEW) }.isSuccess)
        assertFalse(runCatching { convertCurrencyAmount(10.0, 0.0) }.isSuccess)
    }
}
