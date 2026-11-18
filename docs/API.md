# Exchange-rate API

## Provider

FinCalc uses the public [Frankfurter API v2](https://frankfurter.dev/), an open, keyless HTTPS service that publishes daily reference rates sourced from central banks and other official providers. The app calls a pair endpoint such as:

```text
GET https://api.frankfurter.dev/v2/rate/inr/usd
Accept: application/json
```

The response includes `base`, `quote`, `rate` and `date`. FinCalc multiplies the entered amount by `rate`. Reference rates can be delayed, may not update on holidays and are not executable market quotes.

## Implementation

- `FrankfurterApi` owns the HTTP request, timeouts and response parsing.
- `CurrencyRepository` moves the blocking request to `Dispatchers.IO` and coordinates cache fallback.
- `SettingsStore` stores the last successful quote by currency pair in Preferences DataStore.
- Only a selected currency pair is requested. No credentials or personal information are sent.

## Offline behavior

If a refresh fails, the repository checks the saved pair and then a saved inverse pair. An inverse quote is reciprocated. A fallback is marked `isCached` and the screen displays **Offline · showing saved rate**, the original published rate date and the local time the value was saved. If neither direction has been saved before, the app presents an error and retry action; it does not invent a rate.

The currency list is intentionally curated to commonly used codes. Provider support for any code can change, so a failed or unsupported pair receives the same graceful fallback behavior.
