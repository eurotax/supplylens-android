package com.eurotax.supplylens.core.network.token

import kotlinx.coroutines.delay

/**
 * Mock implementation of Token API Service.
 * Returns dummy data for development until real API is available.
 * 
 * Real implementation will use Retrofit + BuildConfig.TOKEN_API_BASE_URL/KEY
 */
class RealTokenService {
    
    suspend fun searchTokens(query: String): List<TokenSearchResult> {
        delay(500) // Simulate network delay
        return listOf(
            TokenSearchResult(
                symbol = "ETH",
                name = "Ethereum",
                chain = "ethereum",
                address = "0x0000000000000000000000000000000000000000",
                price = 2500.0,
                volume24h = 15000000000.0
            ),
            TokenSearchResult(
                symbol = "BTC",
                name = "Bitcoin",
                chain = "bitcoin",
                address = "0x0000000000000000000000000000000000000001",
                price = 45000.0,
                volume24h = 25000000000.0
            )
        )
    }
    
    suspend fun getToken(chain: String, address: String): TokenDetail {
        delay(500)
        return TokenDetail(
            symbol = "ETH",
            name = "Ethereum",
            chain = chain,
            address = address,
            price = 2500.0,
            volume24h = 15000000000.0,
            marketCap = 300000000000.0,
            holders = 120000000,
            liquidityUsd = 5000000000.0
        )
    }
}

data class TokenSearchResult(
    val symbol: String,
    val name: String,
    val chain: String,
    val address: String,
    val price: Double,
    val volume24h: Double
)

data class TokenDetail(
    val symbol: String,
    val name: String,
    val chain: String,
    val address: String,
    val price: Double,
    val volume24h: Double,
    val marketCap: Double?,
    val holders: Int?,
    val liquidityUsd: Double?
)
