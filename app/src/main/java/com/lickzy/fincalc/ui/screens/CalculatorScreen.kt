package com.lickzy.fincalc.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lickzy.fincalc.AppScreen
import com.lickzy.fincalc.domain.calculator.CompoundingFrequency
import com.lickzy.fincalc.domain.calculator.DiscountResult
import com.lickzy.fincalc.domain.calculator.EmiResult
import com.lickzy.fincalc.domain.calculator.InterestResult
import com.lickzy.fincalc.domain.calculator.PercentageMode
import com.lickzy.fincalc.domain.calculator.PercentageResult
import com.lickzy.fincalc.domain.calculator.TaxEstimate
import com.lickzy.fincalc.domain.calculator.TaxRegime
import com.lickzy.fincalc.domain.calculator.TimeUnit
import com.lickzy.fincalc.domain.calculator.calculateDiscount
import com.lickzy.fincalc.domain.calculator.calculateEmi
import com.lickzy.fincalc.domain.calculator.calculatePercentage
import com.lickzy.fincalc.domain.calculator.compoundInterest
import com.lickzy.fincalc.domain.calculator.estimateIndianIncomeTax
import com.lickzy.fincalc.domain.calculator.simpleInterest
import com.lickzy.fincalc.ui.components.ActionRow
import com.lickzy.fincalc.ui.components.ChoiceRow
import com.lickzy.fincalc.ui.components.HelperText
import com.lickzy.fincalc.ui.components.InputField
import com.lickzy.fincalc.ui.components.OutlinedAction
import com.lickzy.fincalc.ui.components.PageFrame
import com.lickzy.fincalc.ui.components.PixelLabel
import com.lickzy.fincalc.ui.components.ResultLine
import com.lickzy.fincalc.ui.components.ResultPanel
import com.lickzy.fincalc.utils.formatCurrency
import com.lickzy.fincalc.utils.formatNumber

@Composable
fun CalculatorScreen(screen: AppScreen, currencyCode: String, onBack: () -> Unit) {
    when (screen) {
        AppScreen.INTEREST -> InterestScreen(currencyCode, onBack)
        AppScreen.EMI -> EmiScreen(currencyCode, onBack)
        AppScreen.TAX -> TaxScreen(onBack)
        AppScreen.PERCENTAGE -> PercentageScreen(onBack)
        AppScreen.DISCOUNT -> DiscountScreen(currencyCode, onBack)
        else -> InterestScreen(currencyCode, onBack)
    }
}

@Composable
private fun InterestScreen(currency: String, onBack: () -> Unit) {
    var mode by rememberSaveable { mutableStateOf("Simple") }
    var principal by rememberSaveable { mutableStateOf("") }
    var rate by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }
    var unitLabel by rememberSaveable { mutableStateOf("Years") }
    var frequency by rememberSaveable { mutableStateOf(CompoundingFrequency.ANNUALLY) }
    var result by remember { mutableStateOf<InterestResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    PageFrame("Interest calculator", onBack) {
        HelperText("Compare simple growth with interest that compounds over time.")
        ChoiceRow(listOf("Simple", "Compound"), mode) { mode = it; result = null }
        InputField(principal, { principal = it; result = null; error = null }, "Principal amount", suffix = currency)
        InputField(rate, { rate = it; result = null; error = null }, "Annual interest rate", suffix = "%")
        InputField(time, { time = it; result = null; error = null }, "Time period", suffix = unitLabel.lowercase())
        ChoiceRow(listOf("Months", "Years"), unitLabel) { unitLabel = it; result = null }
        if (mode == "Compound") FrequencyField(frequency.label, { frequency = it; result = null })
        error?.let { HelperText(it, color = MaterialTheme.colorScheme.error) }
        ActionRow(
            onCalculate = {
                val p = number(principal)
                val r = number(rate, allowZero = true)
                val t = number(time)
                if (p == null || r == null || t == null) {
                    result = null
                    error = "Enter a positive principal and time, and a non-negative rate."
                } else {
                    runCatching {
                        if (mode == "Simple") simpleInterest(p, r, t, timeUnit(unitLabel))
                        else compoundInterest(p, r, t, timeUnit(unitLabel), frequency)
                    }.onSuccess { result = it; error = null }
                        .onFailure { result = null; error = it.message ?: "Check the values and try again." }
                }
            },
            onReset = { principal = ""; rate = ""; time = ""; result = null; error = null; mode = "Simple"; unitLabel = "Years"; frequency = CompoundingFrequency.ANNUALLY },
        )
        AnimatedVisibility(result != null) {
            result?.let { calculated ->
                ResultPanel("Your ${mode.lowercase()} interest") {
                    ResultLine("Interest earned", formatCurrency(calculated.interest, currency))
                    ResultLine("Principal", formatCurrency(number(principal) ?: 0.0, currency))
                    ResultLine("Final amount", formatCurrency(calculated.finalAmount, currency), emphasized = true)
                }
            }
        }
    }
}

@Composable
private fun EmiScreen(currency: String, onBack: () -> Unit) {
    var loan by rememberSaveable { mutableStateOf("") }
    var rate by rememberSaveable { mutableStateOf("") }
    var tenure by rememberSaveable { mutableStateOf("") }
    var unitLabel by rememberSaveable { mutableStateOf("Years") }
    var result by remember { mutableStateOf<EmiResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    PageFrame("EMI calculator", onBack) {
        HelperText("See your monthly instalment and the total cost of a loan.")
        InputField(loan, { loan = it; result = null; error = null }, "Loan amount", suffix = currency)
        InputField(rate, { rate = it; result = null; error = null }, "Annual interest rate", suffix = "%")
        InputField(tenure, { tenure = it; result = null; error = null }, "Loan tenure", suffix = unitLabel.lowercase())
        ChoiceRow(listOf("Months", "Years"), unitLabel) { unitLabel = it; result = null }
        error?.let { HelperText(it, color = MaterialTheme.colorScheme.error) }
        ActionRow(
            onCalculate = {
                val amount = number(loan)
                val annualRate = number(rate, allowZero = true)
                val duration = number(tenure)
                if (amount == null || annualRate == null || duration == null || (unitLabel == "Months" && duration < 1.0)) {
                    result = null
                    error = "Enter a loan amount, a non-negative rate and a tenure of at least one month."
                } else runCatching { calculateEmi(amount, annualRate, duration, timeUnit(unitLabel)) }
                    .onSuccess { result = it; error = null }
                    .onFailure { result = null; error = it.message ?: "Check the values and try again." }
            },
            onReset = { loan = ""; rate = ""; tenure = ""; result = null; error = null; unitLabel = "Years" },
        )
        AnimatedVisibility(result != null) {
            result?.let { calculated ->
                ResultPanel("Repayment summary") {
                    ResultLine("Monthly EMI", formatCurrency(calculated.monthlyPayment, currency), emphasized = true)
                    ResultLine("Principal", formatCurrency(calculated.principal, currency))
                    ResultLine("Total interest", formatCurrency(calculated.totalInterest, currency))
                    ResultLine("Total payment", formatCurrency(calculated.totalPayment, currency), emphasized = true)
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        HelperText("Principal vs interest")
                        Row(Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                            val principalWeight = (calculated.principal / calculated.totalPayment).coerceIn(.01, 1.0)
                            Box(Modifier.weight(principalWeight.toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                            if (principalWeight < 1.0) Box(Modifier.weight((1.0 - principalWeight).toFloat()).fillMaxHeight().background(MaterialTheme.colorScheme.outline))
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            HelperText("Principal ${formatNumber(100 * calculated.principal / calculated.totalPayment, 1)}%")
                            HelperText("Interest ${formatNumber(100 * calculated.totalInterest / calculated.totalPayment, 1)}%")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxScreen(onBack: () -> Unit) {
    var income by rememberSaveable { mutableStateOf("") }
    var estimates by remember { mutableStateOf<Pair<TaxEstimate, TaxEstimate>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    PageFrame("Income tax", onBack) {
        PixelLabel("FY 2026–27  /  AY 2027–28", MaterialTheme.colorScheme.primary)
        HelperText("Compare estimated tax under both regimes.")
        InputField(income, { income = it; estimates = null; error = null }, "Taxable income", suffix = "₹")
        error?.let { HelperText(it, color = MaterialTheme.colorScheme.error) }
        ActionRow(
            onCalculate = {
                val amount = number(income, allowZero = true)
                if (amount == null) {
                    estimates = null
                    error = "Enter a valid non-negative annual income."
                } else {
                    estimates = estimateIndianIncomeTax(amount, TaxRegime.OLD) to estimateIndianIncomeTax(amount, TaxRegime.NEW)
                    error = null
                }
            },
            onReset = { income = ""; estimates = null; error = null },
        )
        Text("Estimated tax · individual comparison", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        estimates?.let { (old, new) ->
            TaxResultCard(old)
            TaxResultCard(new)
        } ?: HelperText("Results include applicable rebate, surcharge and 4% cess once you calculate.")
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.CutCornerShape(3.dp, 14.dp, 3.dp, 14.dp)) {
            HelperText(
                "Estimate only for a resident under 60 with ordinary income. Verify current rules before filing.",
                Modifier.padding(14.dp),
            )
        }
    }
}

@Composable
private fun TaxResultCard(estimate: TaxEstimate) {
    ResultPanel(estimate.regime.displayName) {
        ResultLine("Tax on slabs", formatCurrency(estimate.slabTax, "INR"))
        ResultLine("Rebate / marginal relief", "−${formatCurrency(estimate.rebateAndRelief, "INR")}")
        ResultLine("Surcharge", formatCurrency(estimate.surcharge, "INR"))
        ResultLine("Health & education cess", formatCurrency(estimate.cess, "INR"))
        ResultLine("Estimated tax", formatCurrency(estimate.estimatedTax, "INR"), emphasized = true)
        HelperText("Effective rate ${formatNumber(estimate.effectiveRate * 100.0, 2)}%")
    }
}

@Composable
private fun PercentageScreen(onBack: () -> Unit) {
    var mode by rememberSaveable { mutableStateOf(PercentageMode.PERCENT_OF) }
    var x by rememberSaveable { mutableStateOf("") }
    var y by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<PercentageResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    PageFrame("Percentage", onBack) {
        HelperText("Four common percentage calculations, in one place.")
        PercentageModeField(mode.title) { selected -> mode = selected; result = null }
        val firstLabel = when (mode) {
            PercentageMode.PERCENT_OF -> "X — percentage"
            PercentageMode.IS_WHAT_PERCENT -> "X — value"
            PercentageMode.INCREASE -> "Starting value"
            PercentageMode.DECREASE -> "Original amount"
        }
        val secondLabel = when (mode) {
            PercentageMode.PERCENT_OF -> "Y — amount"
            PercentageMode.IS_WHAT_PERCENT -> "Y — base value"
            PercentageMode.INCREASE -> "Increase by"
            PercentageMode.DECREASE -> "Decrease by"
        }
        InputField(x, { x = it; result = null; error = null }, firstLabel, suffix = if (mode == PercentageMode.PERCENT_OF) "%" else null)
        InputField(y, { y = it; result = null; error = null }, secondLabel, suffix = if (mode == PercentageMode.PERCENT_OF) null else if (mode == PercentageMode.IS_WHAT_PERCENT) null else "%")
        error?.let { HelperText(it, color = MaterialTheme.colorScheme.error) }
        ActionRow(
            onCalculate = {
                val first = number(x, allowZero = true)
                val second = number(y, allowZero = true)
                if (first == null || second == null) {
                    result = null
                    error = "Enter both values."
                } else runCatching { calculatePercentage(mode, first, second) }
                    .onSuccess { result = it; error = null }
                    .onFailure { result = null; error = it.message ?: "Check the values and try again." }
            },
            onReset = { x = ""; y = ""; result = null; error = null },
        )
        AnimatedVisibility(result != null) {
            result?.let { calculated ->
                ResultPanel("${mode.title} result") {
                    ResultLine("Result", formatNumber(calculated.value, 2), emphasized = true)
                    calculated.change?.let { ResultLine(if (mode == PercentageMode.INCREASE) "Amount added" else "Amount reduced", formatNumber(it, 2)) }
                }
            }
        }
    }
}

@Composable
private fun DiscountScreen(currency: String, onBack: () -> Unit) {
    var price by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<DiscountResult?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    PageFrame("Discount", onBack) {
        HelperText("See the price after a discount and how much you keep.")
        InputField(price, { price = it; result = null; error = null }, "Original price", suffix = currency)
        InputField(discount, { discount = it; result = null; error = null }, "Discount", suffix = "%")
        error?.let { HelperText(it, color = MaterialTheme.colorScheme.error) }
        ActionRow(
            onCalculate = {
                val original = number(price, allowZero = true)
                val percent = number(discount, allowZero = true)
                if (original == null || percent == null || percent > 100.0) {
                    result = null
                    error = "Enter a price and a discount from 0 to 100%."
                } else runCatching { calculateDiscount(original, percent) }
                    .onSuccess { result = it; error = null }
                    .onFailure { result = null; error = it.message ?: "Check the values and try again." }
            },
            onReset = { price = ""; discount = ""; result = null; error = null },
        )
        AnimatedVisibility(result != null) {
            result?.let { calculated ->
                ResultPanel("Price summary") {
                    ResultLine("Original price", formatCurrency(number(price) ?: 0.0, currency))
                    ResultLine("You save", formatCurrency(calculated.discountAmount, currency))
                    ResultLine("Savings", "${formatNumber(calculated.savingsPercent, 2)}%")
                    ResultLine("Final price", formatCurrency(calculated.finalPrice, currency), emphasized = true)
                }
            }
        }
    }
}

@Composable
private fun FrequencyField(value: String, onSelect: (CompoundingFrequency) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedAction("Compounding: $value  ▾", onClick = { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CompoundingFrequency.entries.forEach { frequency ->
                DropdownMenuItem(text = { Text(frequency.label) }, onClick = { onSelect(frequency); expanded = false })
            }
        }
    }
}

@Composable
private fun PercentageModeField(value: String, onSelect: (PercentageMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedAction("Calculation: $value  ▾", onClick = { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PercentageMode.entries.forEach { mode ->
                DropdownMenuItem(text = { Text(mode.title) }, onClick = { onSelect(mode); expanded = false })
            }
        }
    }
}

private fun number(value: String, allowZero: Boolean = false): Double? {
    val parsed = value.trim().replace(",", "").toDoubleOrNull() ?: return null
    if (!parsed.isFinite()) return null
    return parsed.takeIf { if (allowZero) it >= 0.0 else it > 0.0 }
}

private fun timeUnit(label: String) = if (label == "Months") TimeUnit.MONTHS else TimeUnit.YEARS
