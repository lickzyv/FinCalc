package com.lickzy.fincalc

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.view.WindowCompat
import com.lickzy.fincalc.data.local.ThemeChoice
import com.lickzy.fincalc.ui.screens.CalculatorScreen
import com.lickzy.fincalc.ui.screens.CurrencyScreen
import com.lickzy.fincalc.ui.screens.CreditsScreen
import com.lickzy.fincalc.ui.screens.HomeScreen
import com.lickzy.fincalc.ui.screens.SettingsScreen
import com.lickzy.fincalc.ui.theme.FinCalcTheme

enum class AppScreen { HOME, INTEREST, EMI, TAX, CURRENCY, PERCENTAGE, DISCOUNT, SETTINGS, CREDITS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FinCalcApp() }
    }
}

@Composable
private fun FinCalcApp(viewModel: MainViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val view = LocalView.current
    val darkTheme = when (settings.theme) {
        ThemeChoice.SYSTEM -> systemDark
        ThemeChoice.LIGHT -> false
        ThemeChoice.DARK -> true
    }
    var screen by rememberSaveable { mutableStateOf(AppScreen.HOME) }
    BackHandler(enabled = screen != AppScreen.HOME) {
        screen = if (screen == AppScreen.CREDITS) AppScreen.SETTINGS else AppScreen.HOME
    }

    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }

    FinCalcTheme(darkTheme = darkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = androidx.compose.material3.MaterialTheme.colorScheme.background) {
            AnimatedContent(
                targetState = screen,
                transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(100)) },
                label = "screen transition",
            ) { target ->
                when (target) {
                    AppScreen.HOME -> {
                        LaunchedEffect(target, settings.defaultCurrency) {
                            viewModel.refreshCurrency(settings.defaultCurrency, if (settings.defaultCurrency == "USD") "EUR" else "USD")
                        }
                        HomeScreen(
                            currency = currency,
                            defaultCurrency = settings.defaultCurrency,
                            onOpen = { screen = it },
                        )
                    }
                    AppScreen.CURRENCY -> CurrencyScreen(
                        settings = settings,
                        currency = currency,
                        onBack = { screen = AppScreen.HOME },
                        onRefresh = viewModel::refreshCurrency,
                    )
                    AppScreen.SETTINGS -> SettingsScreen(
                        settings = settings,
                        onBack = { screen = AppScreen.HOME },
                        onThemeChange = viewModel::setTheme,
                        onCurrencyChange = viewModel::setDefaultCurrency,
                        onCredits = { screen = AppScreen.CREDITS },
                    )
                    AppScreen.CREDITS -> CreditsScreen(onBack = { screen = AppScreen.SETTINGS })
                    else -> CalculatorScreen(
                        screen = target,
                        currencyCode = settings.defaultCurrency,
                        onBack = { screen = AppScreen.HOME },
                    )
                }
            }
        }
    }
}
