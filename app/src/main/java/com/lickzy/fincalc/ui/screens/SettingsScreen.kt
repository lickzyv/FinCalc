package com.lickzy.fincalc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lickzy.fincalc.data.local.AppSettings
import com.lickzy.fincalc.data.local.ThemeChoice
import com.lickzy.fincalc.data.local.supportedCurrencyCodes
import com.lickzy.fincalc.ui.components.ChoiceRow
import com.lickzy.fincalc.ui.components.OutlinedAction
import com.lickzy.fincalc.ui.components.PageFrame
import com.lickzy.fincalc.ui.components.PixelLabel
import com.lickzy.fincalc.ui.components.ResultLine

private val currencyNamesForSettings = mapOf(
    "INR" to "Indian rupee", "USD" to "US dollar", "EUR" to "Euro", "GBP" to "British pound",
    "JPY" to "Japanese yen", "CAD" to "Canadian dollar", "AUD" to "Australian dollar",
    "CHF" to "Swiss franc", "CNY" to "Chinese yuan", "SGD" to "Singapore dollar",
    "NZD" to "New Zealand dollar", "HKD" to "Hong Kong dollar",
)

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onBack: () -> Unit,
    onThemeChange: (ThemeChoice) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onCredits: () -> Unit,
) {
    var currencyMenu by remember { mutableStateOf(false) }
    PageFrame("Settings", onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            ChoiceRow(
                options = listOf("System", "Light", "Dark"),
                selected = when (settings.theme) { ThemeChoice.SYSTEM -> "System"; ThemeChoice.LIGHT -> "Light"; ThemeChoice.DARK -> "Dark" },
                onSelect = { choice -> onThemeChange(ThemeChoice.valueOf(choice.uppercase())) },
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Text("Default currency", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Box {
                OutlinedAction(
                    "${settings.defaultCurrency} · ${currencyNamesForSettings[settings.defaultCurrency] ?: settings.defaultCurrency}  ▾",
                    onClick = { currencyMenu = true },
                )
                DropdownMenu(expanded = currencyMenu, onDismissRequest = { currencyMenu = false }) {
                    supportedCurrencyCodes.sorted().forEach { code ->
                        DropdownMenuItem(
                            text = { Text("$code · ${currencyNamesForSettings[code] ?: code}") },
                            onClick = { onCurrencyChange(code); currencyMenu = false },
                        )
                    }
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = CutCornerShape(topStart = 2.dp, topEnd = 16.dp, bottomEnd = 2.dp, bottomStart = 16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PixelLabel("FINCALC", MaterialTheme.colorScheme.secondary)
                ResultLine("Version", "1.0.0")
                ResultLine("Developer", "Anindya Jha")
                ResultLine("GitHub", "Lickzy")
                ResultLine("Package", "com.lickzy.fincalc")
                ResultLine("License", "MIT")
            }
        }
        OutlinedAction("Credits  ↗", onClick = onCredits, modifier = Modifier.fillMaxWidth().padding(top = 2.dp))
    }
}
