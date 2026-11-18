package com.lickzy.fincalc.domain.calculator

import kotlin.math.pow
import kotlin.math.roundToInt

enum class TimeUnit(val yearsPerUnit: Double) {
    MONTHS(1.0 / 12.0), YEARS(1.0),
}

enum class CompoundingFrequency(val timesPerYear: Int, val label: String) {
    ANNUALLY(1, "Annually"),
    SEMI_ANNUALLY(2, "Semi-annually"),
    QUARTERLY(4, "Quarterly"),
    MONTHLY(12, "Monthly"),
    DAILY(365, "Daily"),
}

data class InterestResult(val interest: Double, val finalAmount: Double)

fun simpleInterest(principal: Double, annualRatePercent: Double, time: Double, unit: TimeUnit): InterestResult {
    require(principal > 0.0 && principal.isFinite()) { "Enter a principal greater than zero." }
    require(annualRatePercent >= 0.0 && annualRatePercent.isFinite()) { "Rate cannot be negative." }
    require(time > 0.0 && time.isFinite()) { "Enter a time period greater than zero." }

    val interest = principal * (annualRatePercent / 100.0) * time * unit.yearsPerUnit
    val finalAmount = principal + interest
    require(interest.isFinite() && finalAmount.isFinite()) { "These values are too large to calculate." }
    return InterestResult(interest, finalAmount)
}

fun compoundInterest(
    principal: Double,
    annualRatePercent: Double,
    time: Double,
    unit: TimeUnit,
    frequency: CompoundingFrequency,
): InterestResult {
    require(principal > 0.0 && principal.isFinite()) { "Enter a principal greater than zero." }
    require(annualRatePercent >= 0.0 && annualRatePercent.isFinite()) { "Rate cannot be negative." }
    require(time > 0.0 && time.isFinite()) { "Enter a time period greater than zero." }

    val years = time * unit.yearsPerUnit
    val periods = frequency.timesPerYear * years
    val finalAmount = principal * (1.0 + annualRatePercent / (100.0 * frequency.timesPerYear)).pow(periods)
    require(finalAmount.isFinite()) { "These values are too large to calculate." }
    return InterestResult(finalAmount - principal, finalAmount)
}

data class EmiResult(
    val monthlyPayment: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val principal: Double,
) {
    val interestShare: Double get() = if (totalPayment == 0.0) 0.0 else totalInterest / totalPayment
}

fun calculateEmi(loanAmount: Double, annualRatePercent: Double, tenure: Double, unit: TimeUnit): EmiResult {
    require(loanAmount > 0.0 && loanAmount.isFinite()) { "Enter a loan amount greater than zero." }
    require(annualRatePercent >= 0.0 && annualRatePercent.isFinite()) { "Rate cannot be negative." }
    require(tenure > 0.0 && tenure.isFinite()) { "Enter a tenure greater than zero." }

    val exactMonths = tenure * if (unit == TimeUnit.YEARS) 12.0 else 1.0
    require(exactMonths % 1.0 == 0.0) { "Loan tenure must resolve to a whole number of months." }
    val months = exactMonths.roundToInt()
    require(months > 0) { "Tenure must be at least one month." }
    val monthlyRate = annualRatePercent / 1200.0
    val payment = if (monthlyRate == 0.0) {
        loanAmount / months
    } else {
        val growth = (1.0 + monthlyRate).pow(months)
        loanAmount * monthlyRate * growth / (growth - 1.0)
    }
    val totalPayment = payment * months
    require(payment.isFinite() && totalPayment.isFinite()) { "These values are too large to calculate." }
    return EmiResult(payment, totalPayment - loanAmount, totalPayment, loanAmount)
}

enum class PercentageMode(val title: String) {
    PERCENT_OF("X% of Y"),
    IS_WHAT_PERCENT("X is what % of Y"),
    INCREASE("Percentage increase"),
    DECREASE("Percentage decrease"),
}

data class PercentageResult(val value: Double, val change: Double? = null)

fun calculatePercentage(mode: PercentageMode, x: Double, y: Double): PercentageResult {
    require(x.isFinite() && y.isFinite()) { "Enter valid numbers." }
    return when (mode) {
        PercentageMode.PERCENT_OF -> checkedPercentage(x * (y / 100.0))
        PercentageMode.IS_WHAT_PERCENT -> {
            require(y != 0.0) { "The base value cannot be zero." }
            checkedPercentage(x / y * 100.0)
        }
        PercentageMode.INCREASE -> {
            require(x >= 0.0 && y >= 0.0) { "Values cannot be negative." }
            checkedPercentage(x * (1.0 + y / 100.0), x * (y / 100.0))
        }
        PercentageMode.DECREASE -> {
            require(x >= 0.0 && y in 0.0..100.0) { "Use a non-negative value and a rate from 0 to 100%." }
            checkedPercentage(x * (1.0 - y / 100.0), x * (y / 100.0))
        }
    }
}

data class DiscountResult(val discountAmount: Double, val finalPrice: Double, val savingsPercent: Double)

fun calculateDiscount(originalPrice: Double, discountPercent: Double): DiscountResult {
    require(originalPrice >= 0.0 && originalPrice.isFinite()) { "Price cannot be negative." }
    require(discountPercent in 0.0..100.0 && discountPercent.isFinite()) { "Discount must be between 0 and 100%." }
    val discountAmount = originalPrice * (discountPercent / 100.0)
    val finalPrice = originalPrice - discountAmount
    require(discountAmount.isFinite() && finalPrice.isFinite()) { "These values are too large to calculate." }
    return DiscountResult(discountAmount, finalPrice, discountPercent)
}

private fun checkedPercentage(value: Double, change: Double? = null): PercentageResult {
    require(value.isFinite() && (change == null || change.isFinite())) { "These values are too large to calculate." }
    return PercentageResult(value, change)
}
