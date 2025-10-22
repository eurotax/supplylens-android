package com.eurotax.supplylens.core.ai

import android.content.Context
import android.content.SharedPreferences
import com.eurotax.supplylens.core.billing.SubscriptionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiForecastEngine(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "ai_forecast_prefs"
        private const val KEY_LAST_FORECAST_TIMESTAMP = "last_forecast_timestamp"
        private const val FREE_QUOTA_DAYS = 7
        private const val ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages"
        private const val MODEL = "claude-sonnet-4-20250514"
        private const val MAX_TOKENS = 2000
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    fun canGenerateForecast(status: SubscriptionStatus): Boolean {
        return when (status) {
            SubscriptionStatus.FREE -> {
                val lastTs = prefs.getLong(KEY_LAST_FORECAST_TIMESTAMP, 0L)
                val daysSince = (System.currentTimeMillis() - lastTs) / (1000 * 60 * 60 * 24)
                daysSince >= FREE_QUOTA_DAYS
            }
            SubscriptionStatus.TRIAL_PRO, SubscriptionStatus.PRO -> true
            SubscriptionStatus.EXPIRED -> false
        }
    }
    
    fun getDaysUntilNextForecast(): Int {
        val lastTs = prefs.getLong(KEY_LAST_FORECAST_TIMESTAMP, 0L)
        if (lastTs == 0L) return 0
        val daysSince = (System.currentTimeMillis() - lastTs) / (1000 * 60 * 60 * 24)
        val daysRemaining = FREE_QUOTA_DAYS - daysSince.toInt()
        return maxOf(0, daysRemaining)
    }
    
    suspend fun generateForecast(
        tokenSymbol: String,
        tokenName: String = tokenSymbol,
        currentPrice: Double,
        volume24h: Double,
        marketCap: Double? = null,
        holders: Int? = null,
        liquidityUsd: Double? = null
    ): ForecastResult = withContext(Dispatchers.IO) {
        
        val prompt = buildPrompt(tokenSymbol, tokenName, currentPrice, volume24h, marketCap, holders, liquidityUsd)
        val apiKey = BuildConfig.ANTHROPIC_API_KEY
        if (apiKey.isEmpty()) throw ForecastException("API key not configured")
        
        val requestBody = JSONObject().apply {
            put("model", MODEL)
            put("max_tokens", MAX_TOKENS)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }.toString()
        
        val request = Request.Builder()
            .url(ANTHROPIC_API_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()
        
        try {
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw ForecastException("API error: ${response.code} ${response.message}")
            }
            
            val responseBody = response.body?.string() ?: throw ForecastException("Empty response")
            val jsonResponse = JSONObject(responseBody)
            val contentArray = jsonResponse.getJSONArray("content")
            val prediction = if (contentArray.length() > 0) {
                contentArray.getJSONObject(0).getString("text")
            } else {
                throw ForecastException("No content in response")
            }
            
            prefs.edit().putLong(KEY_LAST_FORECAST_TIMESTAMP, System.currentTimeMillis()).apply()
            
            ForecastResult(prediction, System.currentTimeMillis(), tokenSymbol)
            
        } catch (e: Exception) {
            throw ForecastException("Forecast failed: ${e.message}", e)
        }
    }
    
    private fun buildPrompt(
        sym: String, name: String, price: Double, vol: Double,
        cap: Double?, hold: Int?, liq: Double?
    ): String = buildString {
        append("You are an expert cryptocurrency analyst. Analyze this token and provide a price forecast for 7, 30, and 90 days.\n\n")
        append("**Token Information:**\n- Symbol: $sym\n- Name: $name\n- Current Price: \$$price USD\n")
        append("- 24h Volume: \$${fmt(vol)} USD\n")
        if (cap != null) append("- Market Cap: \$${fmt(cap)} USD\n")
        if (hold != null) append("- Holders: ${fmt(hold.toDouble())}\n")
        if (liq != null) append("- Liquidity: \$${fmt(liq)} USD\n")
        append("\n**Instructions:**\n1. Analyze fundamentals\n2. Consider trends, volume/price, liquidity\n")
        append("3. Provide predictions for 7/30/90 days with confidence levels\n4. Highlight risks and opportunities\n")
        append("5. Max 500 words\n\n**Format:**\n**Price Predictions:**\n- 7 Days: [range] (Confidence: [level])\n")
        append("- 30 Days: [range] (Confidence: [level])\n- 90 Days: [range] (Confidence: [level])\n\n")
        append("**Analysis:**\n[2-3 paragraphs]\n\n**Key Risks:**\n- [Risk 1]\n- [Risk 2]\n\n")
        append("**Key Opportunities:**\n- [Opportunity 1]\n- [Opportunity 2]\n")
    }
    
    private fun fmt(v: Double) = when {
        v >= 1e9 -> "%.2fB".format(v / 1e9)
        v >= 1e6 -> "%.2fM".format(v / 1e6)
        v >= 1e3 -> "%.2fK".format(v / 1e3)
        else -> "%.2f".format(v)
    }
}

data class ForecastResult(val prediction: String, val timestamp: Long, val tokenSymbol: String)
class QuotaExceededException(message: String) : Exception(message)
class ForecastException(message: String, cause: Throwable? = null) : Exception(message, cause)
