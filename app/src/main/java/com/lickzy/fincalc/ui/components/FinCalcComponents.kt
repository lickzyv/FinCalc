package com.lickzy.fincalc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.lickzy.fincalc.ui.theme.FinColors

@Composable
fun PageFrame(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(84.dp).padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                modifier = Modifier.size(46.dp).clip(CutCornerShape(4.dp, 14.dp, 4.dp, 14.dp))
                    .clickable(role = Role.Button, onClick = onBack),
                color = MaterialTheme.colorScheme.primary,
                shape = CutCornerShape(4.dp, 14.dp, 4.dp, 14.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("‹", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                PixelLabel("FINCALC  /  TOOLS", MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp).padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
fun FinCalcMark(modifier: Modifier = Modifier, size: Int = 44) {
    Box(
        modifier = modifier.size(size.dp).clip(CutCornerShape(4.dp, 13.dp, 4.dp, 13.dp)).background(FinColors.Ink),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(size.dp * .72f)) {
            val cell = this.size.minDimension / 7f
            val f = listOf("1111100", "1000000", "1000000", "1111000", "1000000", "1000000", "1000000")
            f.forEachIndexed { row, bits ->
                bits.forEachIndexed { column, bit ->
                    if (bit == '1') {
                        drawRect(
                            color = if (row >= 5 && column >= 4) FinColors.Lavender else FinColors.Lime,
                            topLeft = Offset(column * cell, row * cell),
                            size = Size(cell * .86f, cell * .86f),
                        )
                    }
                }
            }
            listOf(4 to 5, 5 to 4, 6 to 3).forEach { (column, row) ->
                drawRect(FinColors.Lavender, Offset(column * cell, row * cell), Size(cell * .86f, cell * .86f))
            }
            drawRect(FinColors.Orange, Offset(cell * 6, 0f), Size(cell * .86f, cell * .86f))
        }
    }
}

@Composable
fun PixelLabel(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = color)
}

@Composable
fun PixelGlyph(index: Int, color: Color, modifier: Modifier = Modifier, size: Int = 44, backdrop: Color = Color.Transparent) {
    val art = pixelArt[index.mod(pixelArt.size)]
    Box(Modifier.size(size.dp).clip(CutCornerShape(2.dp, 9.dp, 2.dp, 9.dp)).background(backdrop).then(modifier)) {
        Canvas(Modifier.fillMaxSize().padding((size * .12f).dp)) {
            val cell = this.size.minDimension / 7f
            art.forEachIndexed { row, line ->
                line.forEachIndexed { column, pixel ->
                    if (pixel == '1') drawRect(color, Offset(column * cell, row * cell), Size(cell * .9f, cell * .9f))
                }
            }
        }
    }
}

private val pixelArt = listOf(
    listOf("1100001", "1100010", "0000100", "0001000", "0010000", "0100011", "1000011"), // percentage
    listOf("0000001", "0000011", "0000011", "0010011", "0011011", "0111011", "0111111"), // growth
    listOf("0000000", "0011100", "0000000", "0111110", "0000000", "0011100", "0000000"), // payment
    listOf("1111110", "1000010", "1011010", "1011010", "1000010", "1111110", "0000000"), // tax
    listOf("1000001", "0100010", "0010100", "0001000", "0010100", "0100010", "1000001"), // discount
    listOf("1000001", "0100010", "0010100", "0001000", "0010100", "0100010", "1000001"), // exchange
    listOf("0000001", "0000011", "0000110", "0001100", "0011000", "0110000", "1100000"), // pixel spark
)

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier, trailing: String? = null) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        if (trailing != null) PixelLabel(trailing, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CalculatorTile(
    symbol: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    tone: Color = MaterialTheme.colorScheme.surface,
    foreground: Color = MaterialTheme.colorScheme.onSurface,
    index: Int = 0,
    wide: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth().height(if (wide) 152.dp else 146.dp).clickable(role = Role.Button, onClick = onClick),
        shape = CutCornerShape(topStart = 3.dp, topEnd = 18.dp, bottomEnd = 3.dp, bottomStart = 18.dp),
        color = tone,
        border = BorderStroke(1.dp, foreground.copy(alpha = .18f)),
    ) {
        if (wide) {
            Row(Modifier.fillMaxSize().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PixelLabel(symbol, foreground.copy(alpha = .72f))
                    Text(title, style = MaterialTheme.typography.headlineSmall, color = foreground)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = foreground.copy(alpha = .78f))
                }
                PixelGlyph(index, foreground, size = 84)
            }
        } else {
            Column(Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                    PixelLabel(symbol, foreground.copy(alpha = .72f))
                    PixelGlyph(index, foreground, size = 43)
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(title, style = MaterialTheme.typography.titleLarge, color = foreground)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = foreground.copy(alpha = .78f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    error: String? = null,
    placeholder: String = "0",
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .65f)) },
        suffix = suffix?.let { { Text(it, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant) } },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = CutCornerShape(topStart = 2.dp, topEnd = 12.dp, bottomEnd = 2.dp, bottomStart = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
fun ChoiceRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val active = option == selected
            Surface(
                modifier = Modifier.weight(1f).height(44.dp).clip(CutCornerShape(3.dp, 10.dp, 3.dp, 10.dp))
                    .selectable(selected = active, role = Role.RadioButton, onClick = { onSelect(option) }),
                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, if (active) MaterialTheme.colorScheme.onPrimary.copy(alpha = .2f) else MaterialTheme.colorScheme.outline),
                shape = CutCornerShape(3.dp, 10.dp, 3.dp, 10.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(option, style = MaterialTheme.typography.labelLarge, color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ActionRow(onCalculate: () -> Unit, onReset: () -> Unit, calculateLabel: String = "Calculate") {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = onCalculate,
            modifier = Modifier.weight(1f).height(54.dp),
            shape = CutCornerShape(topStart = 3.dp, topEnd = 14.dp, bottomEnd = 3.dp, bottomStart = 14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
        ) {
            Text(calculateLabel.uppercase(), style = MaterialTheme.typography.labelLarge)
        }
        TextButton(onClick = onReset, modifier = Modifier.height(50.dp)) { Text("Reset") }
    }
}

@Composable
fun ResultPanel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CutCornerShape(topStart = 2.dp, topEnd = 18.dp, bottomEnd = 2.dp, bottomStart = 18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PixelLabel("RESULT  /  $title", MaterialTheme.colorScheme.secondary)
            content()
        }
    }
}

@Composable
fun ResultLine(label: String, value: String, emphasized: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = if (emphasized) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
            color = if (emphasized) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.SemiBold,
        )
    }
}

@Composable
fun HelperText(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text, modifier = modifier, style = MaterialTheme.typography.bodySmall, color = color)
}

@Composable
fun OutlinedAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clip(CutCornerShape(3.dp, 10.dp, 3.dp, 10.dp)).clickable(role = Role.Button, onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = CutCornerShape(3.dp, 10.dp, 3.dp, 10.dp),
    ) {
        Box(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun SmallSpacer(height: Int = 4) { Spacer(Modifier.height(height.dp)) }
