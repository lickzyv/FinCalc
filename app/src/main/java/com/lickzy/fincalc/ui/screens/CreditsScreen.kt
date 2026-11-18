package com.lickzy.fincalc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lickzy.fincalc.ui.components.HelperText
import com.lickzy.fincalc.ui.components.PageFrame
import com.lickzy.fincalc.ui.components.PixelGlyph
import com.lickzy.fincalc.ui.components.PixelLabel
import com.lickzy.fincalc.ui.components.ResultLine
import com.lickzy.fincalc.ui.components.ResultPanel

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    PageFrame("Credits", onBack) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PixelGlyph(index = 6, color = MaterialTheme.colorScheme.primary, size = 64)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                PixelLabel("A FINCALC ORIGINAL")
                Text("Made for clearer money decisions.", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        ResultPanel("People") {
            ResultLine("Concept & development", "Anindya Jha")
            ResultLine("Design & development", "Anindya Jha")
        }

        ResultPanel("Built with") {
            HelperText("Kotlin · Jetpack Compose · Material 3")
            HelperText("AndroidX · Coroutines · Preferences DataStore")
            HelperText("JUnit 4")
        }

        ResultPanel("Data & open source") {
            ResultLine("Exchange rates", "Frankfurter")
            HelperText("Thanks to the maintainers of Kotlin, AndroidX, Jetpack Compose, Coroutines, DataStore, JUnit and Frankfurter.")
            ResultLine("License", "MIT")
        }
    }
}
