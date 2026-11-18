package com.lickzy.fincalc.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lickzy.fincalc.AppScreen
import com.lickzy.fincalc.CurrencyUiState
import com.lickzy.fincalc.ui.components.CalculatorTile
import com.lickzy.fincalc.ui.components.FinCalcMark
import com.lickzy.fincalc.ui.components.PixelGlyph
import com.lickzy.fincalc.ui.components.PixelLabel
import com.lickzy.fincalc.ui.components.SectionTitle
import com.lickzy.fincalc.ui.theme.FinColors
import com.lickzy.fincalc.utils.formatNumber

@Composable
fun HomeScreen(currency: CurrencyUiState, defaultCurrency: String, onOpen: (AppScreen) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 22.dp).padding(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FinCalcMark(size = 48)
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("FinCalc", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                PixelLabel("MONEY TOOLS", MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                modifier = Modifier.size(44.dp).clip(CutCornerShape(3.dp, 12.dp, 3.dp, 12.dp))
                    .clickable(role = Role.Button) { onOpen(AppScreen.SETTINGS) }
                    .semantics { contentDescription = "Settings" },
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = CutCornerShape(3.dp, 12.dp, 3.dp, 12.dp),
            ) { Box(contentAlignment = Alignment.Center) { Text("≡", style = MaterialTheme.typography.titleLarge) } }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = FinColors.Ink,
            shape = CutCornerShape(topStart = 3.dp, topEnd = 28.dp, bottomEnd = 3.dp, bottomStart = 28.dp),
        ) {
            Row(Modifier.fillMaxWidth().height(176.dp).padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PixelLabel("MAKE IT ADD UP", FinColors.Lime)
                    Text("Money,\nmade clearer.", style = MaterialTheme.typography.headlineMedium, color = FinColors.CreamBright)
                    Text("Six tools for everyday decisions.", style = MaterialTheme.typography.bodySmall, color = FinColors.Cream.copy(alpha = .8f))
                }
                PixelGlyph(index = 6, color = FinColors.Lime, size = 104, backdrop = FinColors.InkSoft)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionTitle("Exchange", trailing = "CURRENCY")
            val quote = currency.rate
            Surface(
                modifier = Modifier.fillMaxWidth().clip(CutCornerShape(3.dp, 20.dp, 3.dp, 20.dp))
                    .clickable(role = Role.Button) { onOpen(AppScreen.CURRENCY) },
                color = FinColors.Lavender,
                shape = CutCornerShape(3.dp, 20.dp, 3.dp, 20.dp),
            ) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Currency converter", style = MaterialTheme.typography.titleMedium, color = FinColors.Ink)
                        if (quote != null) {
                            Text("1 ${quote.base}  →  ${quote.quote} ${formatNumber(quote.rate, 4)}", style = MaterialTheme.typography.titleLarge, color = FinColors.Ink, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("Rate date · ${quote.rateDate}", style = MaterialTheme.typography.bodySmall, color = FinColors.Ink.copy(alpha = .76f))
                        } else {
                            Text(if (currency.isLoading) "Getting a rate…" else "Convert $defaultCurrency and more", style = MaterialTheme.typography.bodyMedium, color = FinColors.Ink)
                            if (currency.error != null) Text("Rates unavailable · tap to try again", style = MaterialTheme.typography.bodySmall, color = FinColors.Ink)
                        }
                    }
                    PixelGlyph(index = 5, color = FinColors.Ink, size = 54)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("Calculators", trailing = "5 TOOLS")
            val items = listOf(
                TileData("GROWTH", "Interest", "Simple & compound", AppScreen.INTEREST, FinColors.Lime, FinColors.Ink, 1),
                TileData("LOANS", "Loan EMI", "Monthly repayment", AppScreen.EMI, FinColors.Lavender, FinColors.Ink, 2),
                TileData("INDIA · FY 26–27", "Income tax", "Old & new regimes", AppScreen.TAX, FinColors.Orange, FinColors.Ink, 3),
                TileData("QUICK MATH", "Percentage", "Four calculations", AppScreen.PERCENTAGE, FinColors.Pink, FinColors.Ink, 0),
                TileData("SHOPPING", "Discount", "Price & savings", AppScreen.DISCOUNT, FinColors.InkSoft, FinColors.CreamBright, 4),
            )
            val columns = if (androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 700) 3 else 2
            items.chunked(columns).forEach { rowItems ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { item ->
                        CalculatorTile(
                            symbol = item.symbol, title = item.title, subtitle = item.subtitle,
                            modifier = Modifier.weight(1f), tone = item.tone, foreground = item.foreground,
                            index = item.glyph, onClick = { onOpen(item.destination) },
                        )
                    }
                    if (rowItems.size < columns) Spacer(Modifier.weight((columns - rowItems.size).toFloat()))
                }
            }
        }
    }
}

private data class TileData(
    val symbol: String, val title: String, val subtitle: String,
    val destination: AppScreen, val tone: androidx.compose.ui.graphics.Color,
    val foreground: androidx.compose.ui.graphics.Color, val glyph: Int,
)
