package com.lickzy.fincalc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.lickzy.fincalc.CurrencyUiState
import com.lickzy.fincalc.data.local.AppSettings
import com.lickzy.fincalc.data.local.supportedCurrencyCodes
import com.lickzy.fincalc.data.repository.convertCurrencyAmount
import com.lickzy.fincalc.ui.components.HelperText
import com.lickzy.fincalc.ui.components.InputField
import com.lickzy.fincalc.ui.components.OutlinedAction
import com.lickzy.fincalc.ui.components.PageFrame
import com.lickzy.fincalc.ui.components.ResultLine
import com.lickzy.fincalc.ui.components.ResultPanel
import com.lickzy.fincalc.utils.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val currencyNames = mapOf(
    "INR" to "Indian rupee", "USD" to "US dollar", "EUR" to "Euro", "GBP" to "British pound",
    "JPY" to "Japanese yen", "CAD" to "Canadian dollar", "AUD" to "Australian dollar",
    "CHF" to "Swiss franc", "CNY" to "Chinese yuan", "SGD" to "Singapore dollar",
    "NZD" to "New Zealand dollar", "HKD" to "Hong Kong dollar",
)

@Composable
fun CurrencyScreen(
    settings: AppSettings,
    currency: CurrencyUiState,
    onBack: () -> Unit,
    onRefresh: (String, String) -> Unit,
) {
    var from by rememberSaveable { mutableStateOf(settings.defaultCurrency) }
    var to by rememberSaveable { mutableStateOf(if (settings.defaultCurrency == "USD") "EUR" else "USD") }
    var amountText by rememberSaveable { mutableStateOf("1") }
    var picker by remember { mutableStateOf(false) }
    var selectingBase by remember { mutableStateOf(true) }
    LaunchedEffect(from, to) { onRefresh(from, to) }
    val quote = currency.rate?.takeIf { currency.base == from && currency.quote == to }
    val amount = amountText.trim().replace(",", "").toDoubleOrNull()?.takeIf { it >= 0 && it.isFinite() }
    val invalidAmount = amount == null

    PageFrame("Currency converter", onBack) {
        HelperText("Indicative exchange rates")
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CurrencyChoice("From", from, Modifier.weight(1f)) { selectingBase = true; picker = true }
            Surface(
                modifier = Modifier.size(42.dp).clip(CutCornerShape(3.dp, 12.dp, 3.dp, 12.dp)).clickable(role = Role.Button) {
                    val oldFrom = from
                    from = to
                    to = oldFrom
                }.semantics { contentDescription = "Swap currencies" },
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CutCornerShape(3.dp, 12.dp, 3.dp, 12.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("↕", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            CurrencyChoice("To", to, Modifier.weight(1f)) { selectingBase = false; picker = true }
        }
        InputField(
            value = amountText,
            onValueChange = { amountText = it },
            label = "Amount in $from",
            suffix = from,
            placeholder = "1.00",
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = CutCornerShape(topStart = 3.dp, topEnd = 18.dp, bottomEnd = 3.dp, bottomStart = 18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                HelperText("CONVERTED AMOUNT", color = MaterialTheme.colorScheme.primary)
                val converted = if (quote != null && amount != null) runCatching { convertCurrencyAmount(amount, quote.rate) }.getOrNull() else null
                Text(
                    converted?.let { formatCurrency(it, to) } ?: when {
                        invalidAmount -> "Enter a valid amount"
                        currency.isLoading || currency.base != from || currency.quote != to -> "Loading rate…"
                        else -> "Rate unavailable"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                if (quote != null) {
                    HelperText("1 $from = ${formatCurrency(quote.rate, to, 4)}")
                    if (from != to) HelperText("Rate date ${quote.rateDate} · updated ${formatTimestamp(quote.fetchedAtMillis)}")
                }
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            when {
                currency.isLoading && currency.base == from && currency.quote == to -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(Modifier.size(17.dp), strokeWidth = 2.dp)
                    HelperText("Updating reference rate…")
                }
                quote?.isCached == true -> HelperText("Rate dated ${quote.rateDate}", color = MaterialTheme.colorScheme.primary)
                from == to && quote != null -> HelperText("Same currency · no conversion needed")
                quote != null -> HelperText("Published reference rate")
                else -> HelperText("Rates unavailable. Try again.", color = MaterialTheme.colorScheme.error)
            }
            OutlinedAction("Refresh ↻", onClick = { onRefresh(from, to) })
        }
        if (invalidAmount) HelperText("Amount must be a valid, non-negative number.", color = MaterialTheme.colorScheme.error)
        if (currency.error != null && quote == null) {
            TextButton(onClick = { onRefresh(from, to) }) { Text("Try again") }
        }
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = CutCornerShape(3.dp, 14.dp, 3.dp, 14.dp)) {
            HelperText(
                "Indicative rates can differ from those used by your bank or card provider.",
                Modifier.padding(14.dp),
            )
        }
    }

    if (picker) {
        CurrencyPicker(
            selected = if (selectingBase) from else to,
            onDismiss = { picker = false },
            onSelect = { code ->
                if (selectingBase) from = code else to = code
                picker = false
            },
        )
    }
}

@Composable
private fun CurrencyChoice(label: String, code: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(70.dp).clip(CutCornerShape(3.dp, 14.dp, 3.dp, 14.dp)).clickable(role = Role.Button, onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = CutCornerShape(3.dp, 14.dp, 3.dp, 14.dp),
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 9.dp), verticalArrangement = Arrangement.Center) {
            HelperText(label)
            Text("$code  ▾", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun CurrencyPicker(selected: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val matches = supportedCurrencyCodes.sorted().filter { code ->
        val label = currencyNames[code].orEmpty()
        code.contains(query, ignoreCase = true) || label.contains(query, ignoreCase = true)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose currency") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search name or code") },
                    singleLine = true,
                )
                Column(Modifier.height(350.dp).verticalScroll(rememberScrollState())) {
                    matches.forEach { code ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().clip(CutCornerShape(2.dp, 9.dp, 2.dp, 9.dp)).clickable { onSelect(code) },
                            color = if (code == selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            shape = CutCornerShape(2.dp, 9.dp, 2.dp, 9.dp),
                        ) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 11.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(currencyNames[code] ?: code, color = MaterialTheme.colorScheme.onSurface)
                                Text(code, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

private fun formatTimestamp(timestamp: Long): String =
    SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(timestamp))
