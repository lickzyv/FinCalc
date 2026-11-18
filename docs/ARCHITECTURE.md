# Architecture

FinCalc uses a small MVVM arrangement so the UI stays readable without creating layers the app does not need.

```text
Compose screens ──> MainViewModel ──> CurrencyRepository ──> FrankfurterApi
       │                    │                   │
       └── pure calculators └── SettingsStore (Preferences DataStore)
```

## Responsibilities

- `MainActivity.kt` owns the single activity and a small screen switch. The animated transition is intentionally lightweight.
- `MainViewModel.kt` exposes settings and exchange state as `StateFlow` and launches refreshes in `viewModelScope`.
- `ui/screens/` contains the home, calculator, currency and settings screens. Form inputs live with their screen and remain easy to follow.
- `ui/components/` holds shared input, result and page elements. `ui/theme/` defines the color schemes and typography.
- `domain/calculator/FinancialCalculators.kt` contains pure interest, EMI, percentage and discount functions with input checks.
- `domain/calculator/TaxRules2026.kt` contains the tax bands and the tax estimator for the selected tax year.
- `data/api/FrankfurterApi.kt` contains the small HTTP client. `data/repository/CurrencyRepository.kt` applies the offline fallback. `data/local/SettingsStore.kt` persists preferences and rates.
- `utils/Formatting.kt` centralizes locale-aware currency formatting and input parsing.

## Data flow

Calculator forms parse values, call a domain function, and render the returned result. They do not call Android services. Currency refresh runs on the IO dispatcher; successful values are saved to DataStore. On an HTTP or connectivity error, the repository looks for the selected pair or its inverse and marks that value cached. If there is no saved value, the UI shows an error and retry action.

Settings are collected from DataStore and applied at the app root. Theme defaults to the system and calculator amounts use the saved default currency. Tax always uses INR because the supported tax model is Indian.

## Keeping it maintainable

There is no general-purpose navigation framework or dependency injection framework. Add one only if the app's screen or data needs make the current direct wiring hard to understand. Financial formulas should stay pure and be covered by local JUnit tests.
