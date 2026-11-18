<div align="center">
  <img src="assets/fincalc-logo.svg" alt="FinCalc pixel F with a rising line" width="128" height="128" />
  <h1>F I N C A L C</h1>
  <p><strong>Money, made clearer.</strong><br />A simple financial calculator for everyday money decisions.</p>
  <p><code>PIXEL-BUILT · NUMBER-READY · ANDROID</code></p>
  <p>
    <img src="https://img.shields.io/badge/Kotlin-2.2.20-201D2A?logo=kotlin&logoColor=C7ED4B" alt="Kotlin 2.2.20" />
    <img src="https://img.shields.io/badge/Compose-Material%203-201D2A?logo=jetpackcompose&logoColor=B9AEF4" alt="Jetpack Compose Material 3" />
    <img src="https://img.shields.io/badge/Android-API%2026%2B-F27B4A?logo=android&logoColor=201D2A" alt="Android API 26 and newer" />
    <img src="https://img.shields.io/badge/Version-1.0.0-201D2A?labelColor=B9AEF4" alt="App version 1.0.0" />
    <img src="https://img.shields.io/badge/License-MIT-C7ED4B?labelColor=201D2A" alt="MIT License" />
  </p>
</div>

<p align="center"><code>▰ ▰ ▱ ▰ ▰ ▱ ▰ ▰ ▱ ▰ ▰ ▱ ▰ ▰</code></p>

FinCalc is a pocket-sized set of money tools: quick to open, clear about the numbers, and styled with a little retro-computing character. It runs on Android and keeps everyday calculations close at hand.

## ▸ 01 / The toolbox

| Tool | Put it to work |
| --- | --- |
| **Interest** | Compare simple growth with compounding, from monthly to daily intervals. |
| **Loan EMI** | Estimate the monthly payment, total interest and principal split. |
| **Income tax** | Compare old and new Indian regime estimates for FY 2026–27 / AY 2027–28. |
| **Currency** | Convert popular currencies, search and swap pairs, and view the rate date. |
| **Percentage** | Work out a percentage, share, increase or decrease. |
| **Discount** | See the amount saved and the price after a discount. |

Choose system, light or dark appearance and set a default display currency. The tax calculator is an estimate; it is not a tax filing or a statement of actual liability.

## ▸ 02 / Screenshots

No real-device screenshots are included in this checkout yet. We’ll add captures from the running app here when they’re available; no mockups are presented as app screenshots.

## ▸ 03 / Get it running

### Open in Android Studio

1. Clone the repository and open its folder in Android Studio.
2. Install Android SDK Platform 36 and use JDK 17 (Android Studio’s bundled JDK works).
3. Let Gradle sync, then run the `app` configuration on an Android 8.0+ device or emulator.

### Build from a terminal

On Windows PowerShell:

```powershell
git clone https://github.com/Lickzy/FinCalc.git
cd FinCalc
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

On macOS or Linux, use `./gradlew` in place of `gradlew.bat`.

The debug APK is created at `app/build/outputs/apk/debug/app-debug.apk`. To install it on a connected Android device, run `./gradlew installDebug` (or `.\gradlew.bat installDebug` on Windows).

## ▸ 04 / Under the hood

| Layer | What FinCalc uses |
| --- | --- |
| App | Kotlin 2.2.20, Jetpack Compose, Material 3 |
| Android | Minimum API 26 · target API 36 · JDK 17 |
| State | A small MVVM setup, Kotlin Coroutines and Preferences DataStore |
| Exchange rates | Frankfurter’s public reference-rate service; no API key |
| Tests | JUnit 4 unit tests for financial calculations |

The currency client is kept separate from the Compose screens. It requests only the selected pair, shows the provider’s rate date and retains the last successful pair for an unavailable connection. These are reference rates, not trading quotes; your bank or card provider may use a different rate or add fees. Details live in [the API notes](docs/API.md).

### Project map

```text
app/src/main/java/com/lickzy/fincalc/
├── data/       exchange client, repository and local preferences
├── domain/     financial formulas and tax rules
├── ui/         Compose screens, shared components and theme
├── utils/      locale-aware number and currency formatting
├── MainActivity.kt
└── MainViewModel.kt
docs/           architecture, calculation assumptions and API notes
assets/         scalable FinCalc pixel mark
```

For the screen/state flow and module responsibilities, see [Architecture](docs/ARCHITECTURE.md). Formula assumptions and tax sources are in [Calculations](docs/CALCULATIONS.md).

## ▸ 05 / Exchange rates & privacy

Currency conversion uses daily reference rates from [Frankfurter](https://frankfurter.dev/). FinCalc sends the selected currency codes when refreshing a pair; calculations run on the device. Theme, default currency and the most recent successful rates are kept on-device. The app has no login, advertising, analytics or tracking. Its only requested permission is internet access for currency updates.

## ▸ 06 / Next pixels

- Capture and add real screenshots from a device or emulator.
- Add focused Compose UI tests for the main flows.
- Keep tax assumptions and sources current as official rules change.
- Review accessibility and localization opportunities.

## ▸ 07 / Contribute

Small, focused fixes and improvements are welcome. Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. Keep formulas in the domain layer and include or update unit tests when changing calculation behavior.

## ▸ 08 / Credits & license

FinCalc is designed and developed by **Anindya Jha** ([Lickzy](https://github.com/Lickzy)). Exchange reference data is provided by [Frankfurter](https://frankfurter.dev/). The project is built with Kotlin, AndroidX, Jetpack Compose, Coroutines, Preferences DataStore and JUnit.

Released under the [MIT License](LICENSE). See [CHANGELOG.md](CHANGELOG.md) for project updates.

<p align="center"><code>└── END OF TAPE ── SAVE YOUR CHANGES ── ▪</code></p>
