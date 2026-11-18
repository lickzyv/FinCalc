package com.lickzy.fincalc.data.api

import com.lickzy.fincalc.data.repository.RateQuote
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** A small keyless client for Frankfurter's public reference-rate endpoint. */
class FrankfurterApi {
    fun fetchRate(base: String, quote: String): RateQuote {
        val url = URL("https://api.frankfurter.dev/v2/rate/${base.lowercase()}/${quote.lowercase()}")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 7_000
            readTimeout = 7_000
            setRequestProperty("Accept", "application/json")
        }
        try {
            val status = connection.responseCode
            if (status !in 200..299) throw IllegalStateException("Exchange service returned HTTP $status.")
            val payload = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(payload)
            val returnedRate = json.getDouble("rate")
            require(returnedRate > 0.0 && returnedRate.isFinite()) { "Exchange service returned an invalid rate." }
            return RateQuote(
                base = json.getString("base").uppercase(),
                quote = json.getString("quote").uppercase(),
                rate = returnedRate,
                rateDate = json.getString("date"),
                fetchedAtMillis = System.currentTimeMillis(),
                isCached = false,
            )
        } finally {
            connection.disconnect()
        }
    }
}
