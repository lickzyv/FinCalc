# Contributing to FinCalc

Thanks for helping keep FinCalc useful and easy to learn from.

## Before you start

- Search existing issues before opening a new one.
- For a larger change, open an issue first so the scope is clear.
- Keep each pull request focused and avoid adding dependencies without a concrete need.

## Development

1. Open the project in Android Studio with JDK 17 and Android SDK Platform 36 installed.
2. Use the existing package and keep business rules out of composables.
3. Add or update JUnit tests for any changed calculation or parsing rule.
4. Run `./gradlew testDebugUnitTest` and `./gradlew assembleDebug` before submitting.
5. Describe the change, relevant assumptions, and how you checked it in the pull request.

On Windows, use `gradlew.bat` in place of `./gradlew`.

## Code style

- Prefer descriptive names and small functions.
- Keep Compose UI state close to the screen that uses it; persist only real preferences.
- Put tax-year values in `TaxRules2026.kt` and cite the official source when changing them.
- Never include API secrets, user data or generated build output in a commit.
- Keep accessibility labels and error messages understandable.

## Reporting a bug

Include the Android version, device or emulator, steps to reproduce, expected behavior and actual behavior. Remove personal or financial information from screenshots and logs.

By contributing, you agree that your contributions are provided under the MIT License.
